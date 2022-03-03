package com.techdoctorbd.programmingheroquiz.data.model

import com.google.gson.annotations.SerializedName

data class AnswerItem(
    @SerializedName("A")
    val a: String = "",
    @SerializedName("B")
    val b: String = "",
    @SerializedName("C")
    val c: String = "",
    @SerializedName("D")
    val d: String = ""
)