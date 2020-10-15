package com.fascode

import com.eatthepath.pushy.apns.ApnsClient
import com.eatthepath.pushy.apns.ApnsClientBuilder
import com.eatthepath.pushy.apns.ApnsPushNotification
import com.eatthepath.pushy.apns.PushNotificationResponse
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification
import com.eatthepath.pushy.apns.util.TokenUtil
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.util.concurrent.ExecutionException

object ApnsUtils {
    val topic = "com.fascode.sk.attributable"
    fun buildClient(development: Boolean) : ApnsClient {
        return ApnsClientBuilder()
            .setApnsServer(if(development) ApnsClientBuilder.DEVELOPMENT_APNS_HOST else ApnsClientBuilder.PRODUCTION_APNS_HOST)
            .setClientCredentials(File("PushAttributable.p12"), "")
            .build()
    }
    fun verifyUninstall(token: String, development: Boolean = false) : String {
        var result: String = "Internal Error!"
        try {
            val pushNotificationResponse: PushNotificationResponse<ApnsPushNotification>
                    = doVerifyUninstall(token, development).get()
            if (pushNotificationResponse.isAccepted) {
                result = "Push notification accepted by APNs gateway. $pushNotificationResponse"
            } else {
                result =  "Notification rejected by the APNs gateway: ${pushNotificationResponse.rejectionReason}"
                if (pushNotificationResponse.tokenInvalidationTimestamp != null) {
                    result += (
                        "\t…and the token is invalid as of " +
                                pushNotificationResponse.tokenInvalidationTimestamp
                    )
                }
            }
        } catch (e: ExecutionException) {
            result = "Failed to send push notification: ${e.message}"
            e.printStackTrace()
        }
        return result
    }
    private fun doVerifyUninstall(token: String, development: Boolean = false) :
            PushNotificationFuture<ApnsPushNotification, PushNotificationResponse<ApnsPushNotification>> {
        val apnsClient = buildClient(development = development)
        val payload = ApnsPayloadBuilder()
            .setContentAvailable(false)
            .buildWithDefaultMaximumLength()
        val pushNotification = SimpleApnsPushNotification(TokenUtil.sanitizeTokenString(token), topic, payload)
        return apnsClient.sendNotification(pushNotification)
    }

    fun retarget(token: String, title: String, body: String, pid: String, c: String, development: Boolean = false) : String {
        return sendMessageToDevice(token = token, title = title, body = body, development = development, customProperties = mapOf(
            "af" to mapOf(
                "c" to c,
                "pid" to pid,
                "is_retargeting" to "true"
            )
        ))
    }

    fun sendMessageToDevice(token: String, title: String, body: String, development: Boolean = false, customProperties: Map<String, Any>? = null) : String {
        var result: String = "Internal Error!"
        try {
            val pushNotificationResponse: PushNotificationResponse<SimpleApnsPushNotification> =
                doSendMessageToDevice(token, title, body, development, customProperties).get()
            if (pushNotificationResponse.isAccepted) {
                result = "Push notification accepted by APNs gateway. $pushNotificationResponse"
            } else {
                result =  "Notification rejected by the APNs gateway: ${pushNotificationResponse.rejectionReason}"
                if (pushNotificationResponse.tokenInvalidationTimestamp != null) {
                    result += (
                            "\t…and the token is invalid as of " +
                                    pushNotificationResponse.tokenInvalidationTimestamp
                            )
                }
            }
        } catch (e: ExecutionException) {
            result = "Failed to send push notification: ${e.message}"
            e.printStackTrace()
        }
        return result
    }
    private fun doSendMessageToDevice(token: String, title: String, body: String, development: Boolean = false, customProperties: Map<String, Any>? = null):
            PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> {
        val apnsClient = buildClient(development = development)
        val payloadBuilder = ApnsPayloadBuilder()
            .setAlertTitle(title)
            .setAlertBody(body)
            .setContentAvailable(true)
        customProperties?.forEach { key, value -> payloadBuilder.addCustomProperty(key, value) }
        val payload = payloadBuilder.buildWithDefaultMaximumLength()
        val pushNotification = SimpleApnsPushNotification(TokenUtil.sanitizeTokenString(token), topic, payload)
        return apnsClient.sendNotification(pushNotification)
    }
}