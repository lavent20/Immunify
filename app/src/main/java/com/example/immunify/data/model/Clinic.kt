package com.example.immunify.data.model

data class Clinic(
    val id: String,
    val name: String,
    val address: String,
    val distance: String,
    val rating: Double,
    val isAvailable: Boolean,
    val latitude: Double,
    val longitude: Double
)
