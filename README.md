<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/logop.png" alt="JavaQuest Logo" width="120" height="120">
  <h1 align="center">JavaQuest</h1>
  <p align="center">
    <strong>Belajar Aksara Jawa Jadi Seru & Menyenangkan</strong>
    <br>
    Game edukasi interaktif untuk mempelajari aksara Jawa (Ha Na Ca Ra Ka)
    <br>
    berbasis Android dengan Kotlin & Jetpack Compose
  </p>
  <p>
    <img src="https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white">
    <img src="https://img.shields.io/badge/Compose-BOM%202024.02.01-4285F4?logo=jetpackcompose&logoColor=white">
    <img src="https://img.shields.io/badge/Android-API%2024%E2%80%9336-3DDC84?logo=android&logoColor=white">
    <img src="https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black">
    <img src="https://img.shields.io/badge/Material%20You-3-7C3AED?logo=materialdesign&logoColor=white">
    <img src="https://img.shields.io/badge/MVVM-Architecture-22C55E">
  </p>
</div>

---

## 📖 Tentang JavaQuest

**JavaQuest** adalah game edukasi Android yang dirancang untuk membantu siapa saja -- dari anak-anak hingga dewasa -- belajar **Aksara Jawa** (Hanacaraka) dengan cara yang interaktif dan menyenangkan. 

Aksara Jawa adalah sistem tulisan tradisional yang digunakan untuk menulis bahasa Jawa, memiliki keindahan dan nilai budaya yang tinggi. Namun, minat dan kemampuan membaca aksara Jawa semakin menurun di era digital. **JavaQuest hadir untuk menjembatani kesenjangan ini** melalui pendekatan gamifikasi yang modern.

### ✨ Fitur Utama

| Fitur | Deskripsi |
|---|---|
| 🎮 **3 Tingkat Kesulitan** | Mudah (aksara dasar), Sedang (aksara + sandhangan), Sulit (drag-and-drop puzzle) |
| 📚 **Kamus Aksara Lengkap** | 20 aksara dasar, sandhangan vokal, aksara murda, rekan, swara, dan angka Jawa |
| 🔥 **Leaderboard Real-time** | Bersaing dengan pemain lain secara langsung via Firebase |
| 👤 **Profil & Avatar** | Profil pengguna dengan upload foto dari galeri |
| ❤️ **Sistem Nyawa** | 3 nyawa per game -- jawab salah, nyawa berkurang! |
| 🏆 **Skor & Riwayat** | Skor otomatis tersimpan, lihat kemajuan belajarmu |
| 🎨 **Animasi Menarik** | Efek particle, animasi jatuh, dan transisi halus |

---

## 🎯 Gameplay

### Mudah (Easy)
10 soal pilihan ganda. Kamu akan melihat sebuah aksara Jawa, lalu memilih padanan latin yang benar. Cocok untuk pemula yang baru mengenal aksara dasar.

### Sedang (Medium)
10 soal pilihan ganda dengan tingkat kesulitan lebih tinggi. Menggabungkan aksara dasar dengan **sandhangan vokal** (i, u, e, o, ê).

### Sulit (Hard)
Mode **drag-and-drop**! Kamu diberikan sebuah kata dalam latin, lalu harus menyusun aksara Jawa yang benar dengan memilih dan mengurutkan karakter yang tersedia. Soal diambil langsung dari database Firebase.

---

## 📸 Tangkapan Layar

<details>
<summary>Klik untuk melihat tangkapan layar</summary>

| | | |
|---|---|---|
| <img src="app/src/main/res/drawable/splash.png" width="200"> | ![Screen 2]() | ![Screen 3]() |
| Splash Screen | Home Screen | Game Screen |

</details>

---

## 🧱 Tech Stack

