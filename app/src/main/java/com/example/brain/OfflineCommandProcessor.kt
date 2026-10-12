package com.example.brain

import com.example.data.model.ActionType

data class CommandResult(
    val isHandledOffline: Boolean,
    val spokenResponse: String,
    val actionType: ActionType = ActionType.NONE,
    val actionPayload: String? = null,
    val promptForOnlineAi: String? = null
)

class OfflineCommandProcessor {

    fun processCommand(input: String): CommandResult {
        val text = input.trim()
        val lower = text.lowercase()

        // 1. Phone Calling Commands (Hindi / Hinglish / English)
        // e.g. "Rahul ko call karo", "Call Amit", "Phone lagao Dad ko"
        if (lower.contains("call") || lower.contains("फोन लगाओ") || lower.contains("कॉल करो") || lower.contains("phone lagao")) {
            val contactName = extractContactNameForCall(text)
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = if (contactName.isNotBlank()) {
                    "हाँ सर, $contactName को कॉल लगाने के लिए तैयार हूँ। क्या डायल करूँ?"
                } else {
                    "सर, किसे कॉल करना है? कृपया नाम बताइए।"
                },
                actionType = ActionType.PHONE_CALL,
                actionPayload = contactName
            )
        }

        // 2. WhatsApp Messaging
        // e.g. "WhatsApp Amit: meeting tomorrow", "Amit ko WhatsApp karo"
        if (lower.contains("whatsapp") || lower.contains("व्हाट्सएप")) {
            val (recipient, message) = extractRecipientAndMessage(text, "whatsapp")
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = "सर, WhatsApp संदेश तैयार है: \"$message\"। क्या भेज दूँ?",
                actionType = ActionType.WHATSAPP_MESSAGE,
                actionPayload = "$recipient|||$message"
            )
        }

        // 3. SMS Messaging Commands
        // e.g. "Amit ko message bhejo ki main 10 minute mein aa raha hoon", "Send SMS to Rahul: I am coming"
        if (lower.contains("message") || lower.contains("संदेश") || lower.contains("sms") || lower.contains("msg")) {
            val (recipient, message) = extractRecipientAndMessage(text, "message")
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = "सर, $recipient के लिए SMS तैयार है: \"$message\"। क्या मैं यह संदेश भेज दूँ?",
                actionType = ActionType.SMS_MESSAGE,
                actionPayload = "$recipient|||$message"
            )
        }

        // 4. Website Builder Trigger
        // e.g. "MAXY, mere liye ek gaming website banao jisme login aur tournament section ho"
        if (lower.contains("website") || lower.contains("वेबसाइट") || lower.contains("web page")) {
            return CommandResult(
                isHandledOffline = false,
                spokenResponse = "बिल्कुल सर! आपकी वेबसाइट जनरेट कर रहा हूँ। तुरंत पूर्वावलोकन तैयार होगा।",
                actionType = ActionType.WEB_PROJECT,
                actionPayload = text,
                promptForOnlineAi = text
            )
        }

        // 5. Android App Builder Trigger
        // e.g. "MAXY, ek tournament app banao jisme login, signup, tournament list aur admin panel ho"
        if (lower.contains("app banao") || lower.contains("create app") || lower.contains("android app") || lower.contains("ऐप बनाओ")) {
            return CommandResult(
                isHandledOffline = false,
                spokenResponse = "समझ गया सर! आपके लिए पूरा Android Studio और Jetpack Compose प्रोजेक्ट तैयार किया जा रहा है।",
                actionType = ActionType.APP_PROJECT,
                actionPayload = text,
                promptForOnlineAi = text
            )
        }

        // 6. Flashlight Device Control
        if (lower.contains("flashlight") || lower.contains("torch") || lower.contains("टॉर्च")) {
            val enable = !lower.contains("off") && !lower.contains("बंद") && !lower.contains("band")
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = if (enable) "टॉर्च चालू कर दी गई है, सर।" else "टॉर्च बंद कर दी गई है, सर।",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = if (enable) "FLASHLIGHT_ON" else "FLASHLIGHT_OFF"
            )
        }

        // 7. Time & Date Queries
        if (lower.contains("time") || lower.contains("समय") || lower.contains("टाइम")) {
            val currentTime = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = "सर, अभी समय है $currentTime.",
                actionType = ActionType.NONE
            )
        }

        if (lower.contains("date") || lower.contains("तारीख") || lower.contains("दिन")) {
            val currentDate = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = "सर, आज की तारीख है $currentDate.",
                actionType = ActionType.NONE
            )
        }

        // 8. Open Installed Applications
        if (lower.startsWith("open ") || lower.contains("kholo") || lower.contains("खोलो")) {
            val appTarget = extractAppTarget(lower)
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = "सर, $appTarget खोल रहा हूँ।",
                actionType = ActionType.DEVICE_CONTROL,
                actionPayload = "OPEN_APP:$appTarget"
            )
        }

        // 9. Notes & Reminders
        if (lower.contains("note") || lower.contains("नोट") || lower.contains("याद रखना") || lower.contains("remind")) {
            val noteContent = text
                .replace(Regex("(?i)^(maxy|note likho|note banao|remind me to|create note)"), "")
                .trim()
            return CommandResult(
                isHandledOffline = true,
                spokenResponse = "सर, आपकी नोट सुरक्षित रूप से सहेज ली गई है।",
                actionType = ActionType.NOTE_CREATED,
                actionPayload = noteContent.ifBlank { "Important Task" }
            )
        }

        // 10. Default: Pass to Online Gemini JARVIS Core
        return CommandResult(
            isHandledOffline = false,
            spokenResponse = "",
            actionType = ActionType.NONE,
            promptForOnlineAi = text
        )
    }

    private fun extractContactNameForCall(text: String): String {
        return text
            .replace(Regex("(?i)^(maxy|jarvis|please|bhai)\\s*"), "")
            .replace(Regex("(?i)(ko call karo|ko phone lagao|call karo|ko call|call to|call|phone lagao|ko dial karo)"), "")
            .replace(Regex("[.,!?:;]"), "")
            .trim()
    }

    private fun extractRecipientAndMessage(text: String, type: String): Pair<String, String> {
        // e.g. "Amit ko message bhejo ki main 10 minute mein aa raha hoon"
        // e.g. "Send message to Amit: I am on my way"
        var clean = text.replace(Regex("(?i)^(maxy|jarvis|bhai|please)\\s*"), "")
        
        if (clean.contains("ki ", ignoreCase = true) || clean.contains("कि ", ignoreCase = true)) {
            val parts = clean.split(Regex("(?i)\\s+(ki|कि)\\s+"), limit = 2)
            val recipientPart = parts[0]
                .replace(Regex("(?i)(ko message bhejo|ko sms bhejo|ko whatsapp karo|ko whatsapp message bhejo|message bhejo|sms bhejo)"), "")
                .trim()
            val msgPart = parts.getOrNull(1)?.trim() ?: "Hello"
            return Pair(recipientPart, msgPart)
        }

        if (clean.contains(":")) {
            val parts = clean.split(":", limit = 2)
            val recipientPart = parts[0]
                .replace(Regex("(?i)(send message to|send sms to|whatsapp to|message|sms)"), "")
                .trim()
            return Pair(recipientPart, parts[1].trim())
        }

        // Fallback
        val words = clean.split(" ")
        val recipient = words.firstOrNull { !it.equals(type, ignoreCase = true) && !it.equals("ko", ignoreCase = true) } ?: "Friend"
        val message = clean.replace(Regex("(?i)($recipient|ko|message|bhejo|send|sms|whatsapp)"), "").trim()
        return Pair(recipient, message.ifBlank { "Hello from MAXY AI!" })
    }

    private fun extractAppTarget(lower: String): String {
        return when {
            lower.contains("youtube") -> "youtube"
            lower.contains("whatsapp") -> "whatsapp"
            lower.contains("chrome") || lower.contains("browser") -> "chrome"
            lower.contains("camera") || lower.contains("कैमरा") -> "camera"
            lower.contains("settings") || lower.contains("सेटिंग") -> "settings"
            lower.contains("maps") -> "maps"
            lower.contains("calculator") -> "calculator"
            lower.contains("contacts") -> "contacts"
            else -> "settings"
        }
    }
}
