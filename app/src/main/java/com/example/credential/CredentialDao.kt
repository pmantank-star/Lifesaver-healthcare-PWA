package com.example.credential

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {

    @Query("SELECT * FROM provider_credentials ORDER BY submissionDate DESC")
    fun getAllCredentials(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM provider_credentials WHERE providerId = :providerId LIMIT 1")
    fun getCredentialByProviderId(providerId: String): Flow<CredentialEntity?>

    @Query("SELECT * FROM provider_credentials WHERE id = :id LIMIT 1")
    suspend fun getCredentialById(id: String): CredentialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: CredentialEntity)

    @Query("UPDATE provider_credentials SET status = :status, adminReviewerId = :adminId, adminReviewNotes = :notes, verifiedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateAdminVerification(
        id: String,
        status: String,
        adminId: String,
        notes: String,
        timestamp: Long
    )

    @Query("UPDATE provider_credentials SET backgroundCheckStatus = :status, backgroundCheckReference = :reference WHERE id = :id")
    suspend fun updateBackgroundCheck(
        id: String,
        status: String,
        reference: String
    )
}
