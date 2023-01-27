package com.ecommerce.main.models

import java.time.LocalDateTime

data class NotificationModel(
    var title: String,
    var body: String,
    var iconUrl: String,
    var seen: Boolean,
    var timestamp: LocalDateTime
)
