package fr.math.vikapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.math.vikapp.data.*
import kotlinx.coroutines.launch

class VikViewModel : ViewModel() {
    private val repository = VikRepository()

    var currentUser by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)
    var raids by mutableStateOf<List<Raid>>(emptyList())
    var clubs by mutableStateOf<List<Club>>(emptyList())
    var races by mutableStateOf<List<Race>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isLoadingRaces by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Recherche
    var searchQuery by mutableStateOf("")
    var filteredRaids by mutableStateOf<List<Raid>>(emptyList())

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
            isLoading = true
            try {
                raids = repository.getRaids()
                filteredRaids = raids
            } catch (e: Exception) {
                errorMessage = "Erreur réseau : ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchRacesForRaid(raidId: Int) {
        viewModelScope.launch {
            isLoadingRaces = true
            races = emptyList()
            try {
                val allRaces = repository.getRaces()
                races = allRaces.filter { it.raid_id == raidId }
            } catch (e: Exception) {
                errorMessage = "Erreur lors du chargement des courses"
            } finally {
                isLoadingRaces = false
            }
        }
    }

    fun filterRaids(query: String) {
        searchQuery = query
        if (query.isBlank()) {
            filteredRaids = raids
            return
        }

        filteredRaids = raids.filter { 
            it.name.contains(query, ignoreCase = true) || 
            (it.place?.contains(query, ignoreCase = true) ?: false)
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

    fun login(request: LoginRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.login(request)
                if (response.success && response.access_token != null) {
                    token = response.access_token
                    // Récupération des informations complètes de l'utilisateur
                    currentUser = repository.getUser(response.access_token)
                    onSuccess()
                } else {
                    errorMessage = "Échec de la connexion"
                }
            } catch (e: Exception) {
                errorMessage = "Erreur : ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun signup(request: SignupRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.signup(request)
                if (response.success && response.access_token != null) {
                    token = response.access_token
                    // Récupération des informations complètes après inscription
                    currentUser = repository.getUser(response.access_token)
                    onSuccess()
                } else {
                    errorMessage = "Échec de l'inscription"
                }
            } catch (e: Exception) {
                errorMessage = "Erreur : ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                token?.let { repository.logout(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                token = null
                currentUser = null
            }
        }
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }
}
