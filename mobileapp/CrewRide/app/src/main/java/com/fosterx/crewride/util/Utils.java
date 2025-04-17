package com.fosterx.crewride.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

public class Utils {

    /**
     * Convert a vector drawable resource to a Bitmap for use as a map marker.
     */
    public static Bitmap getBitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) {
            return null;
        }
        int width = vectorDrawable.getIntrinsicWidth() - 75;
        int height = vectorDrawable.getIntrinsicHeight() - 75;
        vectorDrawable.setBounds(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
