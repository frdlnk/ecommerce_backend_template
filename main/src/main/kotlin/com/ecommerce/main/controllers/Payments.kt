package com.ecommerce.main.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
class Payments {
    @GetMapping("")
    fun sendInformation(): String {
        return "This controller is for implement the stripe payments methods"
    }
}