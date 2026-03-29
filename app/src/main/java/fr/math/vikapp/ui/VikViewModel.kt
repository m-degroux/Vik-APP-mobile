package fr.math.vikapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.math.vikapp.data.Raid
import fr.math.vikapp.data.User
import fr.math.vikapp.data.VikRepository
import kotlinx.coroutines.launch

class VikViewModel : ViewModel() {
    private val repository = VikRepository()

    var currentUser by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)
    var raids by mutableStateOf<List<Raid>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchRaids()
    }

    fun fetchRaids() {
        viewModelScope.launch {
            try {
                raids = repository.getRaids()
            } catch (e: Exception) {
                errorMessage = "Erreur lors de la récupération des raids"
            }
        }
    }

    fun login(credentials: Map<String, String>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.login(credentials)
                token = response.token
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
                token = response.token
                currentUser = response.user
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Échec de l'inscription"
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
}
