package com.techdoctorbd.programmingheroquiz.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techdoctorbd.programmingheroquiz.data.model.QuestionListResponse
import com.techdoctorbd.programmingheroquiz.data.repository.RemoteRepository
import com.techdoctorbd.programmingheroquiz.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response


class QuizViewModel : ViewModel() {

    var questionListResponse: MutableLiveData<NetworkResult<QuestionListResponse>> =
        MutableLiveData()

    fun getQuestionList(repository: RemoteRepository) = viewModelScope.launch {
        getQuestionsSafeCall(repository)
    }

    private suspend fun getQuestionsSafeCall(repository: RemoteRepository) {
        questionListResponse.value = NetworkResult.Loading()
        try {
            val response = repository.getQuizList()
            questionListResponse.value = handleQuestionListResponse(response)

        } catch (e: Exception) {
            questionListResponse.value = NetworkResult.Error(e.localizedMessage)
        }
    }

    private fun handleQuestionListResponse(response: Response<QuestionListResponse>): NetworkResult<QuestionListResponse>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Request timeout")
            }

            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }

            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }

            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }
}