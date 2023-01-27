package com.ecommerce.main.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class Firebase {
    @Throws(IOException::class)
    private fun initFirestore() {
        val serviceAccount = javaClass.getResourceAsStream("googleServices.json")
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
    }

    fun getMessaging(): FirebaseMessaging {
        return  FirebaseMessaging.getInstance()
    }

}