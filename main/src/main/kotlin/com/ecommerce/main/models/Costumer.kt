package com.ecommerce.main.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "customers")
class Costumer {
    @Id
    lateinit var _id: String;
    var fullname: String = ""
    var email: String = ""
    var password: String = ""
    var fullAddress: String = ""
    var country: String = ""

    //Appdata
    var cart: List<Product> = listOf()
    var notifications: List<NotificationModel> = listOf()
    var deviceToken: String = ""
    var createdAt: LocalDateTime = LocalDateTime.now()
}
