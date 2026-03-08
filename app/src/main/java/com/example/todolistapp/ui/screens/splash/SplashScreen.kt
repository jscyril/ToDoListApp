package com.example.todolistapp.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.R
import com.example.todolistapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Logo scale animation
    val logoScale = remember { Animatable(0f) }
    // Logo alpha
    val logoAlpha = remember { Animatable(0f) }
    // Text alpha
    val textAlpha = remember { Animatable(0f) }
    // Subtitle alpha
    val subtitleAlpha = remember { Animatable(0f) }
    // Ring pulse
    val ringScale = remember { Animatable(0.6f) }
    val ringAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Phase 1: Logo appears with spring animation
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }
        logoScale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Phase 2: Ring pulse
        launch {
            ringAlpha.animateTo(0.6f, animationSpec = tween(400))
            ringScale.animateTo(1.3f, animationSpec = tween(600, easing = FastOutSlowInEasing))
            ringAlpha.animateTo(0f, animationSpec = tween(400))
        }

        // Phase 3: Title fades in
        delay(200)
        textAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))

        // Phase 4: Subtitle fades in
        delay(100)
        subtitleAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))

        // Wait then navigate
        delay(800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
        contentAlignment = Alignment.Center
    ) {
        // Subtle radial gradient behind logo
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 400f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated ring behind logo
            Box(contentAlignment = Alignment.Center) {
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(ringScale.value)
                        .alpha(ringAlpha.value)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .then(
                            Modifier.background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        ElectricCyan.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                        )
                )

                // App Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "ToDo App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "ToDo",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 36.sp,
                modifier = Modifier.alpha(textAlpha.value),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Organize • Focus • Achieve",
                style = MaterialTheme.typography.bodyMedium,
                color = ElectricCyan.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.alpha(subtitleAlpha.value),
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp
            )
        }

        // Bottom version text
        Text(
            text = "v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(subtitleAlpha.value)
        )
    }
}

