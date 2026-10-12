package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.WebProject
import com.example.ui.theme.BorderCyanGlow
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MaxyViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebBuilderScreen(
    viewModel: MaxyViewModel,
    initialProjectId: Long? = null,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.webProjects.collectAsState()
    val activeProject = remember(projects, initialProjectId) {
        if (initialProjectId != null) {
            projects.firstOrNull { it.id == initialProjectId } ?: projects.firstOrNull()
        } else {
            projects.firstOrNull()
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Preview, 1: HTML, 2: CSS, 3: JS, 4: Hosting
    var isMobileFrame by remember { mutableStateOf(true) }
    var userPromptInput by remember { mutableStateOf("") }
    var refreshKey by remember { mutableIntStateOf(0) }

    // Editable code buffers
    var editedHtml by remember(activeProject) { mutableStateOf(activeProject?.htmlContent ?: "") }
    var editedCss by remember(activeProject) { mutableStateOf(activeProject?.cssContent ?: "") }
    var editedJs by remember(activeProject) { mutableStateOf(activeProject?.jsContent ?: "") }

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
                    text = "AI WEBSITE BUILDER",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = activeProject?.title ?: "Generate any website from voice",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (activeProject != null) {
                Button(
                    onClick = { viewModel.exportWebProject(activeProject) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF050811)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("export_zip_button")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("ZIP Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Prompt input field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userPromptInput,
                onValueChange = { userPromptInput = it },
                placeholder = { Text("e.g. Mere liye ek gaming website banao...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("web_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
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
                    if (userPromptInput.isNotBlank()) {
                        viewModel.handleUserInput(userPromptInput)
                        userPromptInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Generate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tab Navigation: Preview, HTML, CSS, JS, Hosting
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF091224),
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("PREVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("HTML", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("CSS", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("JS", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("HOSTING", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(Modifier.height(12.dp))

        // Content Body
        when (selectedTab) {
            0 -> {
                // Live WebView Preview with Desktop / Mobile Frame Toggle
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Viewport: ", fontSize = 12.sp, color = TextSecondary)
                            IconButton(onClick = { isMobileFrame = true }) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = "Mobile View", tint = if (isMobileFrame) NeonCyan else TextMuted)
                            }
                            IconButton(onClick = { isMobileFrame = false }) {
                                Icon(Icons.Default.Tv, contentDescription = "Desktop View", tint = if (!isMobileFrame) NeonCyan else TextMuted)
                            }
                        }

                        IconButton(onClick = { refreshKey++ }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload Preview", tint = NeonCyan)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .then(if (isMobileFrame) Modifier.widthIn(max = 380.dp) else Modifier.fillMaxWidth())
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, BorderCyanGlow, RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            if (activeProject != null) {
                                val previewHtml = remember(activeProject, editedHtml, editedCss, editedJs, refreshKey) {
                                    val currentProj = activeProject.copy(
                                        htmlContent = editedHtml,
                                        cssContent = editedCss,
                                        jsContent = editedJs
                                    )
                                    viewModel.webGenerator.buildPreviewHtml(currentProj)
                                }

                                AndroidView(
                                    factory = { ctx ->
                                        WebView(ctx).apply {
                                            settings.javaScriptEnabled = true
                                            settings.domStorageEnabled = true
                                            settings.loadWithOverviewMode = true
                                            settings.useWideViewPort = true
                                            webViewClient = WebViewClient()
                                            loadDataWithBaseURL(null, previewHtml, "text/html", "UTF-8", null)
                                        }
                                    },
                                    update = { view ->
                                        view.loadDataWithBaseURL(null, previewHtml, "text/html", "UTF-8", null)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No website loaded yet.", color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            1, 2, 3 -> {
                // Code Inspector & Editor Tab
                val (currentCode, onCodeChange, label) = when (selectedTab) {
                    1 -> Triple(editedHtml, { text: String -> editedHtml = text }, "index.html")
                    2 -> Triple(editedCss, { text: String -> editedCss = text }, "style.css")
                    else -> Triple(editedJs, { text: String -> editedJs = text }, "script.js")
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "File: $label", fontWeight = FontWeight.Bold, color = NeonGreen, fontSize = 12.sp)

                        Button(
                            onClick = {
                                if (activeProject != null) {
                                    val updated = activeProject.copy(
                                        htmlContent = editedHtml,
                                        cssContent = editedCss,
                                        jsContent = editedJs
                                    )
                                    viewModel.updateWebProject(updated)
                                    selectedTab = 0 // Return to preview
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Save & Apply to Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = currentCode,
                        onValueChange = onCodeChange,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E3558),
                            focusedContainerColor = Color(0xFF050914),
                            unfocusedContainerColor = Color(0xFF050914),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            4 -> {
                // Deployment & Hosting Guide
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
                            Text("HOW TO DEPLOY YOUR WEBSITE FOR FREE", fontWeight = FontWeight.Bold, color = NeonCyan)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "1. Click 'ZIP Export' above to export your website files.\n" +
                                "2. Extract the ZIP package (contains index.html, style.css, and script.js).\n" +
                                "3. Upload to free global hosting:\n" +
                                "   • GitHub Pages: Push to repo & enable Pages\n" +
                                "   • Netlify Drop: Drag and drop the extracted folder to app.netlify.com/drop\n" +
                                "   • Vercel / Firebase Hosting: Run `firebase deploy`\n" +
                                "4. Your website is instantly live on a custom HTTPS domain!",
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
