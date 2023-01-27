package com.ecommerce.main.repositories

import com.ecommerce.main.models.Admin
import org.springframework.data.mongodb.repository.MongoRepository

interface AdminRepository : MongoRepository<Admin, String> {
    fun findByEmail(email:String): Admin?
}