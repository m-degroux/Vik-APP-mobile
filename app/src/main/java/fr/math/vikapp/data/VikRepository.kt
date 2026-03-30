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

    suspend fun login(request: LoginRequest) = service.login(request)
    suspend fun signup(request: SignupRequest) = service.signup(request)
    suspend fun logout(token: String) = service.logout("Bearer $token")
    suspend fun getUser(token: String) = service.getUser("Bearer $token")
    suspend fun updateProfile(token: String, profileData: Map<String, String>) = 
        service.updateProfile("Bearer $token", profileData)

    suspend fun getRaids(): List<Raid> {
        return try {
            val response = service.getRaids()
            response.data
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getClubs(): List<Club> = service.getClubs()
    suspend fun getRaces(): List<Race> = service.getRaces()
    suspend fun getRaceById(id: Int): Race = service.getRaceById(id)
}
