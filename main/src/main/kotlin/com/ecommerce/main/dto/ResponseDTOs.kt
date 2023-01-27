package com.ecommerce.main.dto

import java.time.LocalDateTime

data class ResponseDTO(
    var message: String,
    var msgType: String,
    var timestamp: LocalDateTime
)
