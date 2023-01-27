package com.ecommerce.main.dto

data class RegisterDTO(
    var fullName: String,
    var email: String,
    var password: String,
    var fullAddress: String,
    var country: String,
    var deviceToken: String =""
)
