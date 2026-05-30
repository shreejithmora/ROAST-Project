package com.example.roast

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RoastUiState {
    object Idle : RoastUiState
    data class Loading(val message: String) : RoastUiState
    data class Success(
        val roastText: String,
        val isAiGenerated: Boolean,
        val modelName: String? = null
    ) : RoastUiState
    data class Error(val message: String, val fallbackRoast: String) : RoastUiState
}

class RoastViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("roast_prefs", Context.MODE_PRIVATE)

    private val _targetName = MutableStateFlow("")
    val targetName: StateFlow<String> = _targetName.asStateFlow()

    private val _selectedArchetype = MutableStateFlow(RoastArchetype.PROGRAMMER)
    val selectedArchetype: StateFlow<RoastArchetype> = _selectedArchetype.asStateFlow()

    private val _selectedLevel = MutableStateFlow(RoastLevel.SAVAGE)
    val selectedLevel: StateFlow<RoastLevel> = _selectedLevel.asStateFlow()

    private val _customTag = MutableStateFlow("")
    val customTag: StateFlow<String> = _customTag.asStateFlow()

    private val _useAI = MutableStateFlow(isApiKeyAvailable())
    val useAI: StateFlow<Boolean> = _useAI.asStateFlow()

    private val _isApiKeyConfigured = MutableStateFlow(isApiKeyAvailable())
    val isApiKeyConfigured: StateFlow<Boolean> = _isApiKeyConfigured.asStateFlow()

    private val _uiState = MutableStateFlow<RoastUiState>(RoastUiState.Idle)
    val uiState: StateFlow<RoastUiState> = _uiState.asStateFlow()

    private val _emojiReactions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val emojiReactions: StateFlow<Map<String, Int>> = _emojiReactions.asStateFlow()

    private val loadingMessages = listOf(
        "Scanning digital footprint...",
        "Measuring emotional sensitivity parameters...",
        "Inspecting your browser history (yikes)...",
        "Formulating heavy damage coordinates...",
        "Consulting the roast database...",
        "Analyzing NPC behaviors...",
        "Summoning the comedic spirits...",
        "Warming up the insult engine..."
    )

    fun setTargetName(name: String) {
        _targetName.value = name
    }

    fun setArchetype(arch: RoastArchetype) {
        _selectedArchetype.value = arch
    }

    fun setRoastLevel(level: RoastLevel) {
        _selectedLevel.value = level
    }

    fun setCustomTag(tag: String) {
        _customTag.value = tag
    }

    fun setUseAI(enabled: Boolean) {
        if (enabled && !isApiKeyAvailable()) return // Restrict if key placeholder
        _useAI.value = enabled
    }

    // New Easter Egg API Key configurations
    fun getCustomApiKey(): String {
        return prefs.getString("custom_gemini_api_key", "") ?: ""
    }

    fun saveCustomApiKey(key: String) {
        prefs.edit().putString("custom_gemini_api_key", key.trim()).apply()
        val available = isApiKeyAvailable()
        _isApiKeyConfigured.value = available
        // Automatically default AI state to enabled if a valid key is set!
        _useAI.value = available
    }

    // Check if a real Gemini API key is configured (either BuildConfig or User-entered)
    fun isApiKeyAvailable(): Boolean {
        if (getCustomApiKey().isNotEmpty()) return true

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "GEMINI_API_KEY"
    }

    fun incrementReaction(emoji: String) {
        val current = _emojiReactions.value.toMutableMap()
        val count = current[emoji] ?: 0
        current[emoji] = count + 1
        _emojiReactions.value = current
    }

    fun triggerRoast() {
        viewModelScope.launch {
            // Reset emoji reactions
            _emojiReactions.value = mapOf(
                "💀" to (10..50).random(),
                "😭" to (5..35).random(),
                "🔥" to (12..60).random(),
                "🤡" to (2..15).random()
            )

            val currentArchetype = _selectedArchetype.value
            val currentLevel = _selectedLevel.value
            val currentTarget = _targetName.value.trim()
            val currentCustom = _customTag.value.trim()

            // Step-by-step loading state animations
            _uiState.value = RoastUiState.Loading(loadingMessages.random())
            delay(800)
            _uiState.value = RoastUiState.Loading(loadingMessages.random())
            delay(700)

            val apiToUse = getCustomApiKey().ifEmpty {
                try {
                    BuildConfig.GEMINI_API_KEY
                } catch (e: Exception) {
                    ""
                }
            }

            val shouldTryAI = _useAI.value && apiToUse.isNotEmpty() && apiToUse != "MY_GEMINI_API_KEY" && apiToUse != "GEMINI_API_KEY"

            if (shouldTryAI) {
                try {
                    val systemPrompt = "You are an elite stand-up comedian, internet meme scientist, and multi-award-winning roast battle champion. " +
                            "Your mission is to craft an incredibly savage, high-IQ, and devastatingly witty punchline (maximum 130 characters) " +
                            "about the specified target based on their Archetype and personal details. " +
                            "Rules:\n" +
                            "- DO NOT output any introductions (e.g., 'Oh...', 'Well,', 'Here is your roast:'), conversational filler, explanation, or meta-commentary. Output ONLY the raw roast text.\n" +
                            "- Absolutely NO quotation marks around the output. Make sure it is clean text.\n" +
                            "- The roast MUST be under 130 characters. Keep it incredibly brief, punchy, and highly comedic so it fits on a meme card perfectly.\n" +
                            "- Rely on clever metaphors, modern tech/social references, and brilliant sarcasm rather than basic curses. Keep it PG-13 (strictly no hate speech, slurs, or real bigotry)."
 
                    var promptText = "Unleash a brilliant comedic roast targeting: ${if (currentTarget.isEmpty()) "me (the user)" else currentTarget}.\n" +
                            "Target's Archetype/Persona: ${currentArchetype.displayName} - ${currentArchetype.bio}.\n" +
                            "Spice Level: ${currentLevel.displayName} (${currentLevel.description}).\n"
                    
                    if (currentCustom.isNotEmpty()) {
                        promptText += "Embarrassing personal context/habits to exploit: $currentCustom.\n"
                    }

                    promptText += "Instructions:\n" +
                            "- Incorporate their name '${if (currentTarget.isEmpty()) "you" else currentTarget}' dynamically and structure the sentence beautifully.\n" +
                            "- Match the requested spice level style: Mild Buzz is cheeky; Savage Burn is a sharp slap; Obliteration is ultimate psychological comedic damage.\n" +
                            "- Deliver a devastatingly funny, meme-certified burn!"
 
                    val modelsToTry = listOf(
                        "gemini-2.0-flash",
                        "gemini-1.5-flash",
                        "gemini-2.5-flash",
                        "gemini-3.5-flash"
                    )
                    val safetyFilters = listOf(
                        SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_ONLY_HIGH"),
                        SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_ONLY_HIGH"),
                        SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_ONLY_HIGH"),
                        SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_ONLY_HIGH")
                    )
                    val request = GenerateContentRequest(
                        contents = listOf(
                            Content(parts = listOf(Part(text = promptText)))
                        ),
                        generationConfig = GenerationConfig(
                            temperature = 0.85f,
                            maxOutputTokens = 120
                        ),
                        systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                        safetySettings = safetyFilters
                    )

                    var aiText: String? = null
                    var modelUsed: String? = null
                    var lastException: Exception? = null

                    for (modelName in modelsToTry) {
                        try {
                            android.util.Log.d("RoastViewModel", "Trying model: $modelName with systemInstruction")
                            val response = RetrofitClient.service.generateContent(modelName, apiToUse, request)
                            val textResult = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                            if (!textResult.isNullOrEmpty()) {
                                aiText = textResult
                                modelUsed = modelName
                                break
                            }
                        } catch (e: Exception) {
                            var exceptionMessage = e.message ?: "Unknown error"
                            if (e is retrofit2.HttpException) {
                                try {
                                    val errorBody = e.response()?.errorBody()?.string()
                                    if (!errorBody.isNullOrEmpty()) {
                                        exceptionMessage = try {
                                            val json = org.json.JSONObject(errorBody)
                                            val errorObj = json.optJSONObject("error")
                                            errorObj?.optString("message") ?: json.optString("message", errorBody)
                                        } catch (jsonEx: Exception) {
                                            errorBody
                                        }
                                    }
                                } catch (ignored: Exception) {}
                            }
                            android.util.Log.e("RoastViewModel", "Failed model $modelName with systemInstruction: $exceptionMessage")
                            lastException = Exception(exceptionMessage, e)
                        }
                    }

                    // Fallback try: If all failed, attempt without systemInstruction (putting systemPrompt directly inside contents)
                    if (aiText.isNullOrEmpty()) {
                        val fallbackRequest = GenerateContentRequest(
                            contents = listOf(
                                Content(parts = listOf(Part(text = "System Rules:\n$systemPrompt\n\nPrompt:\n$promptText")))
                            ),
                            generationConfig = GenerationConfig(
                                temperature = 0.85f,
                                maxOutputTokens = 120
                            ),
                            safetySettings = safetyFilters
                        )
                        for (modelName in modelsToTry) {
                            try {
                                android.util.Log.d("RoastViewModel", "Trying model: $modelName without systemInstruction")
                                val response = RetrofitClient.service.generateContent(modelName, apiToUse, fallbackRequest)
                                val textResult = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                                if (!textResult.isNullOrEmpty()) {
                                    aiText = textResult
                                    modelUsed = modelName
                                    break
                                }
                            } catch (e: Exception) {
                                var exceptionMessage = e.message ?: "Unknown error"
                                if (e is retrofit2.HttpException) {
                                    try {
                                        val errorBody = e.response()?.errorBody()?.string()
                                        if (!errorBody.isNullOrEmpty()) {
                                            exceptionMessage = try {
                                                val json = org.json.JSONObject(errorBody)
                                                val errorObj = json.optJSONObject("error")
                                                errorObj?.optString("message") ?: json.optString("message", errorBody)
                                            } catch (jsonEx: Exception) {
                                                errorBody
                                            }
                                        }
                                    } catch (ignored: Exception) {}
                                    android.util.Log.e("RoastViewModel", "Failed model $modelName without systemInstruction: $exceptionMessage")
                                    lastException = Exception(exceptionMessage, e)
                                }
                            }
                        }
                    }

                    if (!aiText.isNullOrEmpty()) {
                        val cleanedText = aiText
                            .removeSurrounding("\"")
                            .removeSurrounding("\"")
                            .removeSurrounding("'")
                            .removeSurrounding("'")
                            .removeSurrounding("“")
                            .removeSurrounding("”")
                            .removeSurrounding("«")
                            .removeSurrounding("»")
                            .trim()
                        
                        // Check if response is broken or safety censored to just their name (length <= target length + 2)
                        val cmpTarget = currentTarget.ifEmpty { "you" }
                        val isOverlyShort = cleanedText.length <= (cmpTarget.length + 3) || 
                                cleanedText.equals(cmpTarget, ignoreCase = true) || 
                                cleanedText.equals("$cmpTarget's", ignoreCase = true) ||
                                cleanedText.split(" ").size <= 2

                        if (!isOverlyShort) {
                            _uiState.value = RoastUiState.Success(cleanedText, isAiGenerated = true, modelName = modelUsed)
                        } else {
                            // Automatically swap with high-quality localized roast personalized for standard, seamless user experience
                            val localRoast = RoastRepository.getRandomRoast(currentArchetype, currentLevel).text
                            val customizedRoast = personalizeRoast(localRoast, currentTarget)
                            _uiState.value = RoastUiState.Success(customizedRoast, isAiGenerated = false, modelName = "Curated Local Database")
                        }
                    } else {
                        throw lastException ?: Exception("Empty or blocked response from all tried Gemini models")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    android.util.Log.e("RoastViewModel", "API Key Burn Error: ", e)
                    // Fallback to local curated database if AI fails/times out
                    val localRoast = RoastRepository.getRandomRoast(currentArchetype, currentLevel).text
                    val customizedRoast = personalizeRoast(localRoast, currentTarget)
                    val errorMsg = e.localizedMessage ?: e.message ?: "Unknown API issue"
                    _uiState.value = RoastUiState.Error(
                        message = "AI server error: $errorMsg. Using pre-baked savage burns.",
                        fallbackRoast = customizedRoast
                    )
                }
            } else {
                // Offline generation
                val localRoast = RoastRepository.getRandomRoast(currentArchetype, currentLevel).text
                val customizedRoast = personalizeRoast(localRoast, currentTarget)
                _uiState.value = RoastUiState.Success(customizedRoast, isAiGenerated = false, modelName = "Curated Local Database")
            }
        }
    }

    private fun personalizeRoast(roast: String, target: String): String {
        if (target.isEmpty()) return roast
        
        // Let's replace standard "You " or "Your " with personalized name to make it feel amazing
        return if (roast.startsWith("You ", ignoreCase = true)) {
            val leftover = roast.substring(4)
            "$target, you $leftover"
        } else if (roast.startsWith("Your ", ignoreCase = true)) {
            val leftover = roast.substring(5)
            "Hey $target, your $leftover"
        } else {
            "Yo $target, $roast"
        }
    }
}
