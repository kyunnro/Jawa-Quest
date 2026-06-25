package com.example.aksarajawa

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Logo Bounce & Fade-in Animation in parallel
        scope.launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        scope.launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000)
            )
        }
        
        delay(2500) // Aesthetic delay

        // Navigation check
        val currentUser = FirebaseAuth.getInstance().currentUser
        val destination = if (currentUser != null) Screen.Home.route else Screen.Login.route
        
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(JavaQuestPrimary, JavaQuestSecondary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background Javanese pattern
        JavaneseHeaderPattern(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { this.alpha = alpha.value }
        ) {
            // Animated Logo Image - Fix: Use logop_foreground to avoid Adaptive Icon crash
            Image(
                painter = painterResource(id = R.mipmap.logop_foreground),
                contentDescription = "Logo JavaQuest",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, JavaQuestAccent.copy(alpha = 0.5f), CircleShape)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "JavaQuest",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )
            
            Text(
                text = "Petualangan Belajar Aksara",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            )
            
            Spacer(modifier = Modifier.height(56.dp))
            
            // Subtle loading indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(150.dp)
                    .height(6.dp)
                    .clip(CircleShape),
                color = JavaQuestAccent,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
        
        // Version info at bottom
        Text(
            text = "Versi 1.1",
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
