package fr.math.vikapp.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VikRepository(context: Context) {
    private val baseUrl = "https://sae3g5.skopee.fr/api/"
    private val localDb = LocalDatabase(context)

    private suspend fun makeRequest(
        endpoint: String,
        method: String,
        body: String? = null,
        token: String? = null
    ): String = withContext(Dispatchers.IO) {
        val url = URL(baseUrl + endpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = 15000 // Augmenté à 15s
        connection.readTimeout = 15000
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Content-Type", "application/json")
        token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }

        if (body != null) {
            connection.doOutput = true
            OutputStreamWriter(connection.outputStream).use { it.write(body) }
        }

        val responseCode = connection.responseCode
        val inputStream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        
        if (inputStream == null) {
            throw Exception("Erreur serveur ($responseCode) sans message.")
        }

        val response = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
        connection.disconnect()
        
        if (responseCode !in 200..299) {
            val errorMessage = try {
                val json = JSONObject(response)
                json.optString("message", json.optString("error", "Erreur $responseCode"))
            } catch (e: Exception) {
                "Erreur $responseCode"
            }
            throw Exception(errorMessage)
        }
        response
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        val body = JSONObject().apply {
            put("username", request.username)
            put("password", request.password)
        }.toString()
        
        val responseStr = makeRequest("login", "POST", body)
        if (responseStr.isBlank()) throw Exception("Réponse serveur vide")
        
        val jsonResponse = JSONObject(responseStr)
        val user = jsonResponse.optJSONObject("user")?.let { parseUser(it) }
        localDb.saveUser(user)
        
        return LoginResponse(
            success = jsonResponse.optBoolean("success", false),
            access_token = jsonResponse.optString("access_token", null),
            user = user
        )
    }

    suspend fun signup(request: SignupRequest): LoginResponse {
        val body = JSONObject().apply {
            put("mem_name", request.mem_name)
            put("mem_firstname", request.mem_firstname)
            put("mem_birthdate", request.mem_birthdate)
            put("mem_email", request.mem_email)
            put("mem_phone", request.mem_phone)
            put("mem_adress", request.mem_adress)
            put("mem_zipcode", request.mem_zipcode)
            put("user_username", request.user_username)
            put("user_password", request.user_password)
            put("user_password_confirmation", request.user_password_confirmation)
            put("club_id", request.club_id ?: JSONObject.NULL)
            put("mem_default_licence", request.mem_default_licence ?: JSONObject.NULL)
        }.toString()

        val jsonResponse = JSONObject(makeRequest("signup", "POST", body))
        val user = jsonResponse.optJSONObject("user")?.let { parseUser(it) }
        localDb.saveUser(user)

        return LoginResponse(
            success = jsonResponse.optBoolean("success", false),
            access_token = jsonResponse.optString("access_token", null),
            user = user
        )
    }

    suspend fun getUser(token: String): User {
        return try {
            val jsonResponse = JSONObject(makeRequest("user", "GET", null, token))
            val user = parseUser(jsonResponse)
            localDb.saveUser(user)
            user
        } catch (e: Exception) {
            localDb.getUser() ?: throw e
        }
    }

    suspend fun updateProfile(token: String, userData: Map<String, String>): User {
        return try {
            val body = JSONObject(userData).toString()
            val responseStr = makeRequest("user/profile", "PUT", body, token)
            val user = parseUser(JSONObject(responseStr))
            localDb.saveUser(user)
            localDb.clearDirtyProfile()
            user
        } catch (e: Exception) {
            localDb.markProfileAsDirty(userData)
            throw e
        }
    }

    suspend fun syncDirtyData(token: String) {
        val dirtyProfile = localDb.getDirtyProfile()
        if (dirtyProfile != null) {
            try {
                updateProfile(token, dirtyProfile)
            } catch (e: Exception) {
                // Ignore if it fails again during sync
            }
        }
    }

    suspend fun getRaids(): List<Raid> {
        return try {
            val jsonResponse = JSONObject(makeRequest("raid", "GET"))
            val dataArray = jsonResponse.optJSONArray("data") ?: JSONArray()
            val raids = mutableListOf<Raid>()
            for (i in 0 until dataArray.length()) {
                val raidObj = dataArray.getJSONObject(i)
                raids.add(parseRaid(raidObj))
            }
            if (raids.isNotEmpty()) localDb.saveRaids(raids)
            raids
        } catch (e: Exception) {
            val cache = localDb.getRaids()
            if (cache.isEmpty()) throw e else cache
        }
    }

    suspend fun getClubs(): List<Club> {
        return try {
            val jsonArray = JSONArray(makeRequest("clubs", "GET"))
            val clubs = mutableListOf<Club>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                clubs.add(Club(
                    club_id = obj.getInt("club_id"),
                    club_name = obj.getString("club_name")
                ))
            }
            localDb.saveClubs(clubs)
            clubs
        } catch (e: Exception) {
            localDb.getClubs()
        }
    }

    private fun parseRaid(raidObj: JSONObject): Raid {
        return Raid(
            id = raidObj.getInt("id"),
            name = raidObj.optString("name", "Sans nom"),
            registration = raidObj.optJSONObject("registration")?.let { Registration(it.optString("start"), it.optString("end")) },
            dates = raidObj.optJSONObject("dates")?.let { Dates(it.optString("start"), it.optString("end")) },
            place = raidObj.optString("raid_location", null),
            picture = raidObj.optString("raid_picture", null),
            min_age = if (raidObj.isNull("min_age")) null else raidObj.optInt("min_age"),
            races_count = if (raidObj.isNull("races_count")) null else raidObj.optInt("races_count"),
            countdown = raidObj.optString("countdown", null)
        )
    }

    private fun parseUser(obj: JSONObject): User {
        return User(
            user_id = if (!obj.isNull("user_id")) obj.optInt("user_id") else if (!obj.isNull("id")) obj.optInt("id") else null,
            id = if (!obj.isNull("id")) obj.optInt("id") else null,
            club_id = if (!obj.isNull("club_id")) obj.optInt("club_id") else null,
            mem_name = obj.optString("mem_name", obj.optString("name", null)),
            mem_firstname = obj.optString("mem_firstname", obj.optString("firstname", null)),
            mem_birthdate = obj.optString("mem_birthdate", null),
            mem_adress = obj.optString("mem_adress", null),
            mem_zipcode = obj.optString("mem_zipcode", null),
            mem_phone = obj.optString("mem_phone", null),
            mem_email = obj.optString("mem_email", null),
            mem_default_licence = obj.optString("mem_default_licence", null),
            user_username = obj.optString("user_username", null)
        )
    }

    suspend fun logout(token: String) {
        try { makeRequest("logout", "POST", null, token) } catch (e: Exception) {}
        localDb.saveUser(null)
    }
}
