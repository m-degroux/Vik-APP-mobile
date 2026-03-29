package fr.math.vikapp.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: Int? = null,
    val mem_name: String? = null,
    val mem_firstname: String? = null,
    val user_username: String? = null,
    val mem_email: String? = null,
    val club_id: Int? = null
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val access_token: String? = null,
    val token: String? = null, // Gardé pour la compatibilité si utilisé ailleurs
    val user: User? = null
)

@Serializable
data class Club(
    val club_id: Int,
    val club_name: String,
    val club_address: String? = null,
    val club_active: Int? = null
)

@Serializable
data class RaidResponse(
    val success: Boolean,
    val data: List<Raid>
)

@Serializable
data class Raid(
    val id: Int,
    val name: String,
    val registration: RaidRegistration? = null,
    val dates: RaidDates? = null,
    val location: RaidLocation? = null,
    val picture: String? = null,
    val is_ongoing: Boolean? = null,
    val min_age: Int? = null,
    val races_count: Int? = null,
    val countdown: String? = null
)

@Serializable
data class RaidRegistration(
    val start: String,
    val end: String
)

@Serializable
data class RaidDates(
    val start: String,
    val end: String
)

@Serializable
data class RaidLocation(
    val place: String,
    val lat: String? = null,
    val lng: String? = null
)
