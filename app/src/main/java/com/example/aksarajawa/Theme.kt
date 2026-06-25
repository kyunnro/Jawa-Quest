package com.example.aksarajawa

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette
val JavaQuestPrimary = Color(0xFF7C3AED)
val JavaQuestSecondary = Color(0xFFA855F7)
val JavaQuestAccent = Color(0xFFF59E0B)
val JavaQuestSuccess = Color(0xFF22C55E)
val JavaQuestError = Color(0xFFEF4444)
val JavaQuestBackground = Color(0xFFF8F7FC)
val JavaQuestCard = Color(0xFFFFFFFF)
val JavaQuestTextPrimary = Color(0xFF1E1B4B)
val JavaQuestTextSecondary = Color(0xFF6B7280)

private val LightColorScheme = lightColorScheme(
    primary = JavaQuestPrimary,
    onPrimary = Color.White,
    primaryContainer = JavaQuestPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = JavaQuestPrimary,
    
    secondary = JavaQuestSecondary,
    onSecondary = Color.White,
    secondaryContainer = JavaQuestSecondary.copy(alpha = 0.12f),
    onSecondaryContainer = JavaQuestSecondary,
    
    tertiary = JavaQuestAccent,
    onTertiary = Color.White,
    tertiaryContainer = JavaQuestAccent.copy(alpha = 0.12f),
    onTertiaryContainer = JavaQuestAccent,
    
    background = JavaQuestBackground,
    onBackground = JavaQuestTextPrimary,
    
    surface = JavaQuestCard,
    onSurface = JavaQuestTextPrimary,
    surfaceVariant = Color(0xFFF1F0F7),
    onSurfaceVariant = JavaQuestTextSecondary,
    
    outline = Color(0xFFE2E0EE),
    outlineVariant = Color(0xFFECEAF3),
    
    error = JavaQuestError,
    onError = Color.White
)

val JavaQuestShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp), // For Buttons (14dp button radius)
    large = RoundedCornerShape(16.dp)   // For Cards (16dp corner radius)
)

// Customized M3 Typography
val JavaQuestTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun JavaQuestTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        shapes = JavaQuestShapes,
        typography = JavaQuestTypography,
        content = content
    )
}
