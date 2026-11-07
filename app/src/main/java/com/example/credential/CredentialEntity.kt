package com.example.credential

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ProviderCredential
import com.example.model.ProviderSpecialty
import com.example.model.VerificationStatus

@Entity(tableName = "provider_credentials")
data class CredentialEntity(
    @PrimaryKey
    val id: String,
    val providerId: String,
    val fullName: String,
    val specialty: String,
    val licenseCouncil: String,
    val licenseNumber: String,
    val expiryDate: String,
    val nationalIdType: String,
    val nationalIdMasked: String,
    val documentFileName: String,
    val documentSha256: String,
    val status: String, // PENDING_REVIEW, VERIFIED, REJECTED
    val backgroundCheckStatus: String, // PASSED, IN_PROGRESS, FLAGGED
    val backgroundCheckReference: String,
    val councilRegistryVerified: Boolean = true,
    val adminReviewerId: String? = null,
    val adminReviewNotes: String? = null,
    val submissionDate: String,
    val verifiedTimestamp: Long? = null
) {
    fun toDomainModel(): ProviderCredential {
        val mappedSpecialty = try {
            ProviderSpecialty.valueOf(specialty)
        } catch (_: Exception) {
            ProviderSpecialty.NURSE
        }
        val mappedStatus = try {
            VerificationStatus.valueOf(status)
        } catch (_: Exception) {
            VerificationStatus.PENDING_REVIEW
        }

        return ProviderCredential(
            id = id,
            providerId = providerId,
            fullName = fullName,
            specialty = mappedSpecialty,
            licenseCouncil = licenseCouncil,
            licenseNumber = licenseNumber,
            expiryDate = expiryDate,
            nationalIdType = nationalIdType,
            nationalIdNumber = nationalIdMasked,
            documentFileName = documentFileName,
            status = mappedStatus,
            submissionDate = submissionDate
        )
    }
}
