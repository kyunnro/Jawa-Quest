package com.example.aksarajawa

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryDetailScreen(navController: NavController, aksaraName: String) {
    val context = LocalContext.current
    // Mencari di semua aksara (Dasar, Murda, Rekan)
    val baseAksara = allAksara.find { it.latin.equals(aksaraName, ignoreCase = true) }
    
    // Database Contoh Kata yang lebih lengkap
    val examples = mapOf(
        "ha" to listOf("Hana", "Hidu", "Hulu", "Hero", "Hobi", "Henti"),
        "na" to listOf("Nanas", "Nila", "Nuri", "Nenek", "Noni", "Neneng"),
        "ca" to listOf("Candi", "Citra", "Cukup", "Ceri", "Coklat", "Cepat"),
        "ra" to listOf("Raja", "Ribu", "Rusa", "Remba", "Roda", "Resi"),
        "ka" to listOf("Kaka", "Kiri", "Kuku", "Kera", "Kopi", "Keris"),
        "da" to listOf("Dada", "Dinas", "Duku", "Desa", "Dosa", "Debu"),
        "ta" to listOf("Tani", "Tiga", "Tugu", "Teka", "Toko", "Tebu"),
        "sa" to listOf("Sana", "Sini", "Susu", "Seta", "Sore", "Sega"),
        "wa" to listOf("Wali", "Wira", "Wulu", "Weka", "Wolu", "Weni"),
        "la" to listOf("Laku", "Lilin", "Lulu", "Lela", "Loro", "Lega"),
        "pa" to listOf("Padi", "Pipa", "Pupu", "Peta", "Pondok", "Pepes"),
        "dha" to listOf("Dharma", "Dhingin", "Dhuku", "Dhesa", "Dhosa", "Dhemit"),
        "ja" to listOf("Jala", "Jika", "Juru", "Jera", "Jodoh", "Jembar"),
        "ya" to listOf("Yakin", "Yiyi", "Yuyu", "Yen", "Yoyo", "Yekti"),
        "nya" to listOf("Nyata", "Nyiru", "Nyuwun", "Nyele", "Nyonya", "Nyamlek"),
        "ma" to listOf("Mata", "Mili", "Mumu", "Meja", "Madu", "Meme"),
        "ga" to listOf("Gajah", "Gigi", "Guru", "Geta", "Golek", "Gela"),
        "ba" to listOf("Bata", "Biru", "Buku", "Beda", "Bolu", "Bekti"),
        "tha" to listOf("Thathak", "Thithuk", "Thuthuk", "Thethek", "Thothok", "Thethel"),
        "nga" to listOf("Ngakak", "Ngimpi", "Ngungun", "Ngenest", "Ngopi", "Ngeli"),
        // Contoh untuk Murda
        "na_murda" to listOf("Narendra", "Nindya", "Nugraha", "Netra", "Nirmala", "Nagari"),
        "ka_murda" to listOf("Kusuma", "Kirti", "Kuncoro", "Kresna", "Katon", "Kaloka")
    )

    val currentExamples = examples[aksaraName.lowercase()] 
        ?: examples["${aksaraName.lowercase()}_murda"]
        ?: listOf("Tuladha 1", "Tuladha 2", "Tuladha 3", "Tuladha 4", "Tuladha 5", "Tuladha 6")

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                color = JavaQuestPrimary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Brush.verticalGradient(colors = listOf(JavaQuestPrimary, JavaQuestSecondary)))
                        .statusBarsPadding()
                ) {
                    JavaneseHeaderPattern(modifier = Modifier.matchParentSize())
                    CenterAlignedTopAppBar(
                        title = { 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Variasi '${aksaraName.uppercase()}'", 
                                    color = Color.White, 
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = if (baseAksara?.category == "Murda") "Aksara Murda" else "Sandhangan Swara",
                                    color = Color.White.copy(alpha = 0.7f), 
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    ) { padding ->
        if (baseAksara != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Pelajari bagaimana aksara '${baseAksara.latin}' berubah bunyi saat diberi Sandhangan Swara (vokal).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = JavaQuestTextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                itemsIndexed(sandhanganVokal.zip(currentExamples)) { index, (sandhangan, example) ->
                    val scale = remember { Animatable(0.8f) }
                    LaunchedEffect(Unit) {
                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                    }

                    // Logika penggabungan aksara dengan sandhangan
                    val combinedChar = baseAksara.char + sandhangan.char
                    val combinedLatin = if (sandhangan.latin == "a") baseAksara.latin 
                                      else baseAksara.latin.dropLast(1) + sandhangan.latin

                    PremiumVariationCard(
                        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value },
                        char = combinedChar,
                        latin = if (sandhangan.latin == "a") baseAksara.latin else combinedLatin,
                        example = example
                    )
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aksara tidak ditemukan", color = JavaQuestTextSecondary)
            }
        }
    }
}

@Composable
fun PremiumVariationCard(
    modifier: Modifier = Modifier,
    char: String,
    latin: String,
    example: String
) {
    PremiumCard(
        modifier = modifier,
        borderColor = JavaQuestPrimary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(JavaQuestPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = char, fontSize = 38.sp, color = JavaQuestPrimary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = latin.uppercase(), 
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = JavaQuestTextPrimary)
                )
                Text(
                    text = "Contoh: $example", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = JavaQuestTextSecondary
                )
            }
        }
    }
}
