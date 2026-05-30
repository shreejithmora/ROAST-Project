package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.roast.RoastArchetype
import com.example.roast.RoastLevel
import com.example.roast.RoastViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoastViewModelTest {

    private lateinit var viewModel: RoastViewModel
    private lateinit var context: Application

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        viewModel = RoastViewModel(context)
        // Clear prefs to ensure fresh state
        val prefs = context.getSharedPreferences("roast_prefs", Application.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun testInitialState() {
        assertEquals("", viewModel.targetName.value)
        assertEquals(RoastArchetype.PROGRAMMER, viewModel.selectedArchetype.value)
        assertEquals(RoastLevel.SAVAGE, viewModel.selectedLevel.value)
        assertEquals("", viewModel.customTag.value)
        assertEquals(emptyMap<String, Int>(), viewModel.emojiReactions.value)
    }

    @Test
    fun testUpdateTargetName() {
        viewModel.setTargetName("Alice")
        assertEquals("Alice", viewModel.targetName.value)
    }

    @Test
    fun testUpdateArchetype() {
        viewModel.setArchetype(RoastArchetype.CRYPTO_BRO)
        assertEquals(RoastArchetype.CRYPTO_BRO, viewModel.selectedArchetype.value)
    }

    @Test
    fun testUpdateRoastLevel() {
        viewModel.setRoastLevel(RoastLevel.OBLITERATION)
        assertEquals(RoastLevel.OBLITERATION, viewModel.selectedLevel.value)
    }

    @Test
    fun testUpdateCustomTag() {
        viewModel.setCustomTag("Always forgets semicolons")
        assertEquals("Always forgets semicolons", viewModel.customTag.value)
    }

    @Test
    fun testCustomApiKeyManagement() {
        assertEquals("", viewModel.getCustomApiKey())
        
        viewModel.saveCustomApiKey("test_key")
        assertEquals("test_key", viewModel.getCustomApiKey())
        assertTrue(viewModel.isApiKeyAvailable())
        assertTrue(viewModel.useAI.value)
        
        viewModel.saveCustomApiKey("")
        assertEquals("", viewModel.getCustomApiKey())
    }

    @Test
    fun testIncrementReactions() {
        assertTrue(viewModel.emojiReactions.value.isEmpty())
        
        viewModel.incrementReaction("🔥")
        assertEquals(1, viewModel.emojiReactions.value["🔥"])
        
        viewModel.incrementReaction("🔥")
        assertEquals(2, viewModel.emojiReactions.value["🔥"])
        
        viewModel.incrementReaction("💀")
        assertEquals(1, viewModel.emojiReactions.value["💀"])
    }
}
