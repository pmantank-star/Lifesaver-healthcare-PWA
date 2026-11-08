package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.OmaRepository
import com.example.location.LocationManager
import com.example.location.UserCoordinates
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.HealthProvider
import com.example.model.NotificationType
import com.example.model.ProviderSpecialty
import com.example.notification.OmaNotificationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LifeSavers", appName)
  }

  @Test
  fun `test location manager distance calculation and on demand matching algorithm`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val locationManager = LocationManager(context)

    // Distance between Victoria Island and Lekki Phase 1
    val dist = locationManager.calculateDistanceKm(6.4281, 3.4219, 6.4474, 3.4723)
    assertTrue("Distance should be approximately 6 km", dist in 4f..8f)

    val eta = locationManager.calculateEtaMinutes(dist)
    assertTrue("ETA should be calculated realistically", eta in 10..30)

    val sampleProviders = listOf(
        HealthProvider(
            id = "p-far",
            name = "Far Doctor",
            title = "MD",
            specialty = ProviderSpecialty.DOCTOR,
            rating = 5.0f,
            reviewCount = 10,
            distanceKm = 10f,
            etaMinutes = 40,
            baseRate = 20000,
            licenseNumber = "123",
            yearsExperience = 5,
            bio = "Far doctor",
            services = emptyList(),
            avatarInitials = "FD",
            completedGigs = 50,
            latitude = 6.5500, // Further away
            longitude = 3.3500,
            radarX = 0f,
            radarY = 0f
        ),
        HealthProvider(
            id = "p-close",
            name = "Nearby Nurse",
            title = "RN",
            specialty = ProviderSpecialty.NURSE,
            rating = 4.9f,
            reviewCount = 20,
            distanceKm = 0.5f,
            etaMinutes = 5,
            baseRate = 15000,
            licenseNumber = "456",
            yearsExperience = 4,
            bio = "Close nurse",
            services = emptyList(),
            avatarInitials = "NN",
            completedGigs = 100,
            latitude = 6.4290, // Very close to 6.4281
            longitude = 3.4230,
            radarX = 0f,
            radarY = 0f
        )
    )

    val userCoords = UserCoordinates(6.4281, 3.4219)
    val ranked = locationManager.matchAndRankProviders(userCoords, sampleProviders)

    assertEquals("Closest provider should be ranked first", "p-close", ranked.first().id)
    assertTrue("Closest provider distance should be less than 1km", ranked.first().distanceKm < 1.0f)
  }

  @Test
  fun `test real time push notifications to providers within radius and patient acceptance with ETA`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val notificationManager = OmaNotificationManager(context)

    val booking = Booking(
        id = "OMA-99120",
        serviceId = "srv-01",
        serviceName = "Wound Dressing & Sterile Care",
        providerId = "prov-01",
        providerName = "Nurse Amara Okonkwo",
        providerTitle = "RN, Critical Care Specialist",
        providerSpecialty = ProviderSpecialty.NURSE,
        patientName = "Chidi Anya",
        patientAddress = "14 Adeola Odeku St, Victoria Island",
        patientPhone = "+234 803 555 0192",
        symptomNotes = "Post-op wound dressing",
        status = BookingStatus.SEARCHING,
        baseCost = 16000,
        suppliesCost = 0,
        totalCost = 16000,
        etaMinutes = 8
    )

    // 1. Notify nearby providers within radius
    notificationManager.notifyNearbyProvidersOfRequest(
        booking = booking,
        radiusKm = 5.0f,
        notifiedProvidersCount = 3
    )

    val providerNotif = notificationManager.inAppNotification.value
    assertNotNull("Provider notification should be emitted", providerNotif)
    assertEquals(NotificationType.PROVIDER_DISPATCH_ALERT, providerNotif?.type)
    assertTrue("Notification should mention service name", providerNotif?.message?.contains("Wound Dressing") == true)
    assertTrue("Notification should mention radius", providerNotif?.title?.contains("5.0km") == true)

    // 2. Notify patient upon provider acceptance with ETA
    val acceptedBooking = booking.copy(
        status = BookingStatus.MATCHED,
        etaMinutes = 8
    )
    notificationManager.notifyPatientOfAcceptance(acceptedBooking)

    val patientNotif = notificationManager.inAppNotification.value
    assertNotNull("Patient notification should be emitted", patientNotif)
    assertEquals(NotificationType.PATIENT_ACCEPTED_ALERT, patientNotif?.type)
    assertTrue("Patient notification should specify provider name", patientNotif?.message?.contains("Nurse Amara Okonkwo") == true)
    assertTrue("Patient notification should specify estimated arrival time", patientNotif?.message?.contains("8 mins") == true)
    assertEquals(8, patientNotif?.etaMinutes)
  }

  @Test
  fun `test secure backend provider credential verification pipeline and admin audit approval`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = com.example.credential.OmaDatabase.getInstance(context)
    val dao = database.credentialDao()
    val verificationService = com.example.credential.CredentialVerificationService(dao)

    // 1. Verify license image SHA-256 computation
    val licenseDocName = "dr_ebuka_mdcn_folio_2026.jpg"
    val licenseNumber = "MDCN/GP/2018/19482"
    val hash = verificationService.computeSha256("$licenseNumber:$licenseDocName:prov-02")
    assertNotNull(hash)
    assertEquals("SHA-256 length should be 64 characters", 64, hash.length)

    // 2. Automated background check and government identity validation
    val (idValid, idMsg) = verificationService.verifyGovernmentIdentity("1098234821")
    assertTrue("NIN format should be valid", idValid)
    assertTrue("NIN verification should confirm NIMC registry match", idMsg.contains("NIMC"))

    val (bgStatus, bgRef) = verificationService.runCriminalBackgroundCheck("Dr. Ebuka Nwosu")
    assertEquals("Criminal background check should pass", "PASSED", bgStatus)
    assertTrue("Clearance reference should be generated", bgRef.startsWith("CID-CLEARED-"))

    val (boardValid, boardMsg) = verificationService.queryMedicalBoardRegistry(
        "Medical and Dental Council of Nigeria (MDCN)",
        licenseNumber
    )
    assertTrue("Medical board query should confirm active standing", boardValid)

    // 3. Submit credential package into secure database and associate with provider profile
    val repository = com.example.data.OmaRepository()
    repository.setVerificationService(verificationService)

    val submitted = repository.submitCredential(
        fullName = "Dr. Ebuka Nwosu",
        specialty = ProviderSpecialty.DOCTOR,
        council = "Medical and Dental Council of Nigeria (MDCN)",
        licenseNumber = licenseNumber,
        docName = licenseDocName,
        nationalIdType = "National Identity Number (NIN)",
        nationalIdRaw = "1098234821",
        expiryDate = "2027-12-31",
        documentHash = hash
    )

    assertEquals("Submitted credential status should initially be PENDING_REVIEW", "PENDING_REVIEW", submitted.status)
    assertEquals(hash, submitted.documentSha256)
    assertEquals("****-****-4821", submitted.nationalIdMasked)

    // 4. Manual Verification by Administrator
    val adminId = "ADM-LAGOS-CHIEF"
    val auditNotes = "Verified practicing folio on MDCN active registry. All criminal and identity clearances confirmed."
    val approved = repository.adminVerifyCredential(
        credentialId = submitted.id,
        approve = true,
        adminId = adminId,
        notes = auditNotes
    )

    assertNotNull("Approved credential entity should not be null", approved)
    assertEquals("Status should now be VERIFIED", "VERIFIED", approved?.status)
    assertEquals(adminId, approved?.adminReviewerId)
    assertEquals(auditNotes, approved?.adminReviewNotes)

    // 5. Verify that verified status is associated with provider profile
    val provider = repository.providers.value.find { it.licenseNumber == licenseNumber || it.name.contains("Ebuka") }
    assertNotNull("Provider profile should exist", provider)
    assertEquals(
        "Provider verificationStatus should be updated to VERIFIED upon admin approval",
        com.example.model.VerificationStatus.VERIFIED,
        provider?.verificationStatus
    )
  }

  @Test
  fun `test authentication architecture with login registration and demo profile session`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authViewModel = com.example.viewmodel.AuthViewModel()
    authViewModel.initAuthManager(context)

    // Initially logged out
    var uiState = authViewModel.uiState.value
    org.junit.Assert.assertNull(uiState.currentUser)
    assertEquals(com.example.model.AuthMode.LOGIN, uiState.authMode)

    // Switch mode to Register
    authViewModel.setAuthMode(com.example.model.AuthMode.REGISTER)
    authViewModel.setSelectedRole(com.example.model.UserRole.PROVIDER)
    authViewModel.onFullNameChange("Nurse Fatima Aliyu")
    authViewModel.onEmailChange("fatima.nurse@omahealth.org")
    authViewModel.onPasswordChange("nursePassword123")
    authViewModel.onPhoneNumberChange("+234 802 333 4455")
    authViewModel.onSpecialtyChange(ProviderSpecialty.NURSE)
    authViewModel.onLicenseNumberChange("NMCN-2022-99881")

    uiState = authViewModel.uiState.value
    assertEquals(com.example.model.AuthMode.REGISTER, uiState.authMode)
    assertEquals(com.example.model.UserRole.PROVIDER, uiState.selectedRole)
    assertEquals("Nurse Fatima Aliyu", uiState.fullName)
    assertEquals("fatima.nurse@omahealth.org", uiState.email)
    assertEquals("NMCN-2022-99881", uiState.licenseNumber)

    // Demo Provider Login
    authViewModel.signInAsDemo(com.example.model.UserRole.PROVIDER)
    uiState = authViewModel.uiState.value
    assertNotNull("User should be logged in via demo session", uiState.currentUser)
    assertEquals(com.example.model.UserRole.PROVIDER, uiState.currentUser?.role)
    assertEquals("MDCN-2018-8491", uiState.currentUser?.licenseNumber)

    // Sign out
    authViewModel.signOut()
    uiState = authViewModel.uiState.value
    org.junit.Assert.assertNull("User should be logged out after signOut", uiState.currentUser)

    // Demo Patient Login
    authViewModel.signInAsDemo(com.example.model.UserRole.PATIENT)
    uiState = authViewModel.uiState.value
    assertNotNull("Patient should be logged in", uiState.currentUser)
    assertEquals(com.example.model.UserRole.PATIENT, uiState.currentUser?.role)
  }

  @Test
  fun `test initial asset initialization lifecycle and loading indicator state flow`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = com.example.viewmodel.OmaViewModel()

    // Initially, asset initialization is in progress
    assertTrue("Asset initialization should be active on startup", viewModel.isAssetInitializing.value)
    assertTrue("Progress should be within [0, 1]", viewModel.assetInitProgress.value in 0.0f..1.0f)
    assertNotNull("Status message should be descriptive", viewModel.assetInitMessage.value)

    // Trigger asset initialization
    viewModel.initializeAppAssets(context)

    // Verify status message exists and describes the process
    assertTrue("Status message should not be empty", viewModel.assetInitMessage.value.isNotEmpty())
  }

  @Test
  fun `test patient reviews and testimonials for booked doctors and nurses`() {
    val repository = OmaRepository()
    val reviews = repository.patientReviews.value

    // 1. Initial reviews should include doctor, nurse/CHW, and physiotherapist
    assertTrue("Reviews list should not be empty", reviews.isNotEmpty())
    val doctorReview = reviews.find { it.providerSpecialty == ProviderSpecialty.DOCTOR }
    val chwReview = reviews.find { it.providerSpecialty == ProviderSpecialty.COMMUNITY_HEALTH }
    val physioReview = reviews.find { it.providerSpecialty == ProviderSpecialty.PHYSIOTHERAPIST }

    assertNotNull("Should have review for booked doctor", doctorReview)
    assertEquals("Dr. Babatunde Vance", doctorReview?.providerName)
    assertEquals(5, doctorReview?.rating)
    assertTrue("Doctor testimonial should mention symptoms or diagnosis", doctorReview?.testimonial?.contains("symptoms") == true)
    assertTrue("Doctor review should have positive tags", doctorReview?.tags?.isNotEmpty() == true)

    assertNotNull("Should have review for community health worker / nurse", chwReview)
    assertEquals("Halima Musa", chwReview?.providerName)
    assertEquals(5, chwReview?.rating)

    assertNotNull("Should have review for physiotherapist", physioReview)
    assertEquals("Nkechi Eze", physioReview?.providerName)
    assertEquals(5, physioReview?.rating)

    // 2. Submit a new review for Nurse Amara Okonkwo
    val newNurseReview = com.example.model.PatientReview(
        id = "rev-test-01",
        bookingId = "OMA-89412",
        providerId = "prov-01",
        providerName = "Nurse Amara Okonkwo",
        providerTitle = "RN, Critical & Post-Op Specialist",
        providerSpecialty = ProviderSpecialty.NURSE,
        serviceName = "Wound Dressing & Sterile Care",
        rating = 5,
        testimonial = "Nurse Amara did a sterile, gentle dressing on my surgical wound. Completely painless and very reassuring.",
        dateText = "Just now",
        tags = listOf("Hospital-Grade Hygiene", "Gentle Touch", "Punctual"),
        verifiedBooking = true,
        avatarInitials = "AO"
    )

    repository.submitReview(newNurseReview)
    val updatedReviews = repository.patientReviews.value
    assertEquals(4, updatedReviews.size)
    assertEquals(newNurseReview.id, updatedReviews.first().id)
    assertEquals("Nurse Amara Okonkwo", updatedReviews.first().providerName)
    assertEquals(5, updatedReviews.first().rating)
  }
}


