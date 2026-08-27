package com.example.ujianku.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String? = null,
    @SerialName("teacher_id") val teacherId: String,
    val question: String,
    @SerialName("option_a") val optionA: String,
    @SerialName("option_b") val optionB: String,
    @SerialName("option_c") val optionC: String,
    @SerialName("option_d") val optionD: String,
    @SerialName("correct_answer") val correctAnswer: String
)
