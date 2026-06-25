package com.example.aksarajawa

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, viewModel: GameViewModel, difficulty: String) {
    val questions by viewModel.easyMediumQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val score by viewModel.gameScore.collectAsState()
    val wrongCount by viewModel.wrongCount.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    // Game States
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isCurrentAnswerCorrect by remember { mutableStateOf(false) }
    
    // Lives logic
    val maxHearts = 5
    val currentHearts = (maxHearts - wrongCount).coerceAtLeast(0)

    // Animations
    val shakeOffset = remember { Animatable(0f) }
    val scaleBounce = remember { Animatable(1f) }

    val currentQuestion = questions.getOrNull(currentIndex)

    LaunchedEffect(currentIndex, currentHearts, questions) {
        if (questions.isNotEmpty()) {
            if (currentIndex >= questions.size || currentHearts <= 0) {
                delay(600)
                navController.navigate(Screen.Result.createRoute(difficulty)) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { 
                        viewModel.resetGame()
                        navController.popBackStack() 
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Batal", tint = JavaQuestTextPrimary)
                    }

                    if (questions.isNotEmpty()) {
                        DuolingoProgressBar(
                            progress = (currentIndex.toFloat() / questions.size.toFloat()),
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeartsCount(hearts = currentHearts)
                        StatHeaderItem(
                            icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null, tint = JavaQuestAccent, modifier = Modifier.size(16.dp)) },
                            text = "+$score"
                        )
                    }
                }
            }
        }
    ) { padding ->
        currentQuestion?.let { question ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (difficulty == "Mudah") "Pilihlah pelafalan latin sing bener:" 
                               else "Pilihlah ejaan sandhangan sing pas:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = JavaQuestTextSecondary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question Card
                    PremiumCard(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .graphicsLayer {
                                translationX = shakeOffset.value
                                scaleX = scaleBounce.value
                                scaleY = scaleBounce.value
                            },
                        borderColor = JavaQuestPrimary.copy(alpha = 0.1f)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "ꦗꦮꦢꦶ",
                                fontSize = 120.sp,
                                color = JavaQuestPrimary.copy(alpha = 0.03f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Text(
                                text = question.question,
                                fontSize = 100.sp,
                                color = JavaQuestPrimary,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Options list - logic handled directly in onClick for auto-advance
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp), 
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        question.options.forEach { option ->
                            val isSelected = selectedOption == option
                            
                            val borderColor = when {
                                isAnswerChecked && option == question.correctAnswer -> JavaQuestSuccess
                                isAnswerChecked && isSelected && !isCurrentAnswerCorrect -> JavaQuestError
                                isSelected -> JavaQuestPrimary
                                else -> Color(0xFFECEAF3)
                            }
                            
                            val bgColor = when {
                                isAnswerChecked && option == question.correctAnswer -> JavaQuestSuccess.copy(alpha = 0.1f)
                                isAnswerChecked && isSelected && !isCurrentAnswerCorrect -> JavaQuestError.copy(alpha = 0.1f)
                                isSelected -> JavaQuestPrimary.copy(alpha = 0.08f)
                                else -> Color.White
                            }

                            PremiumOptionCard(
                                text = option,
                                isSelected = isSelected,
                                isAnswerChecked = isAnswerChecked,
                                borderColor = borderColor,
                                containerColor = bgColor,
                                onClick = { 
                                    if (!isAnswerChecked) {
                                        selectedOption = option
                                        val isCorrect = option == question.correctAnswer
                                        isCurrentAnswerCorrect = isCorrect
                                        isAnswerChecked = true
                                        
                                        scope.launch {
                                            // 1. Run Animations based on result
                                            if (isCorrect) {
                                                scaleBounce.animateTo(1.1f, tween(100))
                                                scaleBounce.animateTo(1.0f, spring(Spring.DampingRatioHighBouncy))
                                            } else {
                                                repeat(3) {
                                                    shakeOffset.animateTo(15f, tween(50))
                                                    shakeOffset.animateTo(-15f, tween(50))
                                                }
                                                shakeOffset.animateTo(0f)
                                            }
                                            
                                            // 2. Short delay for user to see the result color (800ms)
                                            delay(800)
                                            
                                            // 3. Auto-submit and reset for next question
                                            viewModel.submitAnswer(
                                                isCorrect = isCorrect,
                                                points = if (difficulty == "Mudah") 10 else 15
                                            )
                                            selectedOption = null
                                            isAnswerChecked = false
                                            isCurrentAnswerCorrect = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = JavaQuestPrimary)
        }
    }
}
