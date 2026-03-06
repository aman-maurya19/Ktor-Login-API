package com.example

import kotlinx.serialization.Serializable
@Serializable
data class SignupRequest(
    val name: String,
    val email: String,
    val mobile: String,  // <--- Ye line add karein
    val password: String
)



