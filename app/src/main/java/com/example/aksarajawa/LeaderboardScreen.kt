package com.example.aksarajawa

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(navController: NavController, viewModel: GameViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Mudah", "Sedang", "Sulit")
    val listState = rememberLazyListState()

    LaunchedEffect(selectedTab) {
        viewModel.loadLeaderboard(tabs[selectedTab])
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(8.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(colors = listOf(JavaQuestPrimary, JavaQuestSecondary)))
                    .statusBarsPadding()
            ) {
                JavaneseHeaderPattern(modifier = Modifier.matchParentSize())

                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Papan Peringkat",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadLeaderboard(tabs[selectedTab]) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.MilitaryTech, null, tint = JavaQuestAccent, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "LIGA AKSARA",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = JavaQuestAccent,
                            letterSpacing = 2.sp
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = JavaQuestPrimary,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(4.dp)
                                .padding(horizontal = 40.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(JavaQuestPrimary)
                        )
                    },
                    divider = { HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = JavaQuestPrimary)
                    }
                } else {
                    if (leaderboard.isEmpty()) {
                        EmptyLeaderboard(onRefresh = { viewModel.loadLeaderboard(tabs[selectedTab]) })
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(bottom = 140.dp)
                        ) {
                            item {
                                LeaderboardPodiumFromEntries(
                                    topEntries = leaderboard.take(3),
                                    currentUserId = currentUser?.id ?: ""
                                )
                            }

                            itemsIndexed(leaderboard) { index, entry ->
                                if (index >= 3) {
                                    LeaderboardEntryItem(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                        rank = index + 1,
                                        entry = entry,
                                        isCurrentUser = entry.userId == currentUser?.id
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val myRankIndex = leaderboard.indexOfFirst { it.userId == currentUser?.id }
            if (myRankIndex != -1) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    StickyEntryRankFooter(
                        myRankIndex = myRankIndex,
                        totalCount = leaderboard.size,
                        entry = leaderboard[myRankIndex]
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardPodiumFromEntries(topEntries: List<LeaderboardEntry>, currentUserId: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        if (topEntries.size >= 2) {
            PodiumEntryItem(entry = topEntries[1], rank = 2, height = 120.dp, color = Color(0xFFC0C0C0), isCurrentUser = topEntries[1].userId == currentUserId)
        }

        if (topEntries.isNotEmpty()) {
            PodiumEntryItem(entry = topEntries[0], rank = 1, height = 155.dp, color = Color(0xFFFFD700), isCurrentUser = topEntries[0].userId == currentUserId)
        }

        if (topEntries.size >= 3) {
            PodiumEntryItem(entry = topEntries[2], rank = 3, height = 100.dp, color = Color(0xFFCD7F32), isCurrentUser = topEntries[2].userId == currentUserId)
        }
    }
}

@Composable
fun PodiumEntryItem(
    entry: LeaderboardEntry,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    isCurrentUser: Boolean
) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    val displayName = entry.username.ifEmpty { "Pemain" }
    // Implementasi Avatar dengan Fallback ke DiceBear PNG
    val avatarUrl = if (!entry.avatarUrl.isNullOrEmpty()) entry.avatarUrl 
                    else "https://api.dicebear.com/7.x/avataaars/png?seed=${displayName}"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            if (rank == 1) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(20.dp, CircleShape, ambientColor = color, spotColor = color)
                        .background(color.copy(alpha = 0.2f), CircleShape)
                )
            }

            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(if (rank == 1) 86.dp else 70.dp)
                    .clip(CircleShape)
                    .border(4.dp, color, CircleShape)
                    .background(Color.White, CircleShape)
                    .shadow(12.dp, CircleShape),
                contentScale = ContentScale.Crop
            )

            Surface(
                color = color,
                shape = CircleShape,
                modifier = Modifier.size(28.dp).offset(y = 10.dp).border(2.dp, Color.White, CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "$rank", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = displayName,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = if (isCurrentUser) JavaQuestPrimary else JavaQuestTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(text = "${entry.score} XP", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = JavaQuestPrimary)

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(85.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.05f)))),
            contentAlignment = Alignment.TopCenter
        ) {
            Icon(Icons.Default.Star, null, tint = color.copy(alpha = 0.4f), modifier = Modifier.padding(top = 12.dp).size(24.dp))
        }
    }
}

@Composable
fun LeaderboardEntryItem(
    modifier: Modifier = Modifier,
    rank: Int,
    entry: LeaderboardEntry,
    isCurrentUser: Boolean
) {
    val displayName = entry.username.ifEmpty { "Pemain" }
    // Implementasi Avatar dengan Fallback ke DiceBear PNG
    val avatarUrl = if (!entry.avatarUrl.isNullOrEmpty()) entry.avatarUrl 
                    else "https://api.dicebear.com/7.x/avataaars/png?seed=${displayName}"

    PremiumCard(
        modifier = modifier,
        containerColor = if (isCurrentUser) JavaQuestPrimary.copy(alpha = 0.08f) else Color.White,
        borderColor = if (isCurrentUser) JavaQuestPrimary.copy(alpha = 0.4f) else Color(0xFFECEAF3)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$rank",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = if (isCurrentUser) JavaQuestPrimary else JavaQuestTextSecondary,
                modifier = Modifier.width(36.dp)
            )
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White, CircleShape)
                    .border(2.dp, if (isCurrentUser) JavaQuestPrimary else Color.Transparent, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentUser) FontWeight.Black else FontWeight.Bold,
                    color = if (isCurrentUser) JavaQuestPrimary else JavaQuestTextPrimary
                )
                Text(
                    text = if (rank <= 3) "Jawara Aksara" else "Pelajar Setia",
                    fontSize = 12.sp,
                    color = JavaQuestTextSecondary
                )
            }
            Text(text = "${entry.score} XP", fontSize = 16.sp, fontWeight = FontWeight.Black, color = JavaQuestPrimary)
        }
    }
}

@Composable
fun StickyEntryRankFooter(myRankIndex: Int, totalCount: Int, entry: LeaderboardEntry) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(32.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "PERINGKAT ANDA",
                    style = MaterialTheme.typography.labelLarge,
                    color = JavaQuestPrimary,
                    fontWeight = FontWeight.Black
                )
                Surface(color = JavaQuestPrimary.copy(alpha = 0.1f), shape = CircleShape) {
                    Text(
                        "Top ${(myRankIndex + 1) * 100 / (if (totalCount > 0) totalCount else 1)}%",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = JavaQuestPrimary
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LeaderboardEntryItem(
                rank = myRankIndex + 1,
                entry = entry,
                isCurrentUser = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
