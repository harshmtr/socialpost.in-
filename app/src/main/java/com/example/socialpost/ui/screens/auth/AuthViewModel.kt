package com.example.socialpost.ui.screens.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialpost.data.remote.LinkedInAuthApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val accessToken: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    savedStateHandle: SavedStateHandle,
    private val api: LinkedInAuthApi
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var currentAuthCode: String? = null

    init {
        // Automatically extract the code from the deep link URL
        val code: String? = savedStateHandle.get<String>("code")
        if (code != null) {
            currentAuthCode = code
            exchangeToken(code)
        } else {
            _authState.value = AuthState.Error("Authorization code not found in deep link.")
        }
    }

    fun retry() {
        currentAuthCode?.let {
            exchangeToken(it)
        } ?: run {
            _authState.value = AuthState.Error("No authorization code available to retry.")
        }
    }

    private fun exchangeToken(code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // In a production app, credentials should come from BuildConfig or a secure remote configuration
                val response = api.exchangeAuthCode(
                    code = code,
                    redirectUri = "https://socialpost.in/auth/linkedin/callback",
                    clientId = com.example.socialpost.BuildConfig.GEMINI_API_KEY, // replace with actual config
                    clientSecret = com.example.socialpost.BuildConfig.NEWS_API_KEY // replace with actual config
                )
                
                // Here you would typically persist the token securely using EncryptedSharedPreferences or DataStore
                _authState.value = AuthState.Success(response.accessToken)
                
            } catch (e: HttpException) {
                // Handle non-2xx network responses
                val errorBody = e.response()?.errorBody()?.string()
                _authState.value = AuthState.Error("Server error: ${e.code()} - $errorBody")
            } catch (e: IOException) {
                // Handle network failures (e.g., no internet)
                _authState.value = AuthState.Error("Network error. Please check your connection and try again.")
            } catch (e: Exception) {
                // Handle unexpected errors
                _authState.value = AuthState.Error(e.localizedMessage ?: "An unexpected error occurred.")
            }
        }
    }
}
