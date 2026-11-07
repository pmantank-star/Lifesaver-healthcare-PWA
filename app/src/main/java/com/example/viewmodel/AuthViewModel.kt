package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.OmaAuthManager
import com.example.model.AuthMode
import com.example.model.AuthUiState
import com.example.model.Gender
import com.example.model.OmaUser
import com.example.model.ProviderSpecialty
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: OmaAuthManager? = null
) : ViewModel() {

    private var internalAuthManager: OmaAuthManager? = authManager

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun initAuthManager(context: Context) {
        if (internalAuthManager == null) {
            internalAuthManager = OmaAuthManager(context)
            viewModelScope.launch {
                internalAuthManager?.currentUser?.collect { user ->
                    _uiState.update { it.copy(currentUser = user) }
                }
            }
        }
    }

    fun setAuthMode(mode: AuthMode) {
        _uiState.update { it.copy(authMode = mode, errorMessage = null, successMessage = null) }
    }

    fun setSelectedRole(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun setSelectedGender(gender: Gender) {
        _uiState.update { it.copy(selectedGender = gender) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onPhoneNumberChange(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
    }

    fun onSpecialtyChange(specialty: ProviderSpecialty) {
        _uiState.update { it.copy(specialty = specialty) }
    }

    fun onLicenseNumberChange(license: String) {
        _uiState.update { it.copy(licenseNumber = license) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signInWithEmail(onSuccess: (OmaUser) -> Unit = {}) {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email address") }
            return
        }
        if (!state.email.contains("@") || !state.email.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = internalAuthManager?.signInWithEmail(state.email, state.password)
                ?: Result.failure(Exception("Auth service not initialized"))

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        successMessage = "Welcome back, ${user.displayName}!"
                    )
                }
                onSuccess(user)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Sign-in failed. Please check your credentials."
                    )
                }
            }
        }
    }

    fun registerWithEmail(onSuccess: (OmaUser) -> Unit = {}) {
        val state = _uiState.value
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your full name") }
            return
        }
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email address") }
            return
        }
        if (!state.email.contains("@") || !state.email.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.selectedRole == UserRole.PROVIDER && state.licenseNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your professional license number") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = internalAuthManager?.registerWithEmail(
                email = state.email,
                password = state.password,
                fullName = state.fullName,
                role = state.selectedRole,
                phoneNumber = state.phoneNumber,
                specialty = if (state.selectedRole == UserRole.PROVIDER) state.specialty else null,
                licenseNumber = if (state.selectedRole == UserRole.PROVIDER) state.licenseNumber else null
            ) ?: Result.failure(Exception("Auth service not initialized"))

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        successMessage = "Account created successfully for ${user.displayName}!"
                    )
                }
                onSuccess(user)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Registration failed. Please try again."
                    )
                }
            }
        }
    }

    fun signInWithGoogle(context: Context, serverClientId: String? = null, onSuccess: (OmaUser) -> Unit = {}) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = internalAuthManager?.signInWithGoogle(context, serverClientId)
                ?: Result.failure(Exception("Auth service not initialized"))

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        successMessage = "Signed in with Google as ${user.displayName}"
                    )
                }
                onSuccess(user)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Google sign in failed"
                    )
                }
            }
        }
    }

    fun signInAsDemo(role: UserRole, onSuccess: (OmaUser) -> Unit = {}) {
        internalAuthManager?.signInAsDemo(role)
        val user = internalAuthManager?.currentUser?.value
        if (user != null) {
            _uiState.update {
                it.copy(
                    currentUser = user,
                    errorMessage = null,
                    successMessage = "Signed in as demo ${if (role == UserRole.PATIENT) "Patient" else "Healthcare Provider"}"
                )
            }
            onSuccess(user)
        }
    }

    fun signOut() {
        internalAuthManager?.signOut()
        _uiState.update {
            it.copy(
                currentUser = null,
                email = "",
                password = "",
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun updateProfile(displayName: String, phoneNumber: String) {
        val current = _uiState.value.currentUser ?: return
        val updated = current.copy(
            displayName = displayName.trim(),
            phoneNumber = phoneNumber.trim()
        )
        _uiState.update { it.copy(currentUser = updated, successMessage = "Profile updated") }
    }
}
