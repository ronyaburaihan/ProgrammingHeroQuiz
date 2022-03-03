package com.techdoctorbd.programmingheroquiz.data.network

import com.techdoctorbd.programmingheroquiz.data.model.QuestionListResponse
import retrofit2.Response
import retrofit2.http.GET

interface QuizApi {

    @GET("quiz.json")
    suspend fun getAllQuestions(): Response<QuestionListResponse>
}