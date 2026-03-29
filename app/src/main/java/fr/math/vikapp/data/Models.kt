package fr.math.vikapp.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val profile_photo_url: String? = null
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
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
    val min_age: String? = null,
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
    val lat: String,
    val lng: String
)
