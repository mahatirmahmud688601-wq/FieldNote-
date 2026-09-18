package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FieldnoteTheme

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val detail: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            title = "Calm Geometric Productivity",
            subtitle = "Built for Students & Knowledge Workers",
            detail = "Fieldnote unites your tasks, rich notes, calendar timeline, and daily focus into a single Scandinavian-Apple aesthetic workspace.",
            icon = Icons.Default.TaskAlt,
            badge = "MINIMALISM"
        ),
        OnboardingStep(
            title = "Gemini AI Copilot & Voice",
            subtitle = "Natural Language Workflow Planning",
            detail = "Speak or type natural schedules like 'Finish Physics assignment next Friday at 4 PM'. Copilot analyzes conflicts, drafts plans, and guides your deep work blocks.",
            icon = Icons.Default.AutoAwesome,
            badge = "INTELLIGENCE"
        ),
        OnboardingStep(
            title = "Zero-Knowledge Hardware Vault",
            subtitle = "AES-256-GCM Hardware Encrypted",
            detail = "Your sensitive thoughts, exam notes, and personal strategies are protected locally. Data never leaves your device without explicit consent.",
            icon = Icons.Default.Lock,
            badge = "SECURITY"
        )
    )

    val step = steps[currentStep]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.accent.copy(alpha = 0.15f))
                    .border(1.dp, colors.accent.copy(alpha = 0.4f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = step.badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = colors.accent
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Large Hero Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.5.dp, colors.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = step.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.accent,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = step.detail,
                fontSize = 13.5.sp,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Step Indicator Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.indices.forEach { index ->
                    val isActive = index == currentStep
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (isActive) colors.accent else colors.surface)
                            .border(0.5.dp, colors.border, CircleShape)
                    )
                }
            }
        }

        // Bottom Navigation Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onComplete,
                modifier = Modifier.testTag("skip_onboarding")
            ) {
                Text("Skip", color = colors.textMuted)
            }

            Button(
                onClick = {
                    if (currentStep < steps.size - 1) {
                        currentStep += 1
                    } else {
                        onComplete()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                shape = CircleShape,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (currentStep == steps.size - 1) "Enter Fieldnote" else "Continue",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
