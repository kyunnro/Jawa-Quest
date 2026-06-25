package com.example.aksarajawa

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * DUOLINGO 3D BUTTON
 */
@Composable
fun DuolingoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = JavaQuestPrimary,
    shadowColor: Color = backgroundColor.copy(alpha = 0.8f).darken(0.2f),
    enabled: Boolean = true,
    height: Dp = 56.dp,
    textColor: Color = Color.White
) {
    var isPressed by remember { mutableStateOf(false) }
    val shadowHeight = 6.dp
    
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) shadowHeight else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "PressedOffset"
    )

    val currentBgColor = if (enabled) backgroundColor else Color(0xFFE2E0EE)
    val currentShadowColor = if (enabled) shadowColor else Color(0xFFC7C5D4)

    Box(
        modifier = modifier
            .height(height + shadowHeight)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        }
                    )
                }
            }
            .graphicsLayer { translationY = animatedOffset.toPx() }
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(height).offset(y = shadowHeight).clip(RoundedCornerShape(14.dp)).background(currentShadowColor))
        Box(
            modifier = Modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(14.dp)).background(currentBgColor).border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = if (enabled) textColor else Color(0xFF9896A4), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
        }
    }
}

fun Color.darken(factor: Float = 0.2f): Color = Color(red = (red * (1f - factor)).coerceIn(0f, 1f), green = (green * (1f - factor)).coerceIn(0f, 1f), blue = (blue * (1f - factor)).coerceIn(0f, 1f), alpha = alpha)

/**
 * PREMIUM 3D CARD
 */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    containerColor: Color = Color.White,
    borderColor: Color = Color(0xFFECEAF3),
    shadowColor: Color = borderColor.darken(0.05f),
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val shadowHeight = 4.dp
    
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) shadowHeight else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "CardPress"
    )

    Box(
        modifier = modifier
            .padding(bottom = shadowHeight)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
            .graphicsLayer { translationY = animatedOffset.toPx() }
    ) {
        Box(modifier = Modifier.matchParentSize().offset(y = shadowHeight).clip(RoundedCornerShape(18.dp)).background(shadowColor))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = containerColor,
            border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

/**
 * STAT CARD COMPONENT
 */
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    PremiumCard(
        modifier = modifier,
        onClick = {}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = JavaQuestTextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = JavaQuestTextSecondary
            )
        }
    }
}

/**
 * PREMIUM OPTION CARD
 */
@Composable
fun PremiumOptionCard(
    text: String,
    isSelected: Boolean,
    isAnswerChecked: Boolean,
    borderColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shadowHeight = 4.dp
    val animatedOffset by animateDpAsState(targetValue = if (isSelected) shadowHeight else 0.dp, label = "OptionPress")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp + shadowHeight)
            .clickable(enabled = !isAnswerChecked) { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize().offset(y = shadowHeight).clip(RoundedCornerShape(16.dp)).background(borderColor.copy(alpha = 0.2f)))
        Surface(
            modifier = Modifier.fillMaxWidth().height(64.dp).graphicsLayer { translationY = animatedOffset.toPx() },
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = if (isAnswerChecked && isSelected) borderColor else JavaQuestTextPrimary, textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * AKSARA TILE
 */
@Composable
fun AksaraTile(
    char: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isVisible: Boolean = true
) {
    if (isVisible) {
        val shadowHeight = 4.dp
        Box(
            modifier = modifier
                .padding(4.dp)
                .width(60.dp)
                .height(70.dp + shadowHeight)
                .clickable { onClick() }
        ) {
            Box(modifier = Modifier.fillMaxSize().offset(y = shadowHeight).clip(RoundedCornerShape(12.dp)).background(if (isSelected) Color(0xFFD1D1D1) else Color(0xFFE2E0EE)))
            Box(modifier = Modifier.fillMaxWidth().height(70.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) Color(0xFFF1F0F7) else Color.White).border(1.5.dp, if (isSelected) Color.LightGray else Color(0xFFECEAF3), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Text(text = char, fontSize = 32.sp, color = JavaQuestPrimary, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Spacer(modifier = modifier.padding(4.dp).size(width = 60.dp, height = 74.dp))
    }
}

/**
 * FEEDBACK SHEET
 */
@Composable
fun FeedbackSheet(isCorrect: Boolean, correctAnswer: String, onContinue: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFDFE0),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null, tint = if (isCorrect) JavaQuestSuccess else JavaQuestError, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = if (isCorrect) "Leres Sanget!" else "Kirang Tepat,", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = if (isCorrect) JavaQuestSuccess else JavaQuestError))
            }
            if (!isCorrect) {
                Text(text = "Jawaban sing bener: $correctAnswer", style = MaterialTheme.typography.bodyMedium, color = JavaQuestError, modifier = Modifier.padding(top = 8.dp, start = 44.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            DuolingoButton(text = "LANJUT", onClick = onContinue, backgroundColor = if (isCorrect) JavaQuestSuccess else JavaQuestError)
        }
    }
}

/**
 * STATS COMPONENTS
 */
@Composable
fun DuolingoProgressBar(progress: Float, modifier: Modifier = Modifier, height: Dp = 16.dp, trackColor: Color = Color(0xFFECEAF3), progressColor: Color = JavaQuestSuccess) {
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow), label = "Progress")
    Box(modifier = modifier.fillMaxWidth().height(height).clip(CircleShape).background(trackColor)) {
        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).clip(CircleShape).background(Brush.horizontalGradient(colors = listOf(progressColor, progressColor.darken(-0.1f))))) {
            Box(modifier = Modifier.fillMaxWidth().height(height / 3).padding(horizontal = 8.dp, vertical = 1.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
        }
    }
}

