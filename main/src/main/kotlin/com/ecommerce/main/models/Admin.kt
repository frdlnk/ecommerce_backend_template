package com.ecommerce.main.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "admins")
class Admin {
    @Id
    lateinit var _id: String;
    var fullname: String = ""
    var email: String = ""
    var password: String = ""
    var notifications: List<NotificationModel> = listOf<NotificationModel>()
    var createdAt: LocalDateTime = LocalDateTime.now()
}