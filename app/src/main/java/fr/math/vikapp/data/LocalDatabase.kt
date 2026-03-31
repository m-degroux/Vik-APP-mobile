package fr.math.vikapp.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalDatabase(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vikapp_local_db", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    // Sauvegarde des Raids
    fun saveRaids(raids: List<Raid>) {
        prefs.edit().putString("raids_cache", json.encodeToString(raids)).apply()
    }

    fun getRaids(): List<Raid> {
        val data = prefs.getString("raids_cache", null) ?: return emptyList()
        return try { json.decodeFromString(data) } catch (e: Exception) { emptyList() }
    }

    // Sauvegarde des Clubs
    fun saveClubs(clubs: List<Club>) {
        prefs.edit().putString("clubs_cache", json.encodeToString(clubs)).apply()
    }

    fun getClubs(): List<Club> {
        val data = prefs.getString("clubs_cache", null) ?: return emptyList()
        return try { json.decodeFromString(data) } catch (e: Exception) { emptyList() }
    }

    // Sauvegarde du Profil Utilisateur
    fun saveUser(user: User?) {
        if (user == null) {
            prefs.edit().remove("user_cache").apply()
        } else {
            prefs.edit().putString("user_cache", json.encodeToString(user)).apply()
        }
    }

    fun getUser(): User? {
        val data = prefs.getString("user_cache", null) ?: return null
        return try { json.decodeFromString(data) } catch (e: Exception) { null }
    }

    // Gestion des données sales
    fun markProfileAsDirty(userData: Map<String, String>) {
        prefs.edit().putString("dirty_profile", json.encodeToString(userData)).apply()
    }

    fun getDirtyProfile(): Map<String, String>? {
        val data = prefs.getString("dirty_profile", null) ?: return null
        return try { json.decodeFromString(data) } catch (e: Exception) { null }
    }

    fun clearDirtyProfile() {
        prefs.edit().remove("dirty_profile").apply()
    }
}
