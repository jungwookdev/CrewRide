import sqlite3
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from datetime import datetime

from sklearn.preprocessing import StandardScaler
from sklearn.manifold import TSNE
from sklearn.cluster import KMeans

import tensorflow as tf
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, Dense, Layer
from tensorflow.keras.optimizers import Adam
from tensorflow.keras import backend as K

# =============================================================================
# 1. Connect to the SQLite Database and Load Data
# =============================================================================
db_path = 'carpool_employees.db'
conn = sqlite3.connect(db_path)

# Load data from the "employees" table.
df = pd.read_sql_query("SELECT * FROM employees", conn)
print("Data preview:")
print(df.head())

# =============================================================================
# 2. Define the Relevant Features
# =============================================================================
features = [
    # --- Personal Preferences ---
    'music_preference',                   # preferred music in carpool
    'talk_preferences',                   # type of conversation preferred
    'conversation_topics',                # topics they enjoy discussing
    'co_rider_gender_preference',         # any gender restrictions
    'preferred_communication',            # chatty/quiet/etc.
    'allergies_pet_peeves',               # potential ride discomforts
    'languages_spoken',                   # communication compatibility

    # --- Commute Behavior ---
    'work_hours_start',                   # commute schedule start time
    'work_hours_end',                     # commute schedule end time

    # --- Carpool Readiness ---
    'comfort_level',                      # ride comfort
    'fuel_type',                          # sustainability signal
    'carpool_insurance_coverage',         # readiness/legal safety
    'preferred_carpool_group_size',       # how many co-riders they want
    'preferred_pickup_time_to_goto_work',      # morning pickup routine
    'preferred_dropoff_time_to_goto_work',     # morning dropoff routine
    'preferred_pickup_time_to_leave_from_work', # evening pickup routine
    'preferred_dropoff_time_to_leave_from_work' # evening dropoff routine
]

# Check that all required columns exist.
missing_cols = [col for col in features if col not in df.columns]
if missing_cols:
    raise ValueError("The following required columns are missing from the database: " + ", ".join(missing_cols))

# =============================================================================
# 3. Preprocessing: Convert Time Strings and Encode Categorical Variables
# =============================================================================
# Helper function to convert HH:MM:SS (or HH:MM) time strings into minutes since midnight.
def convert_time_to_minutes(t):
    try:
        # Try full time format first.
        t_obj = datetime.strptime(t, '%H:%M:%S').time()
    except Exception:
        try:
            # Try shorter format.
            t_obj = datetime.strptime(t, '%H:%M').time()
        except Exception:
            return np.nan
    return t_obj.hour * 60 + t_obj.minute

# List of columns that represent time values.
time_columns = [
    'work_hours_start',
    'work_hours_end',
    'preferred_pickup_time_to_goto_work',
    'preferred_dropoff_time_to_goto_work',
    'preferred_pickup_time_to_leave_from_work',
    'preferred_dropoff_time_to_leave_from_work'
]

# Copy the selected features.
data = df[features].copy()

# Convert time columns from string to numerical minutes.
for col in time_columns:
    if data[col].dtype == object:
        data[col] = data[col].apply(convert_time_to_minutes)

# For simplicity, we convert all remaining non-numeric columns to categorical dummy variables.
# Identify numeric columns (including the converted time columns) and treat the rest as categorical.
numeric_cols = data.select_dtypes(include=['int64', 'float64']).columns.tolist()
categorical_cols = [col for col in data.columns if col not in numeric_cols]

# One-hot encode the categorical columns.
if categorical_cols:
    data_encoded = pd.get_dummies(data, columns=categorical_cols, dummy_na=True)
else:
    data_encoded = data.copy()

# Drop any rows with missing values (if any remain after conversion).
data_encoded.dropna(inplace=True)
data_encoded.reset_index(drop=True, inplace=True)
# Also, align the original dataframe for later DB updates.
df_aligned = df.loc[data_encoded.index].reset_index(drop=True)

print("\nPreprocessed data shape:", data_encoded.shape)

# =============================================================================
# 4. Scale the Features
# =============================================================================
scaler = StandardScaler()
data_scaled = scaler.fit_transform(data_encoded)

# =============================================================================
# 5. Build and Train an Autoencoder for Latent Feature Extraction
# =============================================================================
input_dim = data_scaled.shape[1]
latent_dim = 10  # Choose a latent dimension that captures key patterns

# Define the autoencoder architecture.
input_layer = Input(shape=(input_dim,))
encoder = Dense(128, activation='relu')(input_layer)
encoder = Dense(64, activation='relu')(encoder)
latent = Dense(latent_dim, activation='relu', name='latent')(encoder)

decoder = Dense(64, activation='relu')(latent)
decoder = Dense(128, activation='relu')(decoder)
output_layer = Dense(input_dim, activation='linear')(decoder)

autoencoder = Model(inputs=input_layer, outputs=output_layer)
autoencoder.compile(optimizer=Adam(learning_rate=0.001), loss='mse')

print("\nTraining autoencoder...")
autoencoder.fit(data_scaled, data_scaled, epochs=100, batch_size=32, shuffle=True, verbose=1)

