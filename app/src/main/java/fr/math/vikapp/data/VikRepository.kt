package fr.math.vikapp.data

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class VikRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sae3g5.skopee.fr/api/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val service = retrofit.create(VikApiService::class.java)

    suspend fun login(credentials: Map<String, String>) = service.login(credentials)
    suspend fun signup(userData: Map<String, String>) = service.signup(userData)
    suspend fun logout(token: String) = service.logout("Bearer $token")
    suspend fun getUser(token: String) = service.getUser("Bearer $token")
    suspend fun updateProfile(token: String, profileData: Map<String, String>) = 
        service.updateProfile("Bearer $token", profileData)
    suspend fun getRaids(): RaidResponse = service.getRaids()
}