### Bahasa & Framework
- **Kotlin** 2.1.0 -- Bahasa utama
- **Jetpack Compose** -- UI deklaratif modern (Material Design 3)
- **Navigation Compose** 2.8.5 -- Navigasi antar layar
- **ViewModel + LiveData** -- Arsitektur MVVM

### Backend & Database
- **Firebase Authentication** -- Login/register dengan email & password
- **Firebase Realtime Database** -- Menyimpan profil pengguna, leaderboard, dan soal
- **Firebase Storage** -- Upload avatar pengguna

### Library Pendukung
| Library | Fungsi |
|---|---|
| `coil-compose` 2.7.0 | Loading gambar头像 |
| `kotlinx-serialization` 1.7.3 | Serialisasi JSON |
| `WorkManager` 2.10.0 | Notifikasi background |
| `Material Icons Extended` | Ikon-ikon Material Design |

---

## 🏗️ Arsitektur

```
com.example.aksarajawa
├── MainActivity.kt          # Entry point utama
├── Navigation.kt            # Rute navigasi (12 screen)
├── Models.kt                # Data class & daftar aksara
├── Theme.kt                 # Tema Material 3 (warna, tipografi, shape)
│
├── GameViewModel.kt         # ViewModel utama (MVVM)
├── GameRepository.kt        # Repository Firebase (auth, profil, leaderboard)
├── FirebaseRepository.kt    # Repository Firebase pendukung
├── SupabaseClient.kt        # Placeholder (sudah tidak dipakai)
│
├── Screens                  # 12 Screen Composables
│   ├── SplashScreen.kt
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── HomeScreen.kt
│   ├── GameScreen.kt        # Mudah & Sedang
│   ├── HardGameScreen.kt    # Sulit (drag-and-drop)
│   ├── ResultScreen.kt
│   ├── ProfileScreen.kt
│   ├── LeaderboardScreen.kt
│   ├── AboutScreen.kt
│   ├── DictionaryScreen.kt
│   └── DictionaryDetailScreen.kt
│
├── Components.kt            # Komponen UI bersama
└── NotificationHelper.kt    # Helper notifikasi
```

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio Ladybug (2024.2.1) atau lebih baru
- JDK 17+
- Perangkat Android API 24+ (Android 7.0 Nougat) atau emulator
- Koneksi internet (untuk Firebase)

### Langkah-langkah
1. **Clone repositori**
   ```bash
   git clone https://github.com/kyunnro/Jawa-Quest.git
   ```

2. **Buka di Android Studio**
   - File → Open → Pilih folder `Jawa-Quest`
   - Tunggu Gradle sync selesai

3. **Konfigurasi Firebase**
   - Buka [Firebase Console](https://console.firebase.google.com)
   - Buat project baru (atau gunakan yang sudah ada)
   - Aktifkan **Authentication** (Email/Password)
   - Aktifkan **Realtime Database** (buat dengan aturan `test mode`)
   - Aktifkan **Storage**
   - Download `google-services.json` dan letakkan di folder `app/`
   
4. **Jalankan**
   - Pilih perangkat/emulator
   - Klik **Run** atau tekan `Shift + F10`

---

## 🗺️ Roadmap

- [x] Sistem login/register dengan email
- [x] 3 mode permainan (Mudah, Sedang, Sulit)
- [x] Leaderboard real-time
- [x] Profil pengguna & avatar
- [x] Kamus aksara Jawa
- [ ] Dark mode
- [ ] Soal tidak terbatas (procedural generation)
- [ ] Multiplayer real-time
- [ ] Achievement & badge system
- [ ] Dukungan bahasa Inggris
- [ ] Versi iOS (KMM)

---

## 📄 Lisensi

Distributed under the MIT License. See `LICENSE` for more information.

---

<div align="center">
  Dibuat dengan ❤️ untuk melestarikan budaya Jawa
  
  <br>
  
  **"Sabda pandhita ratu, mulang marang putra wayah, aja nganti ilang kabudhayan Jawa."**
</div>