# Create an encoder model to extract latent features.
encoder_model = Model(inputs=input_layer, outputs=latent)
latent_features = encoder_model.predict(data_scaled)

# =============================================================================
# 6. DEC-Inspired Clustering
# =============================================================================
# Define a custom clustering layer as in DEC.
class ClusteringLayer(Layer):
    def __init__(self, n_clusters, weights=None, alpha=1.0, **kwargs):
        super(ClusteringLayer, self).__init__(**kwargs)
        self.n_clusters = n_clusters
        self.alpha = alpha
        self.initial_weights = weights

    def build(self, input_shape):
        self.input_spec = tf.keras.layers.InputSpec(shape=input_shape)
        self.clusters = self.add_weight(name='clusters',
                                        shape=(self.n_clusters, input_shape[1]),
                                        initializer='glorot_uniform',
                                        trainable=True)
        if self.initial_weights is not None:
            self.set_weights(self.initial_weights)
            del self.initial_weights
        super(ClusteringLayer, self).build(input_shape)

    def call(self, inputs, **kwargs):
        # Compute the squared Euclidean distance between inputs and cluster centers.
        q = 1.0 / (1.0 + (K.sum(K.square(K.expand_dims(inputs, axis=1) - self.clusters), axis=2) / self.alpha))
        q **= (self.alpha + 1.0) / 2.0
        # Normalize to get soft assignments.
        q = K.transpose(K.transpose(q) / K.sum(q, axis=1))
        return q

    def compute_output_shape(self, input_shape):
        return (input_shape[0], self.n_clusters)

# Build the DEC model by adding the clustering layer on top of the encoder.
n_clusters = 5  # Adjust the number of clusters as needed
clustering_layer = ClusteringLayer(n_clusters, name='clustering')(latent)
dec_model = Model(inputs=input_layer, outputs=clustering_layer)

# Initialize cluster centers using KMeans on the latent features.
kmeans = KMeans(n_clusters=n_clusters, random_state=42)
y_pred_initial = kmeans.fit_predict(latent_features)
dec_model.get_layer(name='clustering').set_weights([kmeans.cluster_centers_])

# Define target distribution as used in DEC.
def target_distribution(q):
    weight = q ** 2 / q.sum(0)
    return (weight.T / weight.sum(1)).T

# Compile the DEC model with KL divergence loss.
dec_model.compile(optimizer=Adam(learning_rate=0.001), loss='kullback_leibler_divergence')

# DEC training parameters.
maxiter = 10000
update_interval = 140
tol = 0.001  # tolerance threshold to stop training

y_pred_last = np.copy(y_pred_initial)
print("\nStarting DEC fine-tuning...")
for ite in range(maxiter):
    if ite % update_interval == 0:
        # Compute soft assignments.
        q = dec_model.predict(data_scaled, verbose=0)
        p = target_distribution(q)
        # Determine hard assignments.
        y_pred = q.argmax(1)
        # Check how much the assignments have changed.
        delta_label = np.sum(y_pred != y_pred_last).astype(np.float32) / y_pred.shape[0]
        y_pred_last = np.copy(y_pred)
        print(f"Iteration {ite}: delta_label={delta_label:.4f}")
        if delta_label < tol:
            print("Convergence reached.")
            break
    # Train on a mini-batch.
    idx = np.random.randint(0, data_scaled.shape[0], 256)
    loss = dec_model.train_on_batch(data_scaled[idx], p[idx])

# Use the final soft assignments to get the cluster labels.
q_final = dec_model.predict(data_scaled, verbose=0)
cluster_labels = q_final.argmax(1)

# =============================================================================
# 7. t-SNE Visualization of the Latent Space (Optional)
# =============================================================================
tsne = TSNE(n_components=2, random_state=42)
tsne_results = tsne.fit_transform(latent_features)

plt.figure(figsize=(8, 6))
plt.scatter(tsne_results[:, 0], tsne_results[:, 1], c=cluster_labels, cmap='viridis')
plt.xlabel("t-SNE Dimension 1")
plt.ylabel("t-SNE Dimension 2")
plt.title("t-SNE Visualization of Employee Clusters")
plt.colorbar(label='Cluster Label')
plt.show()

# =============================================================================
# 8. Save Cluster Labels Back to the Database
# =============================================================================
# We assume that the table has a unique identifier column named "employee_id".
cursor = conn.cursor()
cursor.execute("PRAGMA table_info(employees)")
columns_info = cursor.fetchall()
columns_names = [col[1] for col in columns_info]

# If the new column does not exist, add it.
if 'cluster_label' not in columns_names:
    cursor.execute("ALTER TABLE employees ADD COLUMN cluster_label INTEGER")
    conn.commit()

# Align the cluster labels with the original data (only rows that were used in clustering).
df_clustered = df_aligned.copy()
df_clustered['cluster_label'] = cluster_labels

# Update each row in the database with the new cluster label.
for index, row in df_clustered.iterrows():
    employee_id = row['employee_id']
    cluster = int(row['cluster_label'])
    cursor.execute("UPDATE employees SET cluster_label = ? WHERE employee_id = ?", (cluster, employee_id))
conn.commit()

print("\nDatabase updated with new cluster labels.")

conn.close()