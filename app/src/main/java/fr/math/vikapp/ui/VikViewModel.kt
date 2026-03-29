package fr.math.vikapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.math.vikapp.data.Club
import fr.math.vikapp.data.Raid
import fr.math.vikapp.data.RaidResponse
import fr.math.vikapp.data.User
import fr.math.vikapp.data.VikRepository
import kotlinx.coroutines.launch

class VikViewModel : ViewModel() {
    private val repository = VikRepository()

    var currentUser by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)
    var raids by mutableStateOf<List<Raid>>(emptyList())
    var clubs by mutableStateOf<List<Club>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Paramètres
    var isDarkMode by mutableStateOf(false)
    var notificationsEnabled by mutableStateOf(true)
    var language by mutableStateOf("Français")

    init {
        fetchRaids()
        fetchClubs()
    }

    fun fetchRaids() {
        viewModelScope.launch {
            try {
                val response: RaidResponse = repository.getRaids()
                if (response.success) {
                    raids = response.data
                } else {
                    errorMessage = "Erreur lors de la récupération des raids"
                }
            } catch (e: Exception) {
                errorMessage = "Erreur réseau : ${e.message}"
            }
        }
    }

    fun fetchClubs() {
        viewModelScope.launch {
            try {
                clubs = repository.getClubs()
            } catch (e: Exception) {
                errorMessage = "Erreur lors du chargement des clubs"
            }
        }
    }

    fun login(credentials: Map<String, String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.login(credentials)
                token = response.access_token ?: response.token
                currentUser = response.user
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Échec de la connexion"
            } finally {
                isLoading = false
            }
        }
    }

    fun signup(userData: Map<String, String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.signup(userData)
                token = response.access_token ?: response.token
                currentUser = response.user
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Échec de l'inscription : ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            token?.let { repository.logout(it) }
            token = null
            currentUser = null
        }
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }
}
