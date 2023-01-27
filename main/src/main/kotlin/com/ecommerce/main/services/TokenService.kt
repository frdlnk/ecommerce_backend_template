package com.ecommerce.main.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import kotlin.collections.HashMap

class TokenService {
    private val secret :String = "NcRfUjWnZr4u7x!A%D*G-KaPdSgVkYp2";
    fun buildToken(userID: String, email: String): String {
        val extra = HashMap<String, Any>()
        extra.put("email", email)
        return Jwts.builder().setSubject(userID).addClaims(extra).signWith(Keys.hmacShaKeyFor(secret.toByteArray())).setExpiration(Date(System.currentTimeMillis() + 2628000000)).compact()
    }

    fun decodeToken(token: String): String? {
        val claims: Claims = Jwts.parserBuilder().setSigningKey(secret.toByteArray()).build().parseClaimsJws(token).body
        return claims.subject
    }
}