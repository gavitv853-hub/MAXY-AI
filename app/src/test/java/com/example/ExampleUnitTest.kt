package com.example

import com.example.appbuilder.AndroidAppGenerator
import com.example.brain.OfflineCommandProcessor
import com.example.data.model.ActionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private val offlineProcessor = OfflineCommandProcessor()

    @Test
    fun testCallCommandInHindi() {
        val result = offlineProcessor.processCommand("Rahul ko call karo")
        assertTrue(result.isHandledOffline)
        assertEquals(ActionType.PHONE_CALL, result.actionType)
        assertEquals("Rahul", result.actionPayload)
    }

    @Test
    fun testCallCommandInEnglish() {
        val result = offlineProcessor.processCommand("Call Amit")
        assertTrue(result.isHandledOffline)
        assertEquals(ActionType.PHONE_CALL, result.actionType)
        assertEquals("Amit", result.actionPayload)
    }

    @Test
    fun testMessagingCommandInHinglish() {
        val result = offlineProcessor.processCommand("Amit ko message bhejo ki main 10 minute mein aa raha hoon")
        assertTrue(result.isHandledOffline)
        assertEquals(ActionType.SMS_MESSAGE, result.actionType)
        assertNotNull(result.actionPayload)
        assertTrue(result.actionPayload!!.contains("Amit"))
        assertTrue(result.actionPayload!!.contains("10 minute"))
    }

    @Test
    fun testWebsiteBuilderIntent() {
        val result = offlineProcessor.processCommand("MAXY, mere liye ek gaming website banao jisme login aur tournament section ho")
        assertEquals(ActionType.WEB_PROJECT, result.actionType)
    }

    @Test
    fun testAppBuilderIntent() {
        val result = offlineProcessor.processCommand("MAXY, ek tournament app banao jisme login aur list ho")
        assertEquals(ActionType.APP_PROJECT, result.actionType)
    }

    @Test
    fun testFlashlightToggleIntent() {
        val onResult = offlineProcessor.processCommand("Torch chalu karo")
        assertEquals(ActionType.DEVICE_CONTROL, onResult.actionType)
        assertEquals("FLASHLIGHT_ON", onResult.actionPayload)

        val offResult = offlineProcessor.processCommand("Torch band karo")
        assertEquals(ActionType.DEVICE_CONTROL, offResult.actionType)
        assertEquals("FLASHLIGHT_OFF", offResult.actionPayload)
    }
}
