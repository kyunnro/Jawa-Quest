package com.example.aksarajawa

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Dasar") }
    // Kategori Murda dan Swara dihapus sesuai permintaan
    val categories = listOf("Dasar", "Sandhangan", "Rekan", "Angka")
    
    val currentList = remember(selectedCategory) {
        when(selectedCategory) {
            "Dasar" -> aksaraDasar
            "Sandhangan" -> sandhanganVokal
            "Rekan" -> aksaraRekan
            "Angka" -> angkaJawa
            else -> aksaraDasar
        }
    }
    
    val filteredAksara = remember(searchQuery, currentList) {
        if (searchQuery.isEmpty()) {
            currentList
        } else {
            currentList.filter { it.latin.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .shadow(8.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(colors = listOf(JavaQuestPrimary, JavaQuestSecondary)))
                    .statusBarsPadding()
            ) {
                JavaneseHeaderPattern(modifier = Modifier.matchParentSize())
                
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Kamus Aksara", 
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Category Selector Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category; searchQuery = "" },
                        label = { Text(category, fontWeight = FontWeight.ExtraBold) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JavaQuestPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = JavaQuestPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = JavaQuestPrimary.copy(alpha = 0.3f),
                            selectedBorderColor = JavaQuestPrimary,
                            borderWidth = 2.dp,
                            selectedBorderWidth = 2.dp,
                            enabled = true,
                            selected = selectedCategory == category
                        )
                    )
                }
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .shadow(12.dp, CircleShape),
                shape = CircleShape,
                color = Color.White
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari aksara...", color = JavaQuestTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = JavaQuestPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = JavaQuestTextSecondary)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )
            }

            if (filteredAksara.isEmpty()) {
                EmptyDictionary()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(filteredAksara, key = { _, item -> item.latin + selectedCategory }) { index, aksara ->
                        val scale = remember { Animatable(0f) }
                        LaunchedEffect(selectedCategory) {
                            scale.snapTo(0f)
                            scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }

                        PremiumAksaraCard(
                            aksara = aksara,
                            modifier = Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value)
                        ) {
                            // Hanya kategori Dasar yang bisa masuk ke detail variasi
                            if (selectedCategory == "Dasar") {
                                navController.navigate(Screen.DictionaryDetail.createRoute(aksara.latin))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyDictionary() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ꦎꦫꦄꦤ", 
                fontSize = 72.sp, 
                color = JavaQuestPrimary.copy(alpha = 0.05f), 
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Mboten wonten aksara", 
                color = JavaQuestTextSecondary, 
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun PremiumAksaraCard(
    aksara: AksaraData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    PremiumCard(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        borderColor = JavaQuestPrimary.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = JavaQuestPrimary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (aksara.latin.isEmpty()) "A" else aksara.latin.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (aksara.char.isNotEmpty()) {
                Text(
                    text = aksara.char,
                    fontSize = if (aksara.char.length > 2) 28.sp else 36.sp,
                    color = JavaQuestPrimary.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "—",
                    fontSize = 28.sp,
                    color = JavaQuestPrimary.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
