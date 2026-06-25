package com.example.aksarajawa

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
data class Profile(
    var id: String = "",
    var username: String = "",
    @get:PropertyName("avatarUrl") @set:PropertyName("avatarUrl") var avatarUrl: String? = null,
    @get:PropertyName("avatar_url") @set:PropertyName("avatar_url") var avatar_url: String? = null,
    @get:PropertyName("totalScore") @set:PropertyName("totalScore") var totalScore: Int = 0
) {
    val displayAvatar: String? get() = if (!avatarUrl.isNullOrEmpty()) avatarUrl else avatar_url
}

@IgnoreExtraProperties
@Serializable
data class LeaderboardEntry(
    var userId: String = "",
    var username: String = "",
    @get:PropertyName("avatarUrl") @set:PropertyName("avatarUrl") var avatarUrl: String? = null,
    @get:PropertyName("avatar_url") @set:PropertyName("avatar_url") var avatar_url: String? = null,
    var score: Int = 0,
    var difficulty: String = "",
    var createdAt: String = ""
) {
    val displayAvatar: String? get() = if (!avatarUrl.isNullOrEmpty()) avatarUrl else avatar_url
}

@Serializable
data class AksaraData(val latin: String, val char: String, val category: String = "Dasar")

val aksaraDasar = listOf(
    AksaraData("ha", "ꦲ"), AksaraData("na", "ꦤ"), AksaraData("ca", "ꦕ"), AksaraData("ra", "ꦫ"), AksaraData("ka", "ꦏ"),
    AksaraData("da", "ꦢ"), AksaraData("ta", "ꦠ"), AksaraData("sa", "ꦱ"), AksaraData("wa", "ꦮ"), AksaraData("la", "ꦭ"),
    AksaraData("pa", "ꦥ"), AksaraData("dha", "ꦝ"), AksaraData("ja", "ꦗ"), AksaraData("ya", "ꦪ"), AksaraData("nya", "ꦚ"),
    AksaraData("ma", "ꦩ"), AksaraData("ga", "ꦒ"), AksaraData("ba", "ꦧ"), AksaraData("tha", "ꦛ"), AksaraData("nga", "ꦔ")
)

val sandhanganVokal = listOf(
    AksaraData("a", "", "Sandhangan"),
    AksaraData("i", "ꦶ", "Sandhangan"),
    AksaraData("u", "ꦸ", "Sandhangan"),
    AksaraData("e", "ꦺ", "Sandhangan"),
    AksaraData("ê", "ꦼ", "Sandhangan"),
    AksaraData("o", "ꦺꦴ", "Sandhangan")
)

val aksaraMurda = listOf(
    AksaraData("Na", "ꦆ", "Murda"), AksaraData("Ka", "ꦇ", "Murda"), AksaraData("Ta", "ꦈ", "Murda"),
    AksaraData("Sa", "ꦉ", "Murda"), AksaraData("Pa", "ꦊ", "Murda"), AksaraData("Nya", "ꦋ", "Murda"),
    AksaraData("Ga", "ꦌ", "Murda"), AksaraData("Ba", "ꦍ", "Murda")
)

val aksaraRekan = listOf(
    AksaraData("kha", "ꦏ꦳", "Rekan"), AksaraData("dza", "ꦢ꦳", "Rekan"), AksaraData("fa/va", "ꦥ곳", "Rekan"),
    AksaraData("za", "ꦗ꦳", "Rekan"), AksaraData("gha", "ꦒ꦳", "Rekan")
)

val aksaraSwara = listOf(
    AksaraData("A", "ꦄ", "Swara"), AksaraData("I", "ꦅ", "Swara"), AksaraData("U", "ꦆ", "Swara"),
    AksaraData("E", "ꦇ", "Swara"), AksaraData("O", "ꦈ", "Swara")
)

val angkaJawa = listOf(
    AksaraData("1", "꧑", "Angka"), AksaraData("2", "꧒", "Angka"), AksaraData("3", "꧓", "Angka"),
    AksaraData("4", "꧔", "Angka"), AksaraData("5", "꧕", "Angka"), AksaraData("6", "꧖", "Angka"),
    AksaraData("7", "꧗", "Angka"), AksaraData("8", "꧘", "Angka"), AksaraData("9", "꧙", "Angka"),
    AksaraData("0", "꧐", "Angka")
)

val allAksara = aksaraDasar + sandhanganVokal + aksaraMurda + aksaraRekan + aksaraSwara + angkaJawa

data class DictionaryItem(
    val aksaraBase: String = "",
    val aksaraVowel: String = "",
    val exampleAksara: String = "",
    val example: String = "",
    val meaning: String = ""
)

@Serializable
data class QuestionLocal(
    val question: String,
    val correctAnswer: String,
    val options: List<String>
)

@IgnoreExtraProperties
@Serializable
data class QuestionHard(
    var id: Long = 0,
    var latinWord: String = "",
    var aksaraList: List<String> = emptyList(),
    var distractors: List<String> = emptyList(),
    var latin_word: String? = null,
    var aksara_list: List<String>? = null,
    var distractors_list: List<String>? = null
) {
    val displayLatin: String get() = if (latinWord.isNotEmpty()) latinWord else (latin_word ?: "")
    val displayAksaraList: List<String> get() = if (aksaraList.isNotEmpty()) aksaraList else (aksara_list ?: emptyList())
    val displayDistractors: List<String> get() = if (distractors.isNotEmpty()) distractors else (distractors_list ?: emptyList())
}
