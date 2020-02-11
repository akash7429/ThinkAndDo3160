package com.example.loginauth.rest.services;

import com.example.loginauth.LoginActivity;
import com.example.loginauth.MainActivity;
import com.example.loginauth.NewLogIn;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserInterface {

    @POST("login")
    Call<Integer> signin(@Body NewLogIn.UserInfo userInfo);



}