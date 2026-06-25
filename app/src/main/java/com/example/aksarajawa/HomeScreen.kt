package com.example.aksarajawa

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: GameViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userProfile by viewModel.currentUser.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val headerOffset = remember { Animatable(-300f) }
    var cardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
        headerOffset.animateTo(0f, animationSpec = tween(700, easing = EaseOutQuad))
        cardVisible = true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.width(320.dp)
            ) {
                // Header Drawer Premium
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Brush.verticalGradient(colors = listOf(JavaQuestPrimary, JavaQuestSecondary)))
                ) {
                    JavaneseHeaderPattern(modifier = Modifier.fillMaxSize())
                    
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = if (!userProfile?.avatarUrl.isNullOrEmpty()) userProfile?.avatarUrl 
                                    else "https://api.dicebear.com/7.x/avataaars/svg?seed=${userProfile?.username}",
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(85.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                .shadow(12.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = userProfile?.username ?: "Pemain", 
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = Color.White)
                        )
                        
                        Text(
                            text = "Aksara Mastery: ${((userProfile?.totalScore ?: 0) / 10)}%",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "NAVIGASI", 
                        style = MaterialTheme.typography.labelLarge, 
                        color = JavaQuestTextSecondary,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    
                    // FIXED ORDER: Beranda, Profile, Leaderboard, Kamus
                    DrawerMenuItemPremium(
                        label = "Beranda",
                        icon = Icons.Default.Home,
                        isSelected = currentRoute == Screen.Home.route,
                        onClick = { scope.launch { drawerState.close() } }
                    )
                    DrawerMenuItemPremium(
                        label = "Profile",
                        icon = Icons.Default.Person,
                        isSelected = currentRoute == Screen.Profile.route,
                        onClick = { scope.launch { drawerState.close(); navController.navigate(Screen.Profile.route) } }
                    )
                    DrawerMenuItemPremium(
                        label = "Leaderboard",
                        icon = Icons.Default.Leaderboard,
                        isSelected = currentRoute == Screen.Leaderboard.route,
                        onClick = { scope.launch { drawerState.close(); navController.navigate(Screen.Leaderboard.route) } }
                    )
                    DrawerMenuItemPremium(
                        label = "Kamus",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        isSelected = currentRoute == Screen.Dictionary.route,
                        onClick = { scope.launch { drawerState.close(); navController.navigate(Screen.Dictionary.route) } }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Daily Goal Card
                    Surface(
                        color = JavaQuestPrimary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JavaQuestPrimary.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Flag, null, tint = JavaQuestPrimary, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Target Harian", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            Spacer(Modifier.height(12.dp))
                            DuolingoProgressBar(progress = 0.6f, progressColor = JavaQuestPrimary)
                            Text("60/100 XP hari ini", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))
                    
                    DrawerMenuItemPremium(
                        label = "Keluar Akun",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        color = JavaQuestError,
                        onClick = {
                            viewModel.signOut {
                                navController.navigate(Screen.Login.route) { popUpTo(0) }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Dictionary.route) },
                    containerColor = JavaQuestAccent,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.shadow(8.dp, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, "Kamus")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header utama di Home
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(translationY = headerOffset.value)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Brush.verticalGradient(colors = listOf(JavaQuestPrimary, JavaQuestSecondary)))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    JavaneseHeaderPattern(modifier = Modifier.fillMaxSize())

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, "Menu", tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("Sugeng Rawuh,", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        userProfile?.username ?: "Pemain", 
                                        color = Color.White, 
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StreakBadge(streak = 5) 
                                LevelBadge(levelName = "Lv.${((userProfile?.totalScore ?: 0) / 100) + 1}")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // XP Progress Card
                        val totalScore = userProfile?.totalScore ?: 0
                        val progress = (totalScore % 100) / 100f
                        PremiumCard(
                            onClick = { navController.navigate(Screen.Profile.route) },
                            containerColor = Color.White.copy(alpha = 0.15f),
                            borderColor = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, null, tint = JavaQuestAccent, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("XP Belajar", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Text("${totalScore % 100}/100 XP", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    DuolingoProgressBar(progress = progress, progressColor = JavaQuestAccent)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Pilih Tantanganmu", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))

                    AnimatedVisibility(visible = cardVisible, enter = slideInVertically { 50 } + fadeIn()) {
                        GameModeCardPremium(
                            title = "Tingkat Mudah",
                            desc = "Aksara Dasar (Carakan)",
                            icon = "ꦲ",
                            color = Color(0xFFE8F5E9),
                            accentColor = Color(0xFF2E7D32),
                            onClick = { viewModel.generateEasyQuestions(); navController.navigate(Screen.EasyGame.route) }
                        )
                    }

                    AnimatedVisibility(visible = cardVisible, enter = slideInVertically { 100 } + fadeIn()) {
                        GameModeCardPremium(
                            title = "Tingkat Sedang",
                            desc = "Aksara & Sandhangan",
                            icon = "ꦶ",
                            color = Color(0xFFFFF3E0),
                            accentColor = Color(0xFFE65100),
                            onClick = { viewModel.generateMediumQuestions(); navController.navigate(Screen.MediumGame.route) }
                        )
                    }

                    AnimatedVisibility(visible = cardVisible, enter = slideInVertically { 150 } + fadeIn()) {
                        GameModeCardPremium(
                            title = "Tingkat Sulit",
                            desc = "Menyusun Kata",
                            icon = "ꦧ",
                            color = Color(0xFFF3E5F5),
                            accentColor = Color(0xFF7B1FA2),
                            onClick = { viewModel.loadHardQuestions(); navController.navigate(Screen.HardGame.route) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun DrawerMenuItemPremium(
    label: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    isSelected: Boolean = false,
    onClick: () -> Unit, 
    color: Color = JavaQuestTextPrimary
) {
    val bgColor = if (isSelected) JavaQuestPrimary.copy(alpha = 0.1f) else Color.Transparent
    val tintColor = if (isSelected) JavaQuestPrimary else color

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = tintColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label, 
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                ),
                color = tintColor
            )
        }
    }
}

@Composable
fun BadgeMini(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GameModeCardPremium(title: String, desc: String, icon: String, color: Color, accentColor: Color, onClick: () -> Unit) {
    PremiumCard(onClick = onClick, containerColor = Color.White, borderColor = color) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp, color = accentColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = JavaQuestTextSecondary)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}
