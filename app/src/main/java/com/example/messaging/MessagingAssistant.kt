package com.example.messaging

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

data class MessageDraft(
    val recipientName: String,
    val phoneNumber: String,
    val messageText: String,
    val isWhatsApp: Boolean = false
)

class MessagingAssistant(private val context: Context) {

    fun sendViaSmsApp(phoneNumber: String, message: String) {
        try {
            val uri = Uri.parse("smsto:${phoneNumber.replace(" ", "")}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun sendDirectSmsIfPermitted(phoneNumber: String, message: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                val cleanNumber = phoneNumber.replace(" ", "")
                val parts = smsManager.divideMessage(message)
                if (parts.size > 1) {
                    smsManager.sendMultipartTextMessage(cleanNumber, null, parts, null, null)
                } else {
                    smsManager.sendTextMessage(cleanNumber, null, message, null, null)
                }
                return true
            } catch (_: Exception) {
                sendViaSmsApp(phoneNumber, message)
                return false
            }
        } else {
            sendViaSmsApp(phoneNumber, message)
            return false
        }
    }

    fun sendViaWhatsApp(phoneNumber: String?, message: String) {
        val cleanNumber = phoneNumber?.replace(Regex("[^0-9]"), "") ?: ""
        try {
            // First try WhatsApp direct api intent
            val uri = if (cleanNumber.isNotBlank()) {
                Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
            } else {
                Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}")
            }

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to generic share intent
            try {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Message").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            } catch (_: Exception) {}
        }
    }
}
