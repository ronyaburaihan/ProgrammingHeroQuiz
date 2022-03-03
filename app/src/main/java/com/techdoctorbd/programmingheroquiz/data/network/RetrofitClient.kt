package com.techdoctorbd.programmingheroquiz.data.network

import com.google.gson.GsonBuilder
import com.techdoctorbd.programmingheroquiz.util.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
}