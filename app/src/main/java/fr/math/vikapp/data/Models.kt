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
data class Raid(
    val id: Int,
    val name: String,
    val description: String? = null,
    val date: String,
    val location: String? = null,
    val image_url: String? = null
)
