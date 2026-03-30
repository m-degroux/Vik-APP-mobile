package fr.math.vikapp.data

import kotlinx.serialization.SerialName
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
    val token: String? = null,
    val user: User? = null
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
