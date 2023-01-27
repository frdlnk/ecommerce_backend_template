package com.ecommerce.main.repositories

import com.ecommerce.main.models.Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ProductRepository : MongoRepository<Product, String> {
    @Query("{'title': {\$regex: ?0, \$options: 'i'}}")
    fun search(title: String): List<Product>

    fun findByTitle(title: String): Product?
}