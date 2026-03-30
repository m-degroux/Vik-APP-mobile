package fr.math.vikapp.data

import retrofit2.http.*

interface VikApiService {
    @Headers("Accept: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Headers("Accept: application/json")
    @POST("signup")
    suspend fun signup(@Body request: SignupRequest): LoginResponse

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String)

    @Headers("Accept: application/json")
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): User

    @Headers("Accept: application/json")
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profileData: Map<String, String>
    ): User

    @Headers("Accept: application/json")
    @GET("raid")
    suspend fun getRaids(): RaidResponse

    @Headers("Accept: application/json")
    @GET("clubs")
    suspend fun getClubs(): List<Club>

    @Headers("Accept: application/json")
    @GET("races")
    suspend fun getRaces(): List<Race>

    @Headers("Accept: application/json")
    @GET("races/{id}")
    suspend fun getRaceById(@Path("id") id: Int): Race
}
