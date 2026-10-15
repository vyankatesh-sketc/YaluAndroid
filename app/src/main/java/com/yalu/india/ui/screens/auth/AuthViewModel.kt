package com.yalu.india.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AuthRepository
import com.yalu.india.network.models.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoginTab = MutableStateFlow(true)
    val isLoginTab: StateFlow<Boolean> = _isLoginTab.asStateFlow()

    private val _authState = MutableStateFlow<NetworkResult<AuthResponse>>(NetworkResult.Idle)
    val authState: StateFlow<NetworkResult<AuthResponse>> = _authState.asStateFlow()

    fun setLoginTab(isLogin: Boolean) {
        _isLoginTab.value = isLogin
        _authState.value = NetworkResult.Idle
    }

    fun login(loginInput: String, pass: String) {
        if (loginInput.isBlank() || pass.isBlank()) {
            _authState.value = NetworkResult.Error("Please enter your email/phone and password.")
            return
        }
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading
            _authState.value = authRepository.login(loginInput, pass)
        }
    }

    fun register(name: String, email: String, phone: String, pass: String) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
            _authState.value = NetworkResult.Error("Please fill in all registration fields.")
            return
        }
        if (pass.length < 6) {
            _authState.value = NetworkResult.Error("Password must be at least 6 characters.")
            return
        }
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading
            _authState.value = authRepository.register(name, email, phone, pass)
        }
    }

    fun resetState() {
        _authState.value = NetworkResult.Idle
    }
}
