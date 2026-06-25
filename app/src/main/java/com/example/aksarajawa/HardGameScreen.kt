package com.example.aksarajawa

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HardGameScreen(navController: NavController, viewModel: GameViewModel) {
    val questions by viewModel.hardQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val score by viewModel.gameScore.collectAsState()
    val wrongCount by viewModel.wrongCount.collectAsState()

    val scope = rememberCoroutineScope()
    
    // Lives logic
    val maxHearts = 5
    val currentHearts = (maxHearts - wrongCount).coerceAtLeast(0)

    val currentQuestion = questions.getOrNull(currentIndex)
    val userSelections = remember { mutableStateListOf<String>() }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    // Animations
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(currentIndex, currentHearts, questions) {
        if (questions.isNotEmpty()) {
            if (currentIndex >= questions.size || currentHearts <= 0) {
                delay(600)
                navController.navigate(Screen.Result.createRoute("Sulit")) {
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
                    IconButton(onClick = { viewModel.resetGame(); navController.popBackStack() }) {
                        Icon(Icons.Default.Close, null, tint = JavaQuestTextPrimary)
                    }

                    if (questions.isNotEmpty()) {
                        DuolingoProgressBar(
                            progress = currentIndex.toFloat() / questions.size.toFloat(),
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        HeartsCount(hearts = currentHearts)
                        StatHeaderItem(
                            icon = { Icon(Icons.Outlined.Lightbulb, null, tint = JavaQuestAccent, modifier = Modifier.size(16.dp)) },
                            text = "+$score"
                        )
                    }
                }
            }
        }
    ) { padding ->
        currentQuestion?.let { question ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Susunlah aksara dadi tembung:",
                        style = MaterialTheme.typography.titleMedium.copy(color = JavaQuestTextSecondary, fontWeight = FontWeight.Bold)
                    )
                    
                    Text(
                        text = question.displayLatin,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, color = JavaQuestPrimary),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Answer Slots Area
                    PremiumCard(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp).graphicsLayer { translationX = shakeOffset.value },
                        containerColor = Color.White,
                        borderColor = if (isAnswerChecked) (if (isCorrect) JavaQuestSuccess else JavaQuestError) else Color(0xFFECEAF3)
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (userSelections.isEmpty()) {
                                Text("Klik aksara ing ngisor...", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                            }
                            userSelections.forEachIndexed { index, char ->
                                AksaraTile(
                                    char = char,
                                    onClick = { if (!isAnswerChecked) userSelections.removeAt(index) },
                                    isSelected = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Options Area
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val availableOptions = remember(question) { 
                            (question.displayAksaraList + question.displayDistractors).shuffled() 
                        }
                        
                        availableOptions.forEach { char ->
                            val countInSelections = userSelections.count { it == char }
                            val countInOptions = availableOptions.count { it == char }
                            val isUsed = countInSelections >= countInOptions

                            AksaraTile(
                                char = char,
                                isVisible = !isUsed,
                                onClick = { if (!isAnswerChecked) userSelections.add(char) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(140.dp))
                }

                // Action Button Area
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                    if (!isAnswerChecked) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().background(Color.White).padding(24.dp),
                            color = Color.White, shadowElevation = 8.dp
                        ) {
                            DuolingoButton(
                                text = "PERIKSA",
                                onClick = {
                                    if (userSelections.isNotEmpty()) {
                                        val userAnswer = userSelections.joinToString("")
                                        val correctAnswer = question.displayAksaraList.joinToString("")
                                        isCorrect = userAnswer == correctAnswer
                                        isAnswerChecked = true
                                        
                                        if (!isCorrect) {
                                            scope.launch {
                                                repeat(3) {
                                                    shakeOffset.animateTo(15f, tween(50))
                                                    shakeOffset.animateTo(-15f, tween(50))
                                                }
                                                shakeOffset.animateTo(0f)
                                            }
                                        }
                                    }
                                },
                                enabled = userSelections.isNotEmpty()
                            )
                        }
                    }

                    // Slide-up Feedback
                    AnimatedVisibility(
                        visible = isAnswerChecked,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        FeedbackSheet(
                            isCorrect = isCorrect,
                            correctAnswer = question.displayAksaraList.joinToString(""),
                            onContinue = {
                                viewModel.submitAnswer(isCorrect, 20)
                                userSelections.clear()
                                isAnswerChecked = false
                            }
                        )
                    }
                }
            }
        }
    }
}
