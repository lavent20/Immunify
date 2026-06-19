package com.example.immunify.data.model

import com.example.immunify.model.Vaksin

data class VaksinModelResponse(
    val item: Vaksin? = Vaksin(),
    val key: String? = ""
)