package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.R
import com.example.roast.RoastArchetype
import com.example.roast.RoastLevel
import com.example.roast.RoastUiState
import com.example.roast.RoastViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoastScreen(
    viewModel: RoastViewModel,
    modifier: Modifier = Modifier
) {
    val targetName by viewModel.targetName.collectAsState()
    val selectedArchetype by viewModel.selectedArchetype.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val customTag by viewModel.customTag.collectAsState()
    val useAI by viewModel.useAI.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val reactions by viewModel.emojiReactions.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var lastToast by remember { mutableStateOf<Toast?>(null) }
    val showToast: (String) -> Unit = { message ->
        lastToast?.cancel()
        val toast = Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
        toast.show()
        lastToast = toast
    }

    // Splash State
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    // Base Modern Dark Theme Colors
    val onyxBlack = Color(0xFF0C0C0E)
    val cardBg = Color(0xFF16161A)
    val cardBorder = Color(0xFF2E2E36)
    val neonPink = Color(0xFFFF2A85)
    val neonYellow = Color(0xFFFFF01F)
    val neonPurple = Color(0xFF8B5CF6)
    val neonGreen = Color(0xFF39FF14)

    // Easter Egg API Key dialog state
    var eggClicks by remember { mutableStateOf(0) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    // ScrollState for parent scrollability
    val scrollState = rememberScrollState()

    if (showSplash) {
        MemeSplashLayout()
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(onyxBlack)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // --- HEADER ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔥 ROAST ME",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp,
                                fontSize = 32.sp
                            ),
                            color = neonPink,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                eggClicks++
                                if (eggClicks >= 7) {
                                    showApiKeyDialog = true
                                    eggClicks = 0
                                } else if (eggClicks >= 3) {
                                    val stepsRemaining = 7 - eggClicks
                                    showToast("You are $stepsRemaining steps away from the Hacker Cave... 🤫")
                                }
                            }
                        )
                    }
                    Text(
                        text = "Welcome to the emotional damage ward",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // --- INPUT FIELDS AND OPTIONS CARD ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Target Name
                        Text(
                            text = "👤 Who are we burning today?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        OutlinedTextField(
                            value = targetName,
                            onValueChange = { viewModel.setTargetName(it) },
                            placeholder = { Text("Enter name (or leave empty for yourself)", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = neonPink,
                                unfocusedBorderColor = cardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = neonPink
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        HorizontalDivider(color = cardBorder, thickness = 1.dp)

                        // Archetype Picker
                        Text(
                            text = "🎭 Select Victim Persona",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(RoastArchetype.values()) { archetype ->
                                val isSelected = selectedArchetype == archetype
                                val borderCol = if (isSelected) neonPink else cardBorder
                                val bgCol = if (isSelected) neonPink.copy(alpha = 0.15f) else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bgCol)
                                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setArchetype(archetype) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("archetype_${archetype.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = archetype.icon, fontSize = 18.sp)
                                        Text(
                                            text = archetype.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) neonPink else Color.LightGray
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Persona Bio
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(onyxBlack, RoundedCornerShape(10.dp))
                                .border(1.dp, cardBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Archetype Bio:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = neonPurple
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedArchetype.bio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        HorizontalDivider(color = cardBorder, thickness = 1.dp)

                        // Spiciness level
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌶️ Spiciness Intensity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        when (selectedLevel) {
                                            RoastLevel.MILD -> neonGreen.copy(alpha = 0.2f)
                                            RoastLevel.SAVAGE -> neonYellow.copy(alpha = 0.2f)
                                            RoastLevel.OBLITERATION -> neonPink.copy(alpha = 0.2f)
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        when (selectedLevel) {
                                            RoastLevel.MILD -> neonGreen
                                            RoastLevel.SAVAGE -> neonYellow
                                            RoastLevel.OBLITERATION -> neonPink
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = selectedLevel.displayName,
                                    color = when (selectedLevel) {
                                        RoastLevel.MILD -> neonGreen
                                        RoastLevel.SAVAGE -> neonYellow
                                        RoastLevel.OBLITERATION -> neonPink
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoastLevel.values().forEach { level ->
                                val isSelected = selectedLevel == level
                                val borderCol = if (isSelected) {
                                    when (level) {
                                        RoastLevel.MILD -> neonGreen
                                        RoastLevel.SAVAGE -> neonYellow
                                        RoastLevel.OBLITERATION -> neonPink
                                    }
                                } else cardBorder

                                val bgCol = if (isSelected) borderCol.copy(alpha = 0.1f) else Color.Transparent

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgCol)
                                        .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setRoastLevel(level) }
                                        .padding(vertical = 10.dp)
                                        .testTag("level_${level.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = when (level) {
                                                RoastLevel.MILD -> "☕"
                                                RoastLevel.SAVAGE -> "🌶️"
                                                RoastLevel.OBLITERATION -> "💀"
                                            },
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = level.displayName.split(" ").first(),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Optional Custom tags
                        var showCustomTag by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showCustomTag = !showCustomTag }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (showCustomTag) "▼ Hide extra details" else "▶ Add embarrassing detail (optional)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = neonPurple
                            )
                        }

                        AnimatedVisibility(
                            visible = showCustomTag,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                OutlinedTextField(
                                    value = customTag,
                                    onValueChange = { viewModel.setCustomTag(it) },
                                    placeholder = { Text("e.g. Scared of spiders, drinks decaf, heavy snorer", color = Color.Gray, fontSize = 13.sp) },
                                    maxLines = 2,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = neonPurple,
                                        unfocusedBorderColor = cardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = neonPurple
                                    ),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_tag_input")
                                )
                            }
                        }

                        HorizontalDivider(color = cardBorder, thickness = 1.dp)

                        // AI Toggle Box
                        val isApiKeyConfigured by viewModel.isApiKeyConfigured.collectAsState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(onyxBlack, RoundedCornerShape(12.dp))
                                .border(1.dp, cardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "✨",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "AI Cyber-Burner Mode",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colorSchemeAI(useAI, neonYellow)
                                        )
                                        Text(
                                            text = "Generates infinite unique roasts with Gemini",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Switch(
                                    checked = useAI,
                                    onCheckedChange = { viewModel.setUseAI(it) },
                                    enabled = isApiKeyConfigured,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = neonYellow,
                                        checkedTrackColor = neonYellow.copy(alpha = 0.3f),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier
                                        .scale(0.85f)
                                        .testTag("ai_toggle")
                                )
                            }

                            if (!isApiKeyConfigured) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Tap '🔥 ROAST ME' title 7 times to enter your Gemini API key, or add GEMINI_API_KEY to Secrets.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // --- MAIN ROAST ME BUTTON ---
                val transitionState = remember { MutableTransitionState(false) }
                val scaleAnimation by animateFloatAsState(
                    targetValue = if (uiState is RoastUiState.Loading) 0.95f else 1.0f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )

                Button(
                    onClick = { viewModel.triggerRoast() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .scale(scaleAnimation)
                        .testTag("roast_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(neonPink, neonPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState is RoastUiState.Loading) "🔥 GATHERING EMOTIONAL DAMAGE..." else "🔥 ROAST ME !",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                // --- ROAST RESULTS CARD ---
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "RoastStateAnimation"
                ) { state ->
                    when (state) {
                        is RoastUiState.Idle -> {
                            MemeCardShell(gradientBorder = false) {
                                Text(
                                    text = "💀 WAITING TO DESTROY LIFE 💀",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Adjust settings above, write a name if you have an active target, and click ROAST ME. No feelings will be spared.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        is RoastUiState.Loading -> {
                            MemeCardShell(gradientBorder = true) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = neonPink,
                                        strokeWidth = 4.dp,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = state.message,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Brace yourself...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        is RoastUiState.Success -> {
                            RoastResultDisplay(
                                roastText = state.roastText,
                                isAi = state.isAiGenerated,
                                isErrorFallback = false,
                                reactions = reactions,
                                onReactionClick = { viewModel.incrementReaction(it) },
                                onShareClick = {
                                    coroutineScope.launch {
                                        shareMemeCardImage(context, state.roastText, targetName, reactions, state.isAiGenerated, state.modelName)
                                    }
                                },
                                onCopyClick = {
                                    clipboardManager.setText(AnnotatedString(state.roastText))
                                    showToast("Copied roast to clipboard!")
                                },
                                targetName = targetName,
                                modelName = state.modelName
                            )
                        }

                        is RoastUiState.Error -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Connection/key warning label
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "⚠️ ${state.message}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFDC2626)
                                    )
                                }

                                // Show local fallback roast within card
                                RoastResultDisplay(
                                    roastText = state.fallbackRoast,
                                    isAi = false,
                                    isErrorFallback = true,
                                    reactions = reactions,
                                    onReactionClick = { viewModel.incrementReaction(it) },
                                    onShareClick = {
                                        coroutineScope.launch {
                                            shareMemeCardImage(context, state.fallbackRoast, targetName, reactions, false, "Curated Local Database")
                                        }
                                    },
                                    onCopyClick = {
                                        clipboardManager.setText(AnnotatedString(state.fallbackRoast))
                                        showToast("Copied fallback roast to clipboard!")
                                    },
                                    targetName = targetName,
                                    modelName = "Curated Local Database"
                                )
                            }
                        }
                    }
                }
            }


            // --- SECRET API KEY CONFIGURATION DIALOG (EASTER EGG) ---
            if (showApiKeyDialog) {
                var keyInput by remember { mutableStateOf(viewModel.getCustomApiKey()) }
                AlertDialog(
                    onDismissRequest = { showApiKeyDialog = false },
                    title = {
                        Text(
                            "🤫 HACKER DECK: API CONFIG",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = neonYellow,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Enter your personal Gemini API key here to bypass global server settings and run DYNAMIC mode:",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                            OutlinedTextField(
                                value = keyInput,
                                onValueChange = { keyInput = it },
                                placeholder = { Text("AIzaSy...", color = Color.Gray) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = neonPink,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                "Will be stored locally inside private sandbox Preferences.",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.saveCustomApiKey(keyInput)
                                showToast("API Key configured! Cyber-burner mode unlocked.")
                                showApiKeyDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = neonPink)
                        ) {
                            Text("SAVE KEY", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showApiKeyDialog = false }) {
                            Text("ABORT", color = Color.Gray)
                        }
                    },
                    containerColor = cardBg,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, neonPink, RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
fun MemeCardShell(
    gradientBorder: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardBg = Color(0xFF16161A)
    val borderCol = Color(0xFF2E2E36)
    val neonPink = Color(0xFFFF2A85)
    val neonYellow = Color(0xFFFFF01F)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (gradientBorder) {
                    Brush.linearGradient(listOf(neonPink, neonYellow))
                } else {
                    Brush.linearGradient(listOf(borderCol, borderCol))
                }
            )
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun RoastResultDisplay(
    roastText: String,
    isAi: Boolean,
    isErrorFallback: Boolean,
    reactions: Map<String, Int>,
    onReactionClick: (String) -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    targetName: String = "",
    modelName: String? = null
) {
    val neonPink = Color(0xFFFF2A85)
    val neonYellow = Color(0xFFFFF01F)
    val cardBorder = Color(0xFF2E2E36)

    MemeCardShell(gradientBorder = true) {
        // Tag label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isAi) neonYellow.copy(alpha = 0.25f) else neonPink.copy(alpha = 0.25f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isAi) "⚡ DYNAMIC AI BURN" else "💀 CURATED CLASSIC BURN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isAi) neonYellow else neonPink
                )
            }

            if (targetName.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFFF2A85).copy(alpha = 0.15f),
                            RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, Color(0xFFFF2A85), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎯 TARGET: ${targetName.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color(0xFFFF2A85)
                    )
                }
            } else if (isErrorFallback) {
                Text(
                    text = "Fallback mode active",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }
        }

        if (!modelName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isAi) neonYellow else neonPink)
                )
                Text(
                    text = "ENGINE: ${modelName.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large quote layout
        Text(
            text = "“",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif
            ),
            color = neonPink.copy(alpha = 0.4f),
            modifier = Modifier
                .height(30.dp)
                .offset(y = (-10).dp)
        )

        Text(
            text = roastText,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                lineHeight = 28.sp,
                fontSize = 20.sp
            ),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("roast_display_text")
        )

        Text(
            text = "”",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif
            ),
            color = neonPink.copy(alpha = 0.4f),
            modifier = Modifier
                .height(30.dp)
                .align(Alignment.End)
                .offset(y = (-10).dp)
        )

        HorizontalDivider(color = cardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

        // EMOJI REACTION CHIPS (spring animation counters)
        Text(
            text = "Audience Reaction (tap to pile on):",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            reactions.forEach { (emoji, count) ->
                key(emoji) {
                    var scaleState by remember { mutableStateOf(1f) }
                    val scale by animateFloatAsState(
                        targetValue = scaleState,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        finishedListener = { scaleState = 1f }
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color(0xFF222226))
                            .border(1.dp, Color(0xFF2E2E36), RoundedCornerShape(30.dp))
                            .clickable {
                                scaleState = 1.3f
                                onReactionClick(emoji)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("emoji_chip_$emoji"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = emoji, fontSize = 14.sp)
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CALL-TO-ACTIONS: Copy and Share Image
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCopyClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = neonYellow
                ),
                border = BorderStroke(1.dp, neonYellow.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("copy_button")
            ) {
                Text(
                    text = "📋",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Burn", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onShareClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = neonPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(44.dp)
                    .testTag("share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share Card 🖼️", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun MemeSplashLayout() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Launcher Icon representation with stylish border glow
            Image(
                painter = painterResource(id = R.drawable.roast_icon),
                contentDescription = "MemeRoaster Fire Icon",
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, Color(0xFFFF2A85), RoundedCornerShape(32.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Text Banner
            Text(
                text = "ROASTMASTER PRO",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    fontSize = 28.sp
                ),
                color = Color(0xFFFF2A85)
            )

            val tickerMessages = listOf(
                "Sharpening comedic insults...",
                "Warming up cyber-burners...",
                "Calculating emotional damage thresholds...",
                "Gathering offline burn packs..."
            )
            var currentMessageIndex by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(500)
                    currentMessageIndex = (currentMessageIndex + 1) % tickerMessages.size
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFFFF01F),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                     text = tickerMessages[currentMessageIndex],
                     style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                     color = Color.Gray
                )
            }
        }
    }
}

// Helpers
private fun colorSchemeAI(useAIEnabled: Boolean, activeColor: Color): Color {
    return if (useAIEnabled) activeColor else Color.Gray
}

private fun drawMemeCard(
    context: Context,
    roastText: String,
    targetName: String,
    reactions: Map<String, Int>,
    isAi: Boolean,
    modelName: String? = null
): Uri? {
    try {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw gradient border background
        val borderPaint = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                android.graphics.Color.parseColor("#FF2A85"), // neonPinkHex
                android.graphics.Color.parseColor("#FFF01F"), // neonYellowHex
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

        // 2. Draw card inner body
        val innerMargin = 16f
        val bodyPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#16161A") // cardBgHex
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRect(innerMargin, innerMargin, width - innerMargin, height - innerMargin, bodyPaint)

        // 3. Draw Watermark/Header at top
        val titlePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#FF2A85")
            textSize = 42f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("⚡ ROASTME PRO ™", width / 2f, 130f, titlePaint)

        val badgePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#2E2E36")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val badgeText = if (isAi) "DYNAMIC AI CYBER-BURN" else "CURATED CLASSIC BURN"
        val badgeTextPaint = Paint().apply {
            color = if (isAi) android.graphics.Color.parseColor("#FFF01F") else android.graphics.Color.parseColor("#FF2A85")
            textSize = 26f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        // Background for badge
        val rectF = RectF(width / 2f - 240f, 180f, width / 2f + 240f, 230f)
        canvas.drawRoundRect(rectF, 12f, 12f, badgePaint)
        canvas.drawText(badgeText, width / 2f, 215f, badgeTextPaint)

        // Draw active target name if present
        if (targetName.isNotBlank()) {
            val targetBadgePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#1B1115") // dark rosy red tint
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            val targetBorderPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#FF2A85") // neon pink
                style = Paint.Style.STROKE
                strokeWidth = 3f
                isAntiAlias = true
            }
            val targetTextPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#FF2A85")
                textSize = 28f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            val tRectF = RectF(width / 2f - 250f, 255f, width / 2f + 250f, 310f)
            canvas.drawRoundRect(tRectF, 8f, 8f, targetBadgePaint)
            canvas.drawRoundRect(tRectF, 8f, 8f, targetBorderPaint)
            canvas.drawText("🎯 TARGET: ${targetName.uppercase()}", width / 2f, 293f, targetTextPaint)
        }

        // Engine hint on canvas
        if (!modelName.isNullOrBlank()) {
            val enginePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#9CA3AF")
                textSize = 24f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("ENGINE: ${modelName.uppercase()}", width / 2f, 350f, enginePaint)
        }

        // 4. Draw quotation mark (Stylized giant quote behind text)
        val quotePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1AFF2A85") // Semi-transparent pink
            textSize = 280f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        canvas.drawText("“", 80f, 400f, quotePaint)

        // 5. Draw body text (With Wrapping)
        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 46f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val words = roastText.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        val maxTextWidth = width - 220f

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val measuredWidth = textPaint.measureText(testLine)
            if (measuredWidth > maxTextWidth) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        // Center text block vertically on canvas
        val fontMetrics = textPaint.fontMetrics
        val lineHeight = fontMetrics.bottom - fontMetrics.top + 12f
        var y = (height / 2f) - ((lines.size - 1) * lineHeight / 2f) + 40f

        for (line in lines) {
            canvas.drawText(line, width / 2f, y, textPaint)
            y += lineHeight
        }

        // 6. Draw ending quote
        canvas.drawText("”", width - 150f, y + 40f, quotePaint)

        // 7. Draw Reaction Counter at bottom
        val reactionPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#9CA3AF") // gray-400
            textSize = 34f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val reactionString = StringBuilder()
        reactionString.append("CARD ACTIONS: ")
        reactions.forEach { (emoji, count) ->
            reactionString.append("$emoji $count   ")
        }
        canvas.drawText(reactionString.toString(), width / 2f, height - 140f, reactionPaint)

        // Footer note
        val footerPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#6B7280") // gray-500
            textSize = 24f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Shared via ROASTMASTER PRO 🔥 Download on Android", width / 2f, height - 70f, footerPaint)

        // 8. Save to cache directory
        val imagesDir = File(context.cacheDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val file = File(imagesDir, "roast_meme.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        // Get Uri using our explicit provider authority
        return FileProvider.getUriForFile(
            context,
            "com.aistudio.memeroaster.kxmpzq.fileprovider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

private suspend fun shareMemeCardImage(
    context: Context,
    roastText: String,
    targetName: String,
    reactions: Map<String, Int>,
    isAi: Boolean,
    modelName: String? = null
) {
    try {
        val imageUri = withContext(Dispatchers.IO) {
            drawMemeCard(context, roastText, targetName, reactions, isAi, modelName)
        }
        withContext(Dispatchers.Main) {
            if (imageUri != null) {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, "Look at this absolutely savage roast! 🔥 \n\n— Created on RoastMaster Pro.")
                    type = "image/png"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val shareIntent = Intent.createChooser(sendIntent, "Send roast card to victim:").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(shareIntent)
            } else {
                // Fallback to text sharing
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$roastText \n\n— Shared via RoastMaster Pro! 🔥")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Roast:").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(shareIntent)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(context.applicationContext, "Sharing failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
