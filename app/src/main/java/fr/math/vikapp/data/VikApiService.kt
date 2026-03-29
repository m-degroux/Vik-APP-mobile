package fr.math.vikapp.data

import retrofit2.http.*

interface VikApiService {
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): LoginResponse

    @POST("signup")
    suspend fun signup(@Body userData: Map<String, String>): LoginResponse

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String)

    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): User

    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profileData: Map<String, String>
    ): User

    @GET("raid")
    suspend fun getRaids(): RaidResponse
}
