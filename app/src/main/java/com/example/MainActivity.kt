package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class HandBookTab {
    SETUP,
    SANDBOX,
    GEMINI_BOT,
    DEPLOYMENT
}

enum class SandboxTemplate {
    GREETING,
    COUNTER,
    TODO_LIST
}

data class ChatItem(
    val sender: String,
    val message: String,
    val isUser: Boolean,
    val codeBlock: String? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModernAndroidHandbookApp()
        }
    }
}

// Custom Premium Clean Minimalism Palette for Android 2026 Theme
val DeepMidnight = Color(0xFFF8FAFC) // Global off-white background (Slate 50)
val CardSlate = Color(0xFFFFFFFF)    // Crisp white elements (Slate White)
val TechPurple = Color(0xFF0F172A)   // Dominant Slate 900 for primary typography and buttons
val NeonCyan = Color(0xFF3DDC84)     // Native Android Green accent
val AccentMint = Color(0xFF3DDC84)   // Android Green success status
val GhostWhite = Color(0xFF0F172A)   // Standard slate black body typography (Slate 900)
val CustomGrey = Color(0xFF64748B)   // Subtitle body text (Slate 500)
val CodeBg = Color(0xFF0F172A)       // Code block background

@Composable
fun ModernAndroidHandbookApp() {
    var activeTab by remember { mutableStateOf(HandBookTab.SETUP) }
    
    // Checklist state for Step 1
    var step1Checked by remember { mutableStateOf(false) }
    var step2Checked by remember { mutableStateOf(false) }
    var step3Checked by remember { mutableStateOf(false) }
    
    // Stack Card detail views
    var selectedStackDetail by remember { mutableStateOf<String?>(null) }
    
    // Sandbox State
    var currentTemplate by remember { mutableStateOf(SandboxTemplate.GREETING) }
    var greetingNameInput by remember { mutableStateOf("") }
    var interactiveCount by remember { mutableIntStateOf(0) }
    
    // Tasks list state inside AVD
    val interactiveTasks = remember { 
        mutableStateListOf(
            "Review build.gradle",
            "Enable Kotlin 2.2 Compiler",
            "Test on foldable emulator"
        )
    }
    var newTaskTextInput by remember { mutableStateOf("") }
    
    // Chat state for Step 4 (Gemini Bot)
    val chatHistory = remember { 
        mutableStateListOf(
            ChatItem(
                sender = "Gemini AI", 
                message = "Hi! I am Gemini inside Android Studio 2026. Ask me anything about modern Kotlin 2.2 development or Jetpack Compose UI. Try selecting a preset quick prompt below!", 
                isUser = false
            )
        )
    }
    var userChatText by remember { mutableStateOf("") }
    var isAITyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Deployment state
    var deviceEmulationMode by remember { mutableStateOf("Phone") }
    var isPublishingActive by remember { mutableStateOf(false) }
    var publishProgress by remember { mutableStateOf(0f) }
    val publishLogs = remember { mutableStateListOf<String>() }
    var isPublishComplete by remember { mutableStateOf(false) }
    
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = TechPurple,
            secondary = NeonCyan,
            background = DeepMidnight,
            surface = CardSlate,
            onBackground = GhostWhite,
            onSurface = GhostWhite
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Column {
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    NavigationBar(
                        containerColor = CardSlate,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == HandBookTab.SETUP,
                            onClick = { activeTab = HandBookTab.SETUP },
                            icon = { Icon(Icons.Default.Build, contentDescription = "Set Up") },
                            label = { Text("Set Up", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechPurple,
                                selectedTextColor = TechPurple,
                                unselectedIconColor = CustomGrey,
                                unselectedTextColor = CustomGrey,
                                indicatorColor = DeepMidnight
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == HandBookTab.SANDBOX,
                            onClick = { activeTab = HandBookTab.SANDBOX },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Sandbox") },
                            label = { Text("Sandbox", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechPurple,
                                selectedTextColor = TechPurple,
                                unselectedIconColor = CustomGrey,
                                unselectedTextColor = CustomGrey,
                                indicatorColor = DeepMidnight
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == HandBookTab.GEMINI_BOT,
                            onClick = { activeTab = HandBookTab.GEMINI_BOT },
                            icon = { Icon(Icons.Default.Face, contentDescription = "Gemini AI") },
                            label = { Text("Gemini AI", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechPurple,
                                selectedTextColor = TechPurple,
                                unselectedIconColor = CustomGrey,
                                unselectedTextColor = CustomGrey,
                                indicatorColor = DeepMidnight
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == HandBookTab.DEPLOYMENT,
                            onClick = { activeTab = HandBookTab.DEPLOYMENT },
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Deploy") },
                            label = { Text("Deploy", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechPurple,
                                selectedTextColor = TechPurple,
                                unselectedIconColor = CustomGrey,
                                unselectedTextColor = CustomGrey,
                                indicatorColor = DeepMidnight
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepMidnight)
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    HandBookTab.SETUP -> {
                        SetupHandbookView(
                            step1Checked = step1Checked,
                            onStep1Change = { step1Checked = it },
                            step2Checked = step2Checked,
                            onStep2Change = { step2Checked = it },
                            step3Checked = step3Checked,
                            onStep3Change = { step3Checked = it },
                            selectedStackDetail = selectedStackDetail,
                            onStackDetailChange = { selectedStackDetail = it }
                        )
                    }
                    HandBookTab.SANDBOX -> {
                        SandboxWalkthroughView(
                            currentTemplate = currentTemplate,
                            onTemplateChange = { currentTemplate = it },
                            greetingNameInput = greetingNameInput,
                            onGreetingNameInput = { greetingNameInput = it },
                            interactiveCount = interactiveCount,
                            onInteractiveChange = { interactiveCount = it },
                            interactiveTasks = interactiveTasks,
                            newTaskTextInput = newTaskTextInput,
                            onNewTaskTextInput = { newTaskTextInput = it },
                            onAddTask = { 
                                if (newTaskTextInput.isNotBlank()) {
                                    interactiveTasks.add(newTaskTextInput)
                                    newTaskTextInput = ""
                                }
                            },
                            onDeleteTask = { task -> interactiveTasks.remove(task) }
                        )
                    }
                    HandBookTab.GEMINI_BOT -> {
                        GeminiAISimulatorView(
                            chatHistory = chatHistory,
                            userChatText = userChatText,
                            isAITyping = isAITyping,
                            onUserChatTextChange = { userChatText = it },
                            onSendMessage = {
                                if (userChatText.isNotBlank() && !isAITyping) {
                                    val userMsg = userChatText
                                    chatHistory.add(ChatItem("You", userMsg, true))
                                    userChatText = ""
                                    isAITyping = true
                                    
                                    coroutineScope.launch {
                                        delay(1200)
                                        val responsePair = simulateGeminiResponse(userMsg)
                                        chatHistory.add(ChatItem("Gemini AI", responsePair.first, false, responsePair.second))
                                        isAITyping = false
                                    }
                                }
                            },
                            onSendPreset = { prompt ->
                                if (!isAITyping) {
                                    chatHistory.add(ChatItem("You", prompt, true))
                                    isAITyping = true
                                    
                                    coroutineScope.launch {
                                        delay(1200)
                                        val responsePair = simulateGeminiResponse(prompt)
                                        chatHistory.add(ChatItem("Gemini AI", responsePair.first, false, responsePair.second))
                                        isAITyping = false
                                    }
                                }
                            }
                        )
                    }
                    HandBookTab.DEPLOYMENT -> {
                        VerifyDeployView(
                            deviceEmulationMode = deviceEmulationMode,
                            onDeviceModeChange = { deviceEmulationMode = it },
                            isPublishingActive = isPublishingActive,
                            publishProgress = publishProgress,
                            publishLogs = publishLogs,
                            isPublishComplete = isPublishComplete,
                            onTriggerPublish = {
                                if (!isPublishingActive) {
                                    isPublishingActive = true
                                    isPublishComplete = false
                                    publishProgress = 0f
                                    publishLogs.clear()
                                    
                                    coroutineScope.launch {
                                        publishLogs.add("⚡ Instantiating release configuration pipeline...")
                                        delay(400)
                                        publishProgress = 0.2f
                                        publishLogs.add("⚙️ Checking project dependencies (Kotlin 2.2)... Checked!")
                                        delay(400)
                                        publishProgress = 0.4f
                                        publishLogs.add("🔨 Running Compose UI static compilers & optimization checks...")
                                        delay(400)
                                        publishProgress = 0.6f
                                        publishLogs.add("📦 Compiling release binaries via Gradle: :app:assembleRelease")
                                        delay(400)
                                        publishProgress = 0.75f
                                        publishLogs.add("🔒 Optimizing DEX layouts with ProGuard R8 system. APK shrunk by 38%.")
                                        delay(400)
                                        publishProgress = 0.9f
                                        publishLogs.add("🌐 Uploading generated Bundle to Play Console Internal Testing Channel...")
                                        delay(400)
                                        publishProgress = 1.0f
                                        publishLogs.add("🎉 Upload Successful! Version v1.0.0 is Live on the internal test pool.")
                                        isPublishComplete = true
                                        isPublishingActive = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 1: Setup & MAD Stack Tour Helpers
// ==========================================
@Composable
fun SetupHandbookView(
    step1Checked: Boolean,
    onStep1Change: (Boolean) -> Unit,
    step2Checked: Boolean,
    onStep2Change: (Boolean) -> Unit,
    step3Checked: Boolean,
    onStep3Change: (Boolean) -> Unit,
    selectedStackDetail: String?,
    onStackDetailChange: (String?) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // App Core Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(TechPurple, NeonCyan.copy(alpha = 0.7f))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Android 2026 Developer Guide",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Build modern Jetpack Compose layouts with Kotlin 2.2 and Google Gemini AI integration in Android Studio Panda 4.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Checklist Area
        Text(
            text = "Step 1: Environment Readiness Setup 🛠️",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GhostWhite
        )
        Text(
            text = "Ensure your tools match current 2026 industry prerequisites:",
            fontSize = 12.sp,
            color = CustomGrey,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Checklist progress
                val activeCount = listOf(step1Checked, step2Checked, step3Checked).count { it }
                val progressPercent = (activeCount / 3.0f)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flight Checklist Readiness",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${(progressPercent * 100).toInt()}%",
                        color = if (progressPercent == 1.0f) AccentMint else TechPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                
                LinearProgressIndicator(
                    progress = { progressPercent },
                    color = if (progressPercent == 1.0f) AccentMint else TechPurple,
                    trackColor = Color(0xFFE2E8F0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ChecklistRow(
                    text = "Android Studio Panda (or newer) downloaded",
                    checked = step1Checked,
                    onCheckedChange = onStep1Change
                )
                ChecklistRow(
                    text = "Standard Android 15/16 SDK & AVD profiles installed",
                    checked = step2Checked,
                    onCheckedChange = onStep2Change
                )
                ChecklistRow(
                    text = "Google account authenticated inside the IDE to wake Gemini SDK",
                    checked = step3Checked,
                    onCheckedChange = onStep3Change
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Tech Stack Information Grid
        Text(
            text = "Step 2: MAD Tech Stack Handbook 🚀",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GhostWhite
        )
        Text(
            text = "Modern Android Development (MAD) architectures. Tap on any tile to expand details:",
            fontSize = 12.sp,
            color = CustomGrey,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StackItemCard(
                title = "Kotlin 2.2",
                subtitle = "Language of 2026",
                icon = Icons.Default.Info,
                color = TechPurple,
                modifier = Modifier.weight(1f),
                onClick = { onStackDetailChange("kotlin") }
            )
            StackItemCard(
                title = "Jetpack Compose",
                subtitle = "Declarative DSL",
                icon = Icons.Default.Build,
                color = NeonCyan,
                modifier = Modifier.weight(1f),
                onClick = { onStackDetailChange("compose") }
            )
            StackItemCard(
                title = "MVVM Engine",
                subtitle = "Structural Integrity",
                icon = Icons.Default.CheckCircle,
                color = AccentMint,
                modifier = Modifier.weight(1f),
                onClick = { onStackDetailChange("mvvm") }
            )
        }
        
        // Expanded Stack Card Details
        selectedStackDetail?.let { activeType ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (activeType) {
                                "kotlin" -> "Kotlin 2.2 + Coroutines features"
                                "compose" -> "Stateful UI with Jetpack Compose"
                                "mvvm" -> "The MVVM (Model-View-ViewModel) Pattern"
                                else -> ""
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechPurple
                        )
                        IconButton(onClick = { onStackDetailChange(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = CustomGrey)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (activeType) {
                            "kotlin" -> "Kotlin 2.2 introduces blazing fast K2 compilation passes as standard. Its native Null-Safety eliminates the classic NullPointerException crashes, and its light-weight Coroutines and Flows allow you to run heavy background tasks, API fetch requests, or room indexing queries with incredibly smooth 120Hz scrolling on device screens."
                            "compose" -> "Jetpack Compose completely replaces old XML style layouts. The interface is defined as simple, reactive Kotlin functions annotated with @Composable. Instead of manually finding views and modifying their text attributes, Compose automatically detects changes in your @remembered state wrappers and reconstructs (recomposes) the UI nodes securely."
                            "mvvm" -> "Model-View-ViewModel decouples your UI rendering from business rules. The View listens to state variables published by the independent ViewModel. This makes the code exceptionally easy to test, scales perfectly without inflating file sizes, and persists layout actions even when the device is folded or rotated."
                            else -> ""
                        },
                        fontSize = 12.sp,
                        color = GhostWhite,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ChecklistRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AccentMint,
                uncheckedColor = CustomGrey
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (checked) GhostWhite else CustomGrey,
            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

@Composable
fun StackItemCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GhostWhite)
                Text(text = subtitle, fontSize = 10.sp, color = CustomGrey)
            }
        }
    }
}


// ==========================================
// SCREEN 2: Code Editor & Live AVD Simulator Helpers
// ==========================================
@Composable
fun SandboxWalkthroughView(
    currentTemplate: SandboxTemplate,
    onTemplateChange: (SandboxTemplate) -> Unit,
    greetingNameInput: String,
    onGreetingNameInput: (String) -> Unit,
    interactiveCount: Int,
    onInteractiveChange: (Int) -> Unit,
    interactiveTasks: List<String>,
    newTaskTextInput: String,
    onNewTaskTextInput: (String) -> Unit,
    onAddTask: () -> Unit,
    onDeleteTask: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Step 3: Interactive Code Sandbox 🔬",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GhostWhite
        )
        Text(
            text = "Switch Compose presets to inspect the real Kotlin syntax, and test the rendering in the virtual Android device emulator!",
            fontSize = 12.sp,
            color = CustomGrey,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Code Preset Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PresetTabButton(
                title = "Greeting Screen",
                isSelected = currentTemplate == SandboxTemplate.GREETING,
                onClick = { onTemplateChange(SandboxTemplate.GREETING) },
                modifier = Modifier.weight(1f)
            )
            PresetTabButton(
                title = "State Counter",
                isSelected = currentTemplate == SandboxTemplate.COUNTER,
                onClick = { onTemplateChange(SandboxTemplate.COUNTER) },
                modifier = Modifier.weight(1f)
            )
            PresetTabButton(
                title = "Task Manager",
                isSelected = currentTemplate == SandboxTemplate.TODO_LIST,
                onClick = { onTemplateChange(SandboxTemplate.TODO_LIST) },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Main split-like UI workspace
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // IDE Code window
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CodeBg)
                    .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp))
            ) {
                // Editor File Tab Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSlate)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF05032))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (currentTemplate) {
                            SandboxTemplate.GREETING -> "GreetingScreen.kt"
                            SandboxTemplate.COUNTER -> "CounterScreen.kt"
                            SandboxTemplate.TODO_LIST -> "TaskManagerScreen.kt"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GhostWhite,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Code rendering text block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    CodeTextBlock(currentTemplate)
                }
            }
            
            // Virtual Android Emulator Frame UI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LIVE EMULATOR SCREEN (AVD) 📱",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechPurple,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                // Phone Silhouette Wrapper
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF07070B))
                        .border(4.dp, Color(0xFF222230), RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFFBFE)) // Material 3 Standard baseline soft white light theme preview
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        // Virtual App Header & Status bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "15:58",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            // Screen punch-hole mock
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Build, 
                                    contentDescription = null, 
                                    tint = Color.Black, 
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "100%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                        
                        // Emulator inner title
                        Text(
                            text = when (currentTemplate) {
                                SandboxTemplate.GREETING -> "Welcome App"
                                SandboxTemplate.COUNTER -> "State Counter 2026"
                                SandboxTemplate.TODO_LIST -> "Local Task Board"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1C1B1F),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        
                        // Active Screen content switching inside emulator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            when (currentTemplate) {
                                SandboxTemplate.GREETING -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = greetingNameInput,
                                            onValueChange = { onGreetingNameInput(it) },
                                            label = { Text("Enter your name", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF6750A4),
                                                unfocusedBorderColor = Color(0xFF79747E),
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        
                                        AnimatedVisibility(
                                            visible = greetingNameInput.isNotEmpty(),
                                            enter = fadeIn() + expandVertically(),
                                            exit = fadeOut() + shrinkVertically()
                                        ) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DEF8)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text(
                                                        text = "Hello, $greetingNameInput! Welcome to 2026.",
                                                        color = Color(0xFF21005D),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                SandboxTemplate.COUNTER -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Remember State Counter Demo",
                                            fontSize = 10.sp,
                                            color = Color(0xFF49454F)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "$interactiveCount",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1C1B1F)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = { onInteractiveChange(interactiveCount + 1) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                            ) {
                                                Text("+ Add", fontSize = 10.sp)
                                            }
                                            Button(
                                                onClick = { if (interactiveCount > 0) onInteractiveChange(interactiveCount - 1) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4).copy(0.7f)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                            ) {
                                                Text("- Dec", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                                SandboxTemplate.TODO_LIST -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = newTaskTextInput,
                                                onValueChange = { onNewTaskTextInput(it) },
                                                placeholder = { Text("New task...", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF6750A4),
                                                    unfocusedBorderColor = Color(0xFF79747E),
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black
                                                )
                                            )
                                            Button(
                                                onClick = onAddTask,
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                                contentPadding = PaddingValues(0.dp),
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(90.dp)
                                                .verticalScroll(rememberScrollState())
                                        ) {
                                            interactiveTasks.forEach { item ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 1.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(6.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFF6750A4))
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = item,
                                                            fontSize = 10.sp,
                                                            color = Color.Black,
                                                            maxLines = 1
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = { onDeleteTask(item) },
                                                        modifier = Modifier.size(16.dp)
                                                    ) {
                                                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Red.copy(0.7f), modifier = Modifier.size(10.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) TechPurple else CardSlate,
            contentColor = if (isSelected) Color.White else CustomGrey
        ),
        border = BorderStroke(1.dp, if (isSelected) TechPurple else Color.White.copy(0.12f)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        modifier = modifier
    ) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CodeLineText(indent: Int = 0, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.padding(start = (indent * 16).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

// Emits beautiful syntax highlighted code snippets
@Composable
fun CodeTextBlock(template: SandboxTemplate) {
    val keywordColor = Color(0xFFFF79C6)
    val functionColor = Color(0xFF50FA7B)
    val stringColor = Color(0xFFF1FA8C)
    val annotationColor = Color(0xFFBD93F9)
    val standardColor = Color(0xFFF8F8F2)
    val commentColor = Color(0xFF6272A4)
    
    when (template) {
        SandboxTemplate.GREETING -> {
            Column {
                Text(text = "// Basic Hello World Jetpack Compose layout in Kotlin 2.2", color = commentColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                CodeLineText {
                    Text("@Composable", color = annotationColor)
                }
                CodeLineText {
                    Text("fun ", color = keywordColor)
                    Text("GreetingScreen() {", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("var ", color = keywordColor)
                    Text("name ", color = standardColor)
                    Text("by ", color = keywordColor)
                    Text("remember { mutableStateOf(\"\") }", color = standardColor)
                }
                Spacer(modifier = Modifier.height(4.dp))
                CodeLineText(indent = 1) {
                    Text("Column(modifier = Modifier.padding(16.dp)) {", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("TextField(", color = functionColor)
                }
                CodeLineText(indent = 3) {
                    Text("value = name,", color = standardColor)
                }
                CodeLineText(indent = 3) {
                    Text("onValueChange = { name = it },", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text(")", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("if ", color = keywordColor)
                    Text("(name.isNotEmpty()) {", color = standardColor)
                }
                CodeLineText(indent = 3) {
                    Text("Text(text = ", color = functionColor)
                    Text("\"Hello, \$name! Welcome to 2026.\"", color = stringColor)
                    Text(")", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("}", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("}", color = standardColor)
                }
                CodeLineText {
                    Text("}", color = standardColor)
                }
            }
        }
        SandboxTemplate.COUNTER -> {
            Column {
                Text(text = "// Remember State implementation with numerical state", color = commentColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                CodeLineText {
                    Text("@Composable", color = annotationColor)
                }
                CodeLineText {
                    Text("fun ", color = keywordColor)
                    Text("CounterScreen() {", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("var ", color = keywordColor)
                    Text("count ", color = standardColor)
                    Text("by ", color = keywordColor)
                    Text("remember { mutableStateOf(0) }", color = standardColor)
                }
                Spacer(modifier = Modifier.height(4.dp))
                CodeLineText(indent = 1) {
                    Text("Column(horizontalAlignment = Alignment.CenterHorizontally) {", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("Text(", color = functionColor)
                    Text("\"Count: \$count\"", color = stringColor)
                    Text(", fontSize = 32.sp)", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("Row {", color = standardColor)
                }
                CodeLineText(indent = 3) {
                    Text("Button(onClick = { count++ }) { Text(", color = functionColor)
                    Text("\"+ Add\"", color = stringColor)
                    Text(") }", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("}", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("}", color = standardColor)
                }
                CodeLineText {
                    Text("}", color = standardColor)
                }
            }
        }
        SandboxTemplate.TODO_LIST -> {
            Column {
                Text(text = "// MutableStateList showing list observation in Compose", color = commentColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                CodeLineText {
                    Text("@Composable", color = annotationColor)
                }
                CodeLineText {
                    Text("fun ", color = keywordColor)
                    Text("TaskManager() {", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("val ", color = keywordColor)
                    Text("tasks = remember { mutableStateListOf<String>() }", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("var ", color = keywordColor)
                    Text("text by remember { mutableStateOf(\"\") }", color = standardColor)
                }
                Spacer(modifier = Modifier.height(4.dp))
                CodeLineText(indent = 1) {
                    Text("Column {", color = standardColor)
                }
                CodeLineText(indent = 2) {
                    Text("tasks.forEach { task ->", color = standardColor)
                }
                CodeLineText(indent = 3) {
                    Text("Text(task)", color = functionColor)
                }
                CodeLineText(indent = 2) {
                    Text("}", color = standardColor)
                }
                CodeLineText(indent = 1) {
                    Text("}", color = standardColor)
                }
                CodeLineText {
                    Text("}", color = standardColor)
                }
            }
        }
    }
}


// ==========================================
// SCREEN 3: Gemini Studio AI Assistant
// ==========================================
@Composable
fun GeminiAISimulatorView(
    chatHistory: List<ChatItem>,
    userChatText: String,
    isAITyping: Boolean,
    onUserChatTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSendPreset: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AI Title bar
        Card(
            colors = CardDefaults.cardColors(containerColor = TechPurple.copy(0.12f)),
            border = BorderStroke(1.dp, TechPurple.copy(0.3f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AccentMint)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Gemini AI in Android Studio Panda 🐼", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GhostWhite)
                    Text(text = "Your intelligent development sidekick (Simulating Live 2026 responses)", fontSize = 11.sp, color = CustomGrey)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Chat conversation history listing inside card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Chats Scrollable window
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    chatHistory.forEach { chat ->
                        val alignLeft = !chat.isUser
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalAlignment = if (alignLeft) Alignment.Start else Alignment.End
                        ) {
                            Text(
                                text = chat.sender,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chat.isUser) NeonCyan else TechPurple,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (alignLeft) 2.dp else 12.dp,
                                            bottomEnd = if (alignLeft) 12.dp else 2.dp
                                        )
                                    )
                                    .background(if (chat.isUser) TechPurple.copy(0.08f) else Color(0xFFF1F5F9))
                                    .border(
                                        1.dp,
                                        if (chat.isUser) TechPurple.copy(0.15f) else Color(0xFFE2E8F0),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = chat.message,
                                        fontSize = 12.sp,
                                        color = GhostWhite,
                                        lineHeight = 16.sp
                                    )
                                    
                                    chat.codeBlock?.let { code ->
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = CodeBg),
                                            border = BorderStroke(1.dp, Color(0xFF1E293B)),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = code,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFFAAFFAA),
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .horizontalScroll(rememberScrollState())
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (isAITyping) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Gemini is writing...", fontSize = 11.sp, color = CustomGrey, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            CircularProgressIndicator(modifier = Modifier.size(10.dp), strokeWidth = 2.dp, color = TechPurple)
                        }
                    }
                }
                
                // Rapid Preset prompts area
                Text(
                    text = "Suggested Questions:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomGrey,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickPromptChip(
                        prompt = "How does remember state work?",
                        onClick = onSendPreset
                    )
                    QuickPromptChip(
                        prompt = "Give Kotlin Coroutine sample",
                        onClick = onSendPreset
                    )
                    QuickPromptChip(
                        prompt = "Explain MVVM architecture in MAD",
                        onClick = onSendPreset
                    )
                }
                
                // Typing input bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userChatText,
                        onValueChange = onUserChatTextChange,
                        placeholder = { Text("Ask Gemini anything about Android...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechPurple,
                            unfocusedBorderColor = Color.White.copy(0.12f)
                        )
                    )
                    IconButton(
                        onClick = onSendMessage,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = TechPurple),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickPromptChip(prompt: String, onClick: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable { onClick(prompt) }
    ) {
        Text(
            text = prompt,
            fontSize = 11.sp,
            color = TechPurple,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

fun simulateGeminiResponse(prompt: String): Pair<String, String?> {
    val lowPrompt = prompt.lowercase()
    return when {
        lowPrompt.contains("remember") || lowPrompt.contains("state") -> {
            Pair(
                "In Jetpack Compose, UI elements are completely immutable. To hold and track a dynamic value that lives across render updates, you wrap it in remember { mutableStateOf(value) }. When Compose detects the state reference updates, it triggers a 'Recomposition' which automatically refreshes the dependent layout nodes elegantly.",
                "var clicks by remember { mutableStateOf(0) }\n\nButton(onClick = { clicks++ }) {\n    Text(\"Clicks: \$clicks\")\n}"
            )
        }
        lowPrompt.contains("coroutine") || lowPrompt.contains("thread") -> {
            Pair(
                "Kotlin Coroutines allow you to run asynchronous tasks on background worker threads (like Dispatchers.IO) without blocking the primary main UI thread. In Compose, you launch workflows in response to events or use LaunchedEffect to safe-wrap background schedules alongside lifecycles.",
                "// Launch async work safely\ncoroutineScope.launch {\n    val result = fetchFromNetworkAPI()\n    withContext(Dispatchers.Main) {\n        uiState = result\n    }\n}"
            )
        }
        lowPrompt.contains("mvvm") || lowPrompt.contains("viewmodel") -> {
            Pair(
                "Model-View-ViewModel (MVVM) is the gold standard of 2026 Android development. The ViewModel acts as the state manager. It exposes simple read-only Compose State flows or Livedata wrappers which the layout @Composables observe natively. Events are channeled from UI into ViewModel functions, preserving loose coupling.",
                "class WeatherViewModel : ViewModel() {\n    private val _temp = mutableStateOf(\"Calculating...\")\n    val temp: State<String> = _temp\n\n    fun fetchWeather() { /* update _temp... */ }\n}"
            )
        }
        else -> {
            Pair(
                "The 2026 Modern Android Development pipeline focuses purely on clean architecture. Android Studio Panda leverages the K2 compiler with unified Kotlin 2.2 syntax, meaning XML pipelines are obsolete. Always modularize your folders into data, domain, and UI directories, use dependency injection with Hilt, and design with strict adaptive grids for folding phones and big screens.",
                "// Standard clean composable layout structure\n@Composable\nfun AppWorkspace() {\n    Surface(modifier = Modifier.fillMaxSize()) {\n        Column { /* Views */ }\n    }\n}"
            )
        }
    }
}


// ==========================================
// SCREEN 4: Deploy & Emulator adaptive
// ==========================================
@Composable
fun VerifyDeployView(
    deviceEmulationMode: String,
    onDeviceModeChange: (String) -> Unit,
    isPublishingActive: Boolean,
    publishProgress: Float,
    publishLogs: List<String>,
    isPublishComplete: Boolean,
    onTriggerPublish: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Step 5: Testing & Live Deployment 🌏",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GhostWhite
        )
        Text(
            text = "Verify your layouts on diverse screen dimensions, and simulate pushing your bundle onto the Google Play Console tracks!",
            fontSize = 12.sp,
            color = CustomGrey,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Emulation layout preview
        Text(
            text = "Adaptive Material 3 Scaling Simulator",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GhostWhite,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { onDeviceModeChange("Phone") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (deviceEmulationMode == "Phone") TechPurple else CardSlate,
                    contentColor = if (deviceEmulationMode == "Phone") Color.White else CustomGrey
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Phone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onDeviceModeChange("Foldable") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (deviceEmulationMode == "Foldable") TechPurple else CardSlate,
                    contentColor = if (deviceEmulationMode == "Foldable") Color.White else CustomGrey
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Foldable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onDeviceModeChange("Tablet") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (deviceEmulationMode == "Tablet") TechPurple else CardSlate,
                    contentColor = if (deviceEmulationMode == "Tablet") Color.White else CustomGrey
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Tablet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        // Emulated Device Container Preview Box
        Card(
            colors = CardDefaults.cardColors(containerColor = CodeBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Responsive Content Adaptation Preview",
                    fontSize = 11.sp,
                    color = CustomGrey,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Simulated device frame that grows/shrinks depending on selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            when (deviceEmulationMode) {
                                "Phone" -> 0.7f
                                "Foldable" -> 0.88f
                                "Tablet" -> 1.0f
                                else -> 0.8f
                            }
                        )
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardSlate)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (deviceEmulationMode == "Phone") {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Standard Phone Layout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Dynamic vertical linear layout", fontSize = 9.sp, color = CustomGrey)
                        }
                    } else if (deviceEmulationMode == "Foldable") {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).background(DeepMidnight).padding(4.dp)) {
                                Text("Left: List", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                            Box(modifier = Modifier.weight(1f).background(DeepMidnight).padding(4.dp)) {
                                Text("Right: Detail", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TechPurple)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(24.dp).background(AccentMint.copy(0.12f), RoundedCornerShape(4.dp)))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Adaptive Large Wide Screen View", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Multi-column layout maximizes screen space", fontSize = 8.sp, color = CustomGrey)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pushing to Play Store section
        Text(
            text = "Automated Bundle Deployment Engine",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GhostWhite,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Launch Play Console deployment process, optimize dependencies via R8, sign keys, and push live testing tracks instantly.",
                    fontSize = 12.sp,
                    color = GhostWhite.copy(0.8f),
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (!isPublishingActive && !isPublishComplete) {
                    Button(
                        onClick = onTriggerPublish,
                        colors = ButtonDefaults.buttonColors(containerColor = TechPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🚀 Compile & Push for Testing", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (isPublishingActive) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Compiling & Bundling APK...", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            Text("${(publishProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { publishProgress },
                            color = NeonCyan,
                            trackColor = Color.White.copy(0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentMint.copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = AccentMint, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("AAB Deployment Complete!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentMint)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onTriggerPublish,
                            colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, TechPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔁 Re-deploy Build", color = GhostWhite, fontSize = 11.sp)
                        }
                    }
                }
                
                // Output Logs console terminal
                if (publishLogs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Build Pipeline Logs Terminal:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechPurple
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CodeBg),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            publishLogs.forEach { log ->
                                Text(
                                    text = log,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (log.contains("Successful") || log.contains("Complete")) AccentMint else Color(0xFFD6DBE9),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}
