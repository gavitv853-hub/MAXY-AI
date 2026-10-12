package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BorderCrimsonGlow
import com.example.ui.theme.CyberBackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SimulatedCallDialog(
    contactName: String,
    phoneNumber: String,
    onSpeak: (String) -> Unit,
    onHangUp: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var currentAiLine by remember {
        mutableStateOf("नमस्ते! मैं MAXY AI Assistant हूँ, आपके मित्र की ओर से संपर्क कर रहा हूँ। क्या आप 1 मिनट बात कर सकते हैं?")
    }

    LaunchedEffect(Unit) {
        onSpeak(currentAiLine)
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    Dialog(onDismissRequest = onHangUp) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderCrimsonGlow, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberBackgroundDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Security & Identity Disclosure Badge
                Row(
                    modifier = Modifier
                        .background(Color(0xFF132238), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = "AI CALL TELEPHONY • VERIFIED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Orb in Speaking state
                HolographicOrb(
                    state = OrbState.SPEAKING,
                    size = 110.dp
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = contactName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                val mins = (callSeconds / 60).toString().padStart(2, '0')
                val secs = (callSeconds % 60).toString().padStart(2, '0')
                Text(
                    text = "$mins:$secs",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Mandatory Telephony Policy Consent Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C162A), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF1E3558), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(6.dp))
                            Text("AI Identity & Privacy Consent:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                        }
                        Text(
                            text = "\"MAXY AI explicitly identifies itself. Call audio is not recorded and user consent is required.\"",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Live Spoken Script
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF101B33), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = currentAiLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Interactive simulated recipient responses
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            currentAiLine = "धन्यवाद! संदेश यह है कि सर 10 मिनट में मीटिंग में शामिल हो रहे हैं। अच्छा दिन रहे!"
                            onSpeak(currentAiLine)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162A4E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("हाँ, बताइए", fontSize = 11.sp, color = NeonCyan)
                    }

                    Button(
                        onClick = {
                            currentAiLine = "कोई बात नहीं सर, मैं बाद में पुनः संपर्क करूँगा। अलविदा।"
                            onSpeak(currentAiLine)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162A4E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("अभी व्यस्त हूँ", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Hang Up Button
                Button(
                    onClick = onHangUp,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson, contentColor = Color.White),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Hang Up",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
