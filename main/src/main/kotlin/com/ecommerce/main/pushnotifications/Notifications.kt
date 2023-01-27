package com.ecommerce.main.pushnotifications


import com.ecommerce.main.dto.ResponseDTO
import com.ecommerce.main.repositories.CostumerRepository
import com.ecommerce.main.services.Firebase
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.http.ResponseEntity
import com.ecommerce.main.models.NotificationModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/notifications")
class Notifications(val firebase: Firebase, val customerRepository: CostumerRepository) {

    @PostMapping("/create")
    fun sendNotificationsToAllCustomers(@RequestBody notification: NotificationModel):ResponseEntity<ResponseDTO> {
        val messaging = firebase.getMessaging()
        val customers = customerRepository.findAll()

        customers.forEach {
            val message = Message.builder().setToken(it.deviceToken).setNotification(Notification.builder().setTitle(notification.title).setBody(notification.body).setImage(notification.iconUrl).build()).build()
            messaging.send(message)
            var noti = NotificationModel(notification.title, notification.body, notification.iconUrl, false, LocalDateTime.now())
            var customerNotis = it.notifications.plus(noti)
            it.notifications = customerNotis
            customerRepository.save(it)
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO("Notifications sended", "notifications/created", LocalDateTime.now()))
    }
}