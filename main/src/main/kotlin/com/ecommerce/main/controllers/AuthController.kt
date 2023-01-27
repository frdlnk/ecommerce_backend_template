package com.ecommerce.main.controllers

import com.ecommerce.main.models.Costumer
import com.ecommerce.main.repositories.CostumerRepository
import com.ecommerce.main.services.PasswordCrypt
import com.ecommerce.main.dto.LoginDTO
import com.ecommerce.main.dto.RegisterDTO
import com.ecommerce.main.dto.ResponseDTO
import com.ecommerce.main.services.TokenService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/auth")
class AuthController(val costumerRepository: CostumerRepository, val response: HttpServletResponse) {
    val passwordCrypt: PasswordCrypt = PasswordCrypt();
    val tokenService: TokenService = TokenService();

    @PostMapping("/login")
    fun login(@RequestBody data: LoginDTO):ResponseEntity<ResponseDTO> {
        if(data.email.isEmpty() || data.password.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ResponseDTO("Fill the blanks before send", "auth/invalid-request", LocalDateTime.now())
        )
        val customer = costumerRepository.findByEmail(data.email)
        if(customer?._id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("User not found with this credentials", "auth/invalid-credentials", LocalDateTime.now()))
        if(!passwordCrypt.comparePassword(data.password, customer.password).verified) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ResponseDTO("The password for ${data.email} is incorrect", "auth/wrong-password", LocalDateTime.now())
        )
        val jwt = tokenService.buildToken(customer._id, customer.email)
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Welcome back ${customer.fullname.slice(0..customer.fullname.indexOf(" "))}", "auth/login-successfully", LocalDateTime.now()))
    }

    @PostMapping("/register")
    fun register(@RequestBody data :RegisterDTO) :ResponseEntity<ResponseDTO>?{
        if(data.email.isEmpty() || data.password.isEmpty() || data.fullName.isEmpty() || data.fullAddress.isEmpty() || data.country.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO("Fill the blanks before send", "auth/invalid-request", LocalDateTime.now()))
        val customer = costumerRepository.findByEmail(data.email)
        if(customer?._id != null) return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO("${data.email} is already registered", "auth/email-already-exists", LocalDateTime.now()))

        val newCustomer = Costumer()
        newCustomer.fullname = data.fullName
        newCustomer.email = data.email
        newCustomer.password = passwordCrypt.hashPassword(data.password)
        newCustomer.country = data.country
        newCustomer.fullAddress = data.fullAddress
        newCustomer.deviceToken = data.deviceToken
        costumerRepository.save(newCustomer)

        val jwt = tokenService.buildToken(newCustomer._id, newCustomer.email)
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO("Welcome ${newCustomer.fullname.slice(0..newCustomer.fullname.indexOf(" "))}", "auth/registered-successfully", LocalDateTime.now()))
    }

    @GetMapping("/profile")
    fun getProfile(@RequestHeader token: String): ResponseEntity<Any> {
        val decodedToken = tokenService.decodeToken(token)
        if(decodedToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("No token provided on the request", "auth/no-token-provided", LocalDateTime.now()))
        var customerFound = costumerRepository.findById(decodedToken)
        if(customerFound.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("Your session has been expired, Login or Register", "auth/session-expired", LocalDateTime.now()))
        return ResponseEntity.status(HttpStatus.OK).body(customerFound.get())
    }
}