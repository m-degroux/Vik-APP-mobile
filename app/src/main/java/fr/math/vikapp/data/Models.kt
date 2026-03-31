package fr.math.vikapp.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: Int? = null,
    val id: Int? = null,
    val club_id: Int? = null,
    val mem_name: String? = null,
    val name: String? = null,
    val mem_firstname: String? = null,
    val firstname: String? = null,
    val mem_birthdate: String? = null,
    val mem_adress: String? = null,
    val mem_zipcode: String? = null,
    val mem_phone: String? = null,
    val mem_email: String? = null,
    val mem_default_licence: String? = null,
    val user_username: String? = null
) {
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
    val username: String? = null,
    val password: String? = null
)

@Serializable
data class SignupRequest(
    val mem_name: String? = null,
    val mem_firstname: String? = null,
    val mem_birthdate: String? = null,
    val mem_email: String? = null,
    val mem_phone: String? = null,
    val mem_adress: String? = null,
    val mem_zipcode: String? = null,
    val user_username: String? = null,
    val user_password: String? = null,
    val user_password_confirmation: String? = null,
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
    val place: String? = null,
    val picture: String? = null,
    val min_age: Int? = null,
    val races_count: Int? = null,
    val countdown: String? = null
)

@Serializable
data class Race(
    val id: Int? = null,
    val raid_id: Int? = null,
    val name: String? = null,
    val format: String? = null,
    val distance: String? = null,
    val price: String? = null
)

@Serializable
data class Club(
    val club_id: Int,
    val club_name: String,
    val club_address: String? = null,
    val club_active: Int? = null
)
