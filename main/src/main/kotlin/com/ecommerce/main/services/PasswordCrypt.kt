package com.ecommerce.main.services

import at.favre.lib.crypto.bcrypt.BCrypt

class PasswordCrypt {
    fun comparePassword(password: String, userPassword: String) :BCrypt.Result{
        return BCrypt.verifyer().verify(password.toCharArray(), userPassword)
    }

    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }
}