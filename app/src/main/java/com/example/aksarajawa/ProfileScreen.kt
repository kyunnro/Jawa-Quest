package com.example.aksarajawa

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: GameViewModel) {
    val profile by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State internal agar input user tidak "mental" atau tertimpa otomatis saat sinkronisasi DB
    var usernameState by remember { mutableStateOf("") }
    var avatarUrlState by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // 1. Inisialisasi awal: Ambil data dari profil hanya saat layar pertama kali dibuka
    LaunchedEffect(profile) {
        if (!isInitialized && profile != null) {
            usernameState = profile?.username ?: ""
            avatarUrlState = profile?.displayAvatar ?: ""
            isInitialized = true
        }
    }

    // Tampilkan error jika ada
    LaunchedEffect(error) {
        error?.let {
            val message = if (it.contains("404")) "Gagal: Pastikan Firebase Storage sudah aktif (Klik 'Get Started' di Console)." else it
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            avatarUrlState = "" // Kosongkan link manual agar preview beralih ke hasil upload galeri
            viewModel.uploadAvatar(it) 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- SEKSI FOTO PROFIL ---
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Gunakan avatarUrlState jika ada input link, jika tidak gunakan displayAvatar dari DB
                    val displayImage = if (avatarUrlState.isNotEmpty()) avatarUrlState 
                                      else profile?.displayAvatar ?: "https://api.dicebear.com/7.x/avataaars/svg?seed=$usernameState"
                    
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(displayImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(120.dp).padding(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                    }

                    FloatingActionButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Ubah Foto", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- BARIS STATISTIK ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total Skor",
                        value = "${profile?.totalScore ?: 0}",
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFFF59E0B)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Level",
                        value = "${((profile?.totalScore ?: 0) / 100) + 1}",
                        icon = Icons.Default.Star,
                        color = JavaQuestSecondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Informasi Akun", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                
                Spacer(modifier = Modifier.height(12.dp))

                // Input Nama
                OutlinedTextField(
                    value = usernameState,
                    onValueChange = { usernameState = it },
                    label = { Text("Nama Pengguna") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input URL Foto (Link .jpg / .png)
                OutlinedTextField(
                    value = avatarUrlState,
                    onValueChange = { avatarUrlState = it },
                    label = { Text("URL Foto (.jpg / .png)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = { Icon(Icons.Default.Link, null) },
                    placeholder = { Text("https://example.com/foto.jpg") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(40.dp))

                DuolingoButton(
                    text = "SIMPAN PERUBAHAN",
                    onClick = {
                        viewModel.updateProfile(usernameState, avatarUrlState) {
                            scope.launch {
                                showSuccessAnimation = true
                                delay(1200)
                                showSuccessAnimation = false
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && usernameState.isNotBlank()
                )
            }

            // Overlay Animasi Sukses
            AnimatedVisibility(visible = showSuccessAnimation, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                    PremiumCard(onClick = {}, modifier = Modifier.width(200.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, null, tint = JavaQuestSuccess, modifier = Modifier.size(64.dp))
                            Text("Tersimpan!", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
