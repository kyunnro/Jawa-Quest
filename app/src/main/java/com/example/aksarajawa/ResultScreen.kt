package com.example.aksarajawa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

private data class Confetti(
    var x: Float,
    var y: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    var rotation: Float
)

@Composable
fun ResultScreen(navController: NavController, viewModel: GameViewModel, difficulty: String) {
    val score by viewModel.gameScore.collectAsState()
    val correct by viewModel.correctCount.collectAsState()
    val wrong by viewModel.wrongCount.collectAsState()
    val isScoreSaved by viewModel.isScoreSaved.collectAsState()

    // Auto-simpan skor saat halaman hasil muncul
    LaunchedEffect(Unit) {
        viewModel.saveScore(difficulty)
    }

    // 1. Score Count Up Animation
    val animatedScore = remember { Animatable(0f) }
    LaunchedEffect(score) {
        animatedScore.animateTo(score.toFloat(), tween(1500, easing = EaseOutQuad))
    }

    // 2. Trophy Bounce Animation
    val trophyScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(300)
        trophyScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    // 3. Confetti Simulation
    val confettiColors = listOf(Color(0xFFF59E0B), Color(0xFF7C3AED), Color(0xFF22C55E), Color(0xFFEF4444), Color(0xFF3B82F6))
    val confettiList = remember { List(100) { 
        Confetti(
            x = (0..100).random() / 100f,
            y = - (10..200).random() / 100f,
            size = (15..30).random().toFloat(),
            color = confettiColors.random(),
            speed = (0.004f + (1..12).random() / 1000f),
            rotation = (0..360).random().toFloat()
        )
    }}

    // Animation Loop for Confetti
    val infiniteTransition = rememberInfiniteTransition(label = "ConfettiLoop")
    val frameTrigger by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16, easing = LinearEasing))
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Confetti Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val unused = frameTrigger // Force redraw
            confettiList.forEach { c ->
                c.y += c.speed
                c.rotation += 2f
                if (c.y > 1.1f) { c.y = -0.1f; c.x = (0..100).random() / 100f }
                
                rotate(c.rotation, androidx.compose.ui.geometry.Offset(c.x * size.width, c.y * size.height)) {
                    drawRect(c.color, androidx.compose.ui.geometry.Offset(c.x * size.width, c.y * size.height), androidx.compose.ui.geometry.Size(c.size, c.size * 1.6f))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 80.dp), // Padding bawah ditingkatkan dari 40 ke 80
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hore! Sesi Rampung", color = JavaQuestTextSecondary, fontWeight = FontWeight.Bold)
                Text("Mode $difficulty", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = JavaQuestPrimary)
            }

            // Animated Trophy
            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = trophyScale.value; scaleY = trophyScale.value }
                    .size(170.dp)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(Color(0xFFFFF9DB), Color(0xFFFEF3C7))))
                    .border(4.dp, JavaQuestAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, null, tint = JavaQuestAccent, modifier = Modifier.size(96.dp))
            }

            // Score Display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+${animatedScore.value.toInt()}",
                    fontSize = 86.sp,
                    fontWeight = FontWeight.Black,
                    color = JavaQuestPrimary,
                    lineHeight = 86.sp
                )
                Text("SKOR PEROLEHAN", style = MaterialTheme.typography.labelLarge, color = JavaQuestTextSecondary, fontWeight = FontWeight.Black)
            }

            // Accuracy Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ResultCard(Modifier.weight(1f), "BENER", "$correct", JavaQuestSuccess)
                ResultCard(Modifier.weight(1f), "LUPUT", "$wrong", JavaQuestError)
            }

            // Bottom Buttons
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Status simpan skor
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(14.dp)),
                    color = if (isScoreSaved) JavaQuestSuccess.copy(alpha = 0.1f) else JavaQuestPrimary.copy(alpha = 0.08f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        if (isScoreSaved) {
                            Icon(Icons.Default.CheckCircle, null, tint = JavaQuestSuccess)
                            Spacer(Modifier.width(8.dp))
                            Text("Skor Wis Kasimpen!", color = JavaQuestSuccess, fontWeight = FontWeight.Bold)
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = JavaQuestPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Nyimpen skor...", color = JavaQuestPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.resetGame(); navController.popBackStack() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, JavaQuestPrimary)
                    ) {
                        Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(8.dp)); Text("Sinau Meneh", fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { viewModel.resetGame(); navController.navigate(Screen.Home.route) { popUpTo(0) } },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JavaQuestSecondary)
                    ) {
                        Icon(Icons.Default.Home, null); Spacer(Modifier.width(8.dp)); Text("Beranda", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(modifier: Modifier, label: String, value: String, color: Color) {
    PremiumCard(onClick = {}, modifier = modifier, borderColor = color.copy(alpha = 0.2f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(label, color = color, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall)
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black, color = JavaQuestTextPrimary)
        }
    }
}
