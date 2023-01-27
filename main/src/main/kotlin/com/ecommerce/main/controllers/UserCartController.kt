package com.ecommerce.main.controllers

import com.ecommerce.main.dto.ResponseDTO
import com.ecommerce.main.repositories.CostumerRepository
import com.ecommerce.main.repositories.ProductRepository
import com.ecommerce.main.services.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/user/cart")
class UserCartController(val costumerRepository: CostumerRepository, val customerRepository: CostumerRepository, val productRepository: ProductRepository) {
    val tokenService: TokenService = TokenService()

    @PutMapping("/add-product")
    fun addProductToCart(@RequestHeader token: String, @RequestParam productid: String): ResponseEntity<ResponseDTO> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        if(customerFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))
        val product = productRepository.findById(productid)
        val newCart = customerFound.get().cart.plus(product.get())
        customerFound.get().cart = newCart
        customerRepository.save(customerFound.get())
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Cart has been updated", "cart/updated", LocalDateTime.now()))
    }

    @PutMapping("/delete-product")
    fun deleteProduct(@RequestHeader token: String, @RequestParam productIndex: Int):ResponseEntity<ResponseDTO>{
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        if(customerFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))
        val newCart = ArrayList(customerFound.get().cart)
        customerFound.get().cart = listOf(newCart.removeAt(productIndex))
        customerRepository.save(customerFound.get())
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Cart has been updated", "cart/updated", LocalDateTime.now()))
     }

    @PutMapping("/clean")
    fun cleanCart(@RequestHeader token: String):ResponseEntity<ResponseDTO> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        val customerFound = costumerRepository.findById(decodedToken)
        if(customerFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))
        customerFound.get().cart = listOf()
        customerRepository.save(customerFound.get())
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Cart has been cleaned", "cart/cleaned", LocalDateTime.now()))
    }

}