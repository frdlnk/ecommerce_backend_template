package com.ecommerce.main.models

import org.bson.BsonObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "products")
class Product {
    @Id
    lateinit var _id: String
    var title: String = ""
    var imgURL: String = ""
    var description: String = ""
    var price: Double = 0.0
    var categories: List<String> = listOf()
    var inStock: Boolean = true
}
