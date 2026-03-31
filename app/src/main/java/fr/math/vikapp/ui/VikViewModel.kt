package fr.math.vikapp.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.math.vikapp.data.*
import kotlinx.coroutines.launch

class VikViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VikRepository(application)

    var currentUser by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)
    var raids by mutableStateOf<List<Raid>>(emptyList())
    var clubs by mutableStateOf<List<Club>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var searchQuery by mutableStateOf("")
    var filteredRaids by mutableStateOf<List<Raid>>(emptyList())

    var isDarkMode by mutableStateOf(false)
    var notificationsEnabled by mutableStateOf(true)
    var language by mutableStateOf("Français")

    init {
        fetchRaids()
        fetchClubs()
        loadLocalUser()
    }

    private fun loadLocalUser() {
        val localDb = LocalDatabase(getApplication())
        currentUser = localDb.getUser()
    }

    // GET
    fun fetchRaids() {
        viewModelScope.launch {
            isLoading = true
            try {
                raids = repository.getRaids()
                filteredRaids = raids
            } catch (e: Exception) {
                errorMessage = "Mode hors-ligne : données locales chargées"
            } finally {
                isLoading = false
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

    // GET
    fun fetchClubs() {
        viewModelScope.launch {
            try {
                clubs = repository.getClubs()
            } catch (e: Exception) {
            }
        }
    }

    // POST
    fun login(request: LoginRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.login(request)
                if (response.success && response.access_token != null) {
                    token = response.access_token
                    currentUser = repository.getUser(response.access_token)
                    repository.syncDirtyData(response.access_token)
                    onSuccess()
                } else {
                    errorMessage = "Identifiants incorrects"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erreur de connexion"
            } finally {
                isLoading = false
            }
        }
    }

    // POST
    fun signup(request: SignupRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.signup(request)
                if (response.success && response.access_token != null) {
                    token = response.access_token
                    currentUser = repository.getUser(response.access_token)
                    onSuccess()
                } else {
                    errorMessage = "Échec de l'inscription"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erreur d'inscription"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProfile(userData: Map<String, String>) {
        val currentToken = token ?: return
        viewModelScope.launch {
            try {
                currentUser = repository.updateProfile(currentToken, userData)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    // POST
    fun logout() {
        viewModelScope.launch {
            try {
                token?.let { repository.logout(it) }
            } catch (e: Exception) {
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
