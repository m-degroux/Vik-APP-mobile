package fr.math.vikapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id") val user_id: Int? = null,
    @SerialName("id") val id: Int? = null, // Alternative pour la réponse login
    val club_id: Int? = null,
    @SerialName("mem_name") val mem_name: String? = null,
    @SerialName("name") val name: String? = null, // Alternative pour la réponse login
    @SerialName("mem_firstname") val mem_firstname: String? = null,
    @SerialName("firstname") val firstname: String? = null, // Alternative pour la réponse login
    val mem_birthdate: String? = null,
    val mem_adress: String? = null,
    val mem_zipcode: String? = null,
    val mem_phone: String? = null,
    val mem_email: String? = null,
    val mem_default_licence: String? = null,
    val user_username: String? = null
) {
    // Getters utilitaires pour gérer les différences entre login et profil
    val displayId: Int? get() = user_id ?: id
    val displayName: String? get() = mem_name ?: name
    val displayFirstName: String? get() = mem_firstname ?: firstname
}

@Serializable
data class LoginResponse(
    val success: Boolean,
    val access_token: String? = null,
    val token_type: String? = null,
    val user: User? = null
)

@Serializable
data class LoginRequest(
    val username: String, // Changé de user_username -> username selon l'image
    val password: String  // Changé de user_password -> password selon l'image
)

@Serializable
data class SignupRequest(
    val mem_name: String,
    val mem_firstname: String,
    val mem_birthdate: String,
    val mem_email: String,
    val mem_phone: String,
    val mem_adress: String,
    val mem_zipcode: String,
    val user_username: String,
    val user_password: String,
    val user_password_confirmation: String,
    val club_id: Int? = null,
    val mem_default_licence: String? = null
)

@Serializable
data class RaidResponse(
    val success: Boolean,
    val data: List<Raid> = emptyList()
)

@Serializable
data class Registration(
    val start: String? = null,
    val end: String? = null
)

@Serializable
data class Dates(
    val start: String? = null,
    val end: String? = null
)

@Serializable
data class Raid(
    val id: Int,
    val name: String,
    val registration: Registration? = null,
    val dates: Dates? = null,
    val contact: String? = null,
    val website: String? = null,
    @SerialName("raid_location") val place: String? = null,
    @SerialName("raid_picture") val picture: String? = null,
    val min_age: Int? = null,
    val races_count: Int? = null,
    val countdown: String? = null
)

@Serializable
data class Race(
    @SerialName("race_id") val id: Int,
    @SerialName("raid_id") val raid_id: Int,
    @SerialName("race_name") val name: String,
    @SerialName("type_id") val type_id: Int? = null,
    @SerialName("race_length") val distance: String? = null,
    @SerialName("dif_id") val elevation: Int? = null,
    @SerialName("race_start_date") val start_time: String? = null,
    @SerialName("race_meal_price") val price: String? = null
)

@Serializable
data class Club(
    val club_id: Int,
    val club_name: String,
    val club_address: String? = null,
    val club_active: Int? = null
)
