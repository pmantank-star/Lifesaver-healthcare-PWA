package com.example.model

enum class AuthMode {
    LOGIN,
    REGISTER
}

enum class Gender(val displayName: String) {
    FEMALE("Female"),
    MALE("Male")
}

data class LifeSaversUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val gender: Gender = Gender.FEMALE,
    val phoneNumber: String? = null,
    val specialty: ProviderSpecialty? = null,
    val licenseNumber: String? = null,
    val isVerified: Boolean = false,
    val isGoogleUser: Boolean = false
)

typealias OmaUser = LifeSaversUser

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: LifeSaversUser? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val authMode: AuthMode = AuthMode.LOGIN,
    val selectedRole: UserRole = UserRole.PATIENT,
    val selectedGender: Gender = Gender.FEMALE,
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val fullName: String = "",
    val phoneNumber: String = "",
    val specialty: ProviderSpecialty = ProviderSpecialty.NURSE,
    val licenseNumber: String = ""
)
