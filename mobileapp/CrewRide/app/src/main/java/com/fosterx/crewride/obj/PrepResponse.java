package com.fosterx.crewride.obj;

import com.google.gson.annotations.SerializedName;

public class PrepResponse {


    @SerializedName("Note")
    private String Note;

    @SerializedName("prepSuggestion")
    private String prepSuggestion;

    public PrepResponse() {
        // Default constructor
    }

    public String getPrepSuggestion() {
        return prepSuggestion;
    }

    public void setPrepSuggestion(String prepSuggestion) {
        this.prepSuggestion = prepSuggestion;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    @Override
    public String toString() {
        return "PrepResponse{" +
                "Note='" + Note + '\'' +
                ", prepSuggestion='" + prepSuggestion + '\'' +
                '}';
    }
}