package com.ecommerce.main.repositories

import com.ecommerce.main.models.Costumer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CostumerRepository : MongoRepository<Costumer, String> {
    fun findByEmail(email: String):Costumer?
}