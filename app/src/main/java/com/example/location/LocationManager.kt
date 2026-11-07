package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.model.HealthProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class UserCoordinates(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * LocationManager tracks user device coordinates using Google Play Services Location
 * (FusedLocationProviderClient) and implements the on-demand matching algorithm to
 * discover, calculate distance/ETA, and rank nearby healthcare freelance providers.
 */
class LocationManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) {

    companion object {
        // Default Victoria Island, Lagos coordinates (used when running without physical GPS or before permission grant)
        val DEFAULT_COORDINATES = UserCoordinates(
            latitude = 6.4281,
            longitude = 3.4219,
            accuracy = 10f
        )
        private const val MAX_RADAR_RADIUS_KM = 5.0f
    }

    /**
     * Checks if the app has been granted FINE or COARSE location runtime permissions.
     */
    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    /**
     * Retrieves the last known location via FusedLocationProviderClient,
     * falling back to default coordinates if unavailable or permission is denied.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): UserCoordinates = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(DEFAULT_COORDINATES)
            return@suspendCancellableCoroutine
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        continuation.resume(
                            UserCoordinates(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy,
                                timestamp = location.time
                            )
                        )
                    } else {
                        continuation.resume(DEFAULT_COORDINATES)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(DEFAULT_COORDINATES)
                }
        } catch (e: Exception) {
            continuation.resume(DEFAULT_COORDINATES)
        }
    }

    /**
     * Continuously tracks user location updates as a Cold Flow using callbackFlow.
     * Emits fresh coordinates whenever device movement is detected.
     */
    @SuppressLint("MissingPermission")
    fun trackLocationUpdates(intervalMs: Long = 10000L): Flow<UserCoordinates> = callbackFlow {
        if (!hasLocationPermission()) {
            trySend(DEFAULT_COORDINATES)
            close()
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMs
        )
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setMinUpdateDistanceMeters(10f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(
                        UserCoordinates(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            accuracy = loc.accuracy,
                            timestamp = loc.time
                        )
                    )
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            trySend(DEFAULT_COORDINATES)
        }

        awaitClose {
            try {
                fusedLocationClient.removeLocationUpdates(callback)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Calculates precise distance between two coordinates in Kilometers
     * using the standard Haversine formulation.
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadiusKm * c).toFloat()
    }

    /**
     * Estimates arrival ETA in minutes based on real distance and average urban traffic velocity.
     */
    fun calculateEtaMinutes(distanceKm: Float, avgSpeedKmh: Float = 22f): Int {
        val hours = distanceKm / avgSpeedKmh
        val rawMinutes = hours * 60f
        // Add 3-5 minutes preparation / dispatch buffer
        return (rawMinutes + 4f).roundToInt().coerceAtLeast(3)
    }

    /**
     * On-Demand Matching Algorithm:
     * Takes the patient's coordinates and recalculates real distance, ETA, and
     * normalized radar coordinates (radarX, radarY from -1.0 to +1.0) for every provider,
     * then ranks providers closest-first.
     */
    fun matchAndRankProviders(
        userCoords: UserCoordinates,
        providers: List<HealthProvider>
    ): List<HealthProvider> {
        return providers.map { provider ->
            val distanceKm = calculateDistanceKm(
                userCoords.latitude,
                userCoords.longitude,
                provider.latitude,
                provider.longitude
            )
            val etaMinutes = calculateEtaMinutes(distanceKm)

            // Calculate normalized radar offset relative to patient's doorstep
            // 1 degree lat ≈ 111 km, 1 degree lon ≈ 111 km * cos(lat)
            val deltaLatKm = (provider.latitude - userCoords.latitude) * 111.0
            val deltaLonKm = (provider.longitude - userCoords.longitude) * 111.0 * cos(Math.toRadians(userCoords.latitude))

            val normX = (deltaLonKm / MAX_RADAR_RADIUS_KM).toFloat().coerceIn(-0.9f, 0.9f)
            val normY = (-deltaLatKm / MAX_RADAR_RADIUS_KM).toFloat().coerceIn(-0.9f, 0.9f) // invert Y for screen canvas

            provider.copy(
                distanceKm = (Math.round(distanceKm * 10.0) / 10.0).toFloat(),
                etaMinutes = etaMinutes,
                radarX = normX,
                radarY = normY
            )
        }.sortedWith(
            compareByDescending<HealthProvider> { it.isOnline }
                .thenBy { it.distanceKm }
                .thenByDescending { it.rating }
        )
    }
}
