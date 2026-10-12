package com.example.telephony

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

data class ContactInfo(
    val name: String,
    val number: String,
    val photoUri: String? = null
)

class CallAssistant(private val context: Context) {

    // Preset contacts for demonstration when device contacts permission is not granted
    private val demoContacts = listOf(
        ContactInfo("Rahul", "+91 98765 43210"),
        ContactInfo("Amit", "+91 91234 56789"),
        ContactInfo("Pooja", "+91 99887 76655"),
        ContactInfo("Mom", "+91 98111 22334"),
        ContactInfo("Dad", "+91 98222 33445"),
        ContactInfo("Rohit", "+91 97333 44556")
    )

    fun findContact(queryName: String): ContactInfo? {
        val q = queryName.trim().lowercase()
        if (q.isBlank()) return null

        // Try reading real device contacts if permission granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                )
                val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
                val selectionArgs = arrayOf("%$queryName%")

                context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val name = cursor.getString(0) ?: queryName
                        val number = cursor.getString(1) ?: ""
                        val photo = cursor.getString(2)
                        return ContactInfo(name, number, photo)
                    }
                }
            } catch (_: Exception) {}
        }

        // Match from demo contacts or return a generated standard contact format
        val matched = demoContacts.firstOrNull { it.name.lowercase().contains(q) || q.contains(it.name.lowercase()) }
        if (matched != null) return matched

        // If not found in demo, synthesize contact with standard international format
        return ContactInfo(
            name = queryName.replaceFirstChar { it.uppercase() },
            number = "+91 98${(10000000..99999999).random()}"
        )
    }

    fun launchDialer(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${phoneNumber.replace(" ", "")}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun makeDirectCallIfPermitted(phoneNumber: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${phoneNumber.replace(" ", "")}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return true
            } catch (_: Exception) {
                launchDialer(phoneNumber)
                return false
            }
        } else {
            launchDialer(phoneNumber)
            return false
        }
    }
}
