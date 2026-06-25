package com.example.aksarajawa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brush.verticalGradient(colors = listOf(JavaQuestPrimary, JavaQuestSecondary)))
                    .statusBarsPadding()
            ) {
                JavaneseHeaderPattern(modifier = Modifier.fillMaxSize())
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Tentang Aplikasi", 
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        ) 
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // App Logo / Icon
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ꦗ", fontSize = 60.sp, color = JavaQuestPrimary, fontWeight = FontWeight.Black)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "JavaQuest v1.0",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "Sinau Aksara Dadi Nyenengake",
                style = MaterialTheme.typography.bodyMedium,
                color = JavaQuestTextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Info Cards
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AboutInfoCard(
                    icon = Icons.Default.HistoryEdu,
                    title = "Misi Kami",
                    description = "Melestarikan budaya Jawa melalui teknologi modern dan metode pembelajaran gamifikasi yang interaktif."
                )
                
                AboutInfoCard(
                    icon = Icons.Default.Code,
                    title = "Teknologi",
                    description = "Dibangun menggunakan Jetpack Compose, Firebase, dan prinsip Desain Material 3 untuk pengalaman pengguna terbaik."
                )
                
                AboutInfoCard(
                    icon = Icons.Default.Laptop,
                    title = "Tim Pengembang",
                    description = "Dikembangkan dengan penuh dedikasi untuk membantu generasi muda belajar aksara dengan cara yang seru."
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "© 2024 JavaQuest Team",
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AboutInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    PremiumCard(onClick = {}, borderColor = JavaQuestPrimary.copy(alpha = 0.1f)) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(JavaQuestPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = JavaQuestPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = JavaQuestTextSecondary)
            }
        }
    }
}
