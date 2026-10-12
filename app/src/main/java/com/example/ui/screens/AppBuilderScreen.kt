package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderCyanGlow
import com.example.ui.theme.BorderGreenGlow
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MaxyViewModel

@Composable
fun AppBuilderScreen(
    viewModel: MaxyViewModel,
    initialProjectId: Long? = null,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.appProjects.collectAsState()
    val activeProject = remember(projects, initialProjectId) {
        if (initialProjectId != null) {
            projects.firstOrNull { it.id == initialProjectId } ?: projects.firstOrNull()
        } else {
            projects.firstOrNull()
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Files & Code, 1: Validation, 2: Build Guide
    var promptInput by remember { mutableStateOf("") }

    val filesMap = remember(activeProject) {
        if (activeProject != null) {
            viewModel.appGenerator.parseFilesFromJson(activeProject.filesJson)
        } else emptyMap()
    }

    var selectedFile by remember(filesMap) { mutableStateOf(filesMap.keys.firstOrNull() ?: "MainActivity.kt") }
    var explanationText by remember { mutableStateOf<String?>(null) }
    var validationResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI ANDROID APP BUILDER",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                )
                Text(
                    text = activeProject?.title ?: "Kotlin & Jetpack Compose Project Generator",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (activeProject != null) {
                Button(
                    onClick = { viewModel.exportAppProject(activeProject) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("export_android_zip_button")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("ZIP Package", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Prompt Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = { Text("e.g. Ek tournament app banao jisme login aur list ho...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("app_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color(0xFF1E3558),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        viewModel.handleUserInput(promptInput)
                        promptInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Build App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF091224),
            contentColor = NeonGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonGreen
                )
            }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("CODE EXPLORER", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("VALIDATION & AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("BUILD & APK GUIDE", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                // Code Explorer & File Tree
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Column: File Tree list
                    Column(
                        modifier = Modifier
                            .width(130.dp)
                            .background(Color(0xFF070D1B), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF162544), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("PROJECT FILES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                        Spacer(Modifier.height(6.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(filesMap.keys.toList()) { fileKey ->
                                val isSelected = fileKey == selectedFile
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable {
                                            selectedFile = fileKey
                                            explanationText = null
                                            validationResult = null
                                        }
                                        .padding(vertical = 4.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (fileKey.endsWith(".kt")) Icons.Default.Description else Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = if (isSelected) NeonGreen else TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = fileKey.substringAfterLast("/"),
                                        fontSize = 10.sp,
                                        color = if (isSelected) NeonGreen else TextPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    // Right Column: Code Viewer
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF040711), RoundedCornerShape(8.dp))
                            .border(1.dp, BorderGreenGlow, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedFile, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)

                            Button(
                                onClick = {
                                    val code = filesMap[selectedFile] ?: ""
                                    explanationText = viewModel.appGenerator.explainCodeInHindi(selectedFile, code)
                                    viewModel.textToSpeech.speak(explanationText ?: "", "hi-IN")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF12223F)),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonCyan)
                                Spacer(Modifier.width(4.dp))
                                Text("Explain in Hindi", fontSize = 10.sp, color = NeonCyan)
                            }
                        }

                        if (explanationText != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(Color(0xFF0D1C36), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(explanationText ?: "", fontSize = 11.sp, color = NeonAmber)
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        val codeContent = filesMap[selectedFile] ?: "// No file selected"
                        val lines = codeContent.lines()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color(0xFF020409), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Row {
                                // Line Numbers
                                Column(modifier = Modifier.padding(end = 12.dp)) {
                                    lines.forEachIndexed { index, _ ->
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextMuted
                                        )
                                    }
                                }
                                // Code Text
                                Column {
                                    lines.forEach { line ->
                                        Text(
                                            text = line,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Syntax Validation & AI Repair
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGreenGlow)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("KOTLIN SYNTAX & COMPLIANCE VALIDATOR", fontWeight = FontWeight.Bold, color = NeonGreen)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Checks unbalanced braces, import statements, Android Manifest declaration, and Gradle compatibility.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val currentCode = filesMap[selectedFile] ?: ""
                                    validationResult = viewModel.appGenerator.validateKotlinCode(currentCode)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811))
                            ) {
                                Text("Validate $selectedFile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            validationResult?.let { (valid, msg) ->
                                Spacer(Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (valid) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (valid) NeonGreen else NeonAmber
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = if (valid) "PASSED: $msg" else "SYNTAX NOTICE: $msg",
                                        fontWeight = FontWeight.Bold,
                                        color = if (valid) NeonGreen else NeonAmber,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Build & APK Generation Guide
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCyanGlow)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("HOW TO COMPILE THIS PROJECT INTO AN APK", fontWeight = FontWeight.Bold, color = NeonCyan)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Android apps cannot be compiled directly on ordinary mobile runtimes without an isolated development computer or cloud build server. Here is the verified workflow:\n\n" +
                                "1. Click 'ZIP Package' at the top to download the full Android Studio project.\n" +
                                "2. Extract the ZIP on your computer with Android Studio installed.\n" +
                                "3. Open the folder in Android Studio; it will configure the Gradle wrapper automatically.\n" +
                                "4. In the menu, go to: Build > Build Bundle(s) / APK(s) > Build APK(s).\n" +
                                "5. The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
