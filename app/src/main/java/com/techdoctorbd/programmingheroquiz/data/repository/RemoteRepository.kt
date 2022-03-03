package com.techdoctorbd.programmingheroquiz.data.repository

import com.techdoctorbd.programmingheroquiz.data.model.QuestionListResponse
import com.techdoctorbd.programmingheroquiz.data.network.QuizApi
import retrofit2.Response

class RemoteRepository(private val quizApi: QuizApi) {

    suspend fun getQuizList(): Response<QuestionListResponse> {
        return quizApi.getAllQuestions()
    }
}