package com.example.finalproject;

import com.google.gson.annotations.SerializedName;

public class QuoteModel {

    @SerializedName("quote")
    private String quote;

    @SerializedName("author")
    private String author;

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }
}