@Composable
fun StreakBadge(streak: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "Streak")
    val scale by infiniteTransition.animateFloat(initialValue = 1.0f, targetValue = if (streak > 0) 1.15f else 1.0f, animationSpec = infiniteRepeatable(animation = tween(1000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "Scale")
    StatHeaderItem(icon = { Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = if (streak > 0) JavaQuestAccent else Color.Gray, modifier = Modifier.size(20.dp).graphicsLayer(scaleX = scale, scaleY = scale)) }, text = "$streak")
}

@Composable
fun LevelBadge(levelName: String) {
    StatHeaderItem(icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Level", tint = JavaQuestSecondary, modifier = Modifier.size(20.dp)) }, text = levelName)
}

@Composable
fun HeartsCount(hearts: Int) {
    StatHeaderItem(icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Nyawa", tint = JavaQuestError, modifier = Modifier.size(20.dp)) }, text = "$hearts")
}

@Composable
fun StatHeaderItem(icon: @Composable () -> Unit, text: String, modifier: Modifier = Modifier, containerColor: Color = Color.White) {
    Surface(modifier = modifier.shadow(2.dp, CircleShape).clip(CircleShape), color = containerColor) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            icon(); Text(text = text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, color = JavaQuestTextPrimary))
        }
    }
}

/**
 * JAVANESE BATIK HEADER PATTERN
 */
@Composable
fun JavaneseHeaderPattern(modifier: Modifier = Modifier, color: Color = Color.White.copy(alpha = 0.08f)) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.7f)
            cubicTo(width * 0.25f, height * 0.9f, width * 0.5f, height * 0.4f, width * 0.75f, height * 0.8f)
            lineTo(width, height * 0.6f); lineTo(width, 0f); lineTo(0f, 0f); close()
        }
        drawPath(path, color = color)
        val linePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.85f); quadraticTo(width * 0.5f, height * 0.5f, width, height * 0.75f)
        }
        drawPath(linePath, color = color.copy(alpha = color.alpha * 1.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))
    }
}

/**
 * SHARED LEADERBOARD COMPONENTS
 */
@Composable
fun PremiumLeaderboardItem(
    modifier: Modifier = Modifier,
    rank: Int,
    profile: Profile,
    isCurrentUser: Boolean
) {
    PremiumCard(
        modifier = modifier,
        containerColor = if (isCurrentUser) JavaQuestPrimary.copy(alpha = 0.08f) else Color.White,
        borderColor = if (isCurrentUser) JavaQuestPrimary.copy(alpha = 0.4f) else Color(0xFFECEAF3)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$rank", fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (isCurrentUser) JavaQuestPrimary else JavaQuestTextSecondary, modifier = Modifier.width(36.dp))
            AsyncImage(
                model = if (!profile.avatarUrl.isNullOrEmpty()) profile.avatarUrl else "https://api.dicebear.com/7.x/avataaars/svg?seed=${profile.username}",
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp, if (isCurrentUser) JavaQuestPrimary else Color.Transparent, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = profile.username, fontSize = 16.sp, fontWeight = if (isCurrentUser) FontWeight.Black else FontWeight.Bold, color = if (isCurrentUser) JavaQuestPrimary else JavaQuestTextPrimary)
                Text(text = if (rank <= 3) "Jawara Aksara" else "Pelajar Setia", fontSize = 12.sp, color = JavaQuestTextSecondary)
            }
            Text(text = "${profile.totalScore} XP", fontSize = 16.sp, fontWeight = FontWeight.Black, color = JavaQuestPrimary)
        }
    }
}

@Composable
fun EmptyLeaderboard(onRefresh: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.EmojiEvents, null, tint = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Belum ada Jawara", color = JavaQuestTextSecondary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onRefresh) {
                Text("Muat Ulang", color = JavaQuestPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
