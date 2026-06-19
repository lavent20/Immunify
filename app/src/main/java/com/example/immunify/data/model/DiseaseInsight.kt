// data/model/DiseaseInsight.kt
package com.example.immunify.data.model

data class DiseaseInsight(
    val id: String,
    val name: String,
    val emoji: String,
    val imageBgColor: Long,
    val keyFacts: List<String>,
    val overview: String
)