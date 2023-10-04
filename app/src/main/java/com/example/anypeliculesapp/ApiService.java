package com.example.anypeliculesapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/getPreguntes")
    Call<JsonResponseModel> getPreguntes();
}
