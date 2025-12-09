package com.example.finalproject;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("v2/randomquotes")
    Call<List<QuoteModel>> getQuotes(@Header("X-Api-Key") String apiKey, @Query("category") String category);

}
