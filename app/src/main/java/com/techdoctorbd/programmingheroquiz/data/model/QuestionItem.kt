package com.techdoctorbd.programmingheroquiz.data.model

data class QuestionItem(
    val question: String = "",
    val answers: AnswerItem = AnswerItem(),
    val questionImageUrl: String? = null,
    val correctAnswer: String = "",
    val score: Int = 0
)