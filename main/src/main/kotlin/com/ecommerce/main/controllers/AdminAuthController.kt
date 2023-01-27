package com.ecommerce.main.controllers

import com.ecommerce.main.dto.AdminRegisterDTO
import com.ecommerce.main.dto.LoginDTO
import com.ecommerce.main.dto.ResponseDTO
import com.ecommerce.main.models.Admin
import com.ecommerce.main.repositories.AdminRepository
import com.ecommerce.main.services.PasswordCrypt
import com.ecommerce.main.services.TokenService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/admin/auth")
class AdminAuthController (val adminRepository: AdminRepository, val response: HttpServletResponse) {
    val passwordCrypt: PasswordCrypt = PasswordCrypt()
    val tokenService: TokenService = TokenService()

    @PostMapping("/login")
    fun login(@RequestBody data: LoginDTO): ResponseEntity<ResponseDTO> {
        if(data.email.isEmpty() || data.password.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ResponseDTO("Fill the blanks before send", "auth/invalid-request", LocalDateTime.now())
        )
        val admin = adminRepository.findByEmail(data.email)
        if(admin?._id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO("User not found with this credentials", "auth/invalid-credentials", LocalDateTime.now()))
        if(!passwordCrypt.comparePassword(data.password, admin.password).verified) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ResponseDTO("The password for ${data.email} is incorrect", "auth/wrong-password", LocalDateTime.now())
        )
        val jwt = tokenService.buildToken(admin._id, admin.email)
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO("Welcome back ${admin.fullname.slice(0..admin.fullname.indexOf(" "))}", "auth/login-successfully", LocalDateTime.now()))

    }

    @PostMapping("/register")
    fun register(@RequestBody data: AdminRegisterDTO):ResponseEntity<ResponseDTO> {
        if(data.email.isEmpty() || data.password.isEmpty() || data.fullName.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO("Fill the blanks before send", "auth/invalid-request", LocalDateTime.now()))
        val admin = adminRepository.findByEmail(data.email)
        if(admin?._id != null) return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO("${data.email} is already registered", "auth/email-already-exists", LocalDateTime.now()))
        val newAdmin = Admin()
        newAdmin.fullname = data.fullName
        newAdmin.email = data.email
        newAdmin.password = passwordCrypt.hashPassword(data.password)
        adminRepository.save(newAdmin)

        val jwt = tokenService.buildToken(newAdmin._id, newAdmin.email)
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO("Welcome ${newAdmin.fullname.slice(0..newAdmin.fullname.indexOf(" "))}", "auth/registered-successfully", LocalDateTime.now()))

    }
}