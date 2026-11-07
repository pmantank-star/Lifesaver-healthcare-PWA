package com.example.notification

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.model.Booking
import com.example.model.NotificationType
import com.example.model.LifeSaversNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LifeSaversNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_DISPATCH_PROVIDER = "lifesavers_dispatch_provider_channel"
        const val CHANNEL_PATIENT_UPDATES = "lifesavers_patient_channel"

        const val NOTIFICATION_ID_PROVIDER_DISPATCH = 1001
        const val NOTIFICATION_ID_PATIENT_ACCEPTANCE = 1002
        const val NOTIFICATION_ID_PATIENT_ARRIVAL = 1003
        const val NOTIFICATION_ID_PATIENT_COMPLETED = 1004

        const val EXTRA_BOOKING_ID = "extra_booking_id"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // In-app heads-up notification flow for real-time visual feedback in UI
    private val _inAppNotification = MutableStateFlow<LifeSaversNotification?>(null)
    val inAppNotification: StateFlow<LifeSaversNotification?> = _inAppNotification.asStateFlow()

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 1. Channel for Healthcare Providers (Urgent Dispatch Gigs)
            val providerChannel = NotificationChannel(
                CHANNEL_DISPATCH_PROVIDER,
                "LifeSavers Provider Dispatch Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts for incoming patient home care dispatches within radius."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
            }

            // 2. Channel for Patients (Booking Accepted, Arrival, Status)
            val patientChannel = NotificationChannel(
                CHANNEL_PATIENT_UPDATES,
                "LifeSavers Patient Care Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Updates regarding your dispatched health worker, ETA, and care completion."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250)
            }

            notificationManager.createNotificationChannel(providerChannel)
            notificationManager.createNotificationChannel(patientChannel)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Alert nearby providers within a radius that a new patient has requested a service.
     */
    @SuppressLint("MissingPermission")
    fun notifyNearbyProvidersOfRequest(
        booking: Booking,
        radiusKm: Float,
        notifiedProvidersCount: Int
    ) {
        val title = "⚡ Urgent Care Request (~${radiusKm}km radius)"
        val payoutNet = (booking.baseCost * 0.85).toInt()
        val content = "New request for ${booking.serviceName} at ${booking.patientAddress}. Net payout: ₦${"%,d".format(payoutNet)}."

        // Post system notification if permission is available
        if (hasNotificationPermission()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_BOOKING_ID, booking.id)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                booking.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_DISPATCH_PROVIDER)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$content\n\nAlerted $notifiedProvidersCount nearby verified providers. Tap to review and accept gig.")
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            try {
                NotificationManagerCompat.from(context)
                    .notify(NOTIFICATION_ID_PROVIDER_DISPATCH, builder.build())
            } catch (_: SecurityException) {
            }
        }

        // Post in-app heads-up notification for instantaneous feedback
        _inAppNotification.value = LifeSaversNotification(
            id = "notif-prov-${System.currentTimeMillis()}",
            title = title,
            message = content,
            type = NotificationType.PROVIDER_DISPATCH_ALERT,
            bookingId = booking.id,
            etaMinutes = booking.etaMinutes
        )
    }

    /**
     * Notify patient that a provider has accepted their request and is en route with ETA.
     */
    @SuppressLint("MissingPermission")
    fun notifyPatientOfAcceptance(booking: Booking) {
        val title = "✅ Care Request Accepted!"
        val providerName = booking.providerName ?: "A verified provider"
        val content = "$providerName has accepted your dispatch and is en route. Estimated arrival: ~${booking.etaMinutes} mins."

        if (hasNotificationPermission()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_BOOKING_ID, booking.id)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                booking.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_PATIENT_UPDATES)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$content\n\nDoorstep Address: ${booking.patientAddress}\nTap to view live arrival tracker & in-app chat.")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            try {
                NotificationManagerCompat.from(context)
                    .notify(NOTIFICATION_ID_PATIENT_ACCEPTANCE, builder.build())
            } catch (_: SecurityException) {
            }
        }

        // In-app alert
        _inAppNotification.value = LifeSaversNotification(
            id = "notif-pat-${System.currentTimeMillis()}",
            title = title,
            message = content,
            type = NotificationType.PATIENT_ACCEPTED_ALERT,
            bookingId = booking.id,
            etaMinutes = booking.etaMinutes
        )
    }

    /**
     * Notify patient that the provider has arrived at their doorstep.
     */
    @SuppressLint("MissingPermission")
    fun notifyPatientOfArrival(booking: Booking) {
        val title = "🚪 Health Worker Arrived at Doorstep"
        val content = "${booking.providerName ?: "Your provider"} is outside ready to enter for ${booking.serviceName}."

        if (hasNotificationPermission()) {
            val builder = NotificationCompat.Builder(context, CHANNEL_PATIENT_UPDATES)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            try {
                NotificationManagerCompat.from(context)
                    .notify(NOTIFICATION_ID_PATIENT_ARRIVAL, builder.build())
            } catch (_: SecurityException) {
            }
        }

        _inAppNotification.value = LifeSaversNotification(
            id = "notif-arr-${System.currentTimeMillis()}",
            title = title,
            message = content,
            type = NotificationType.PATIENT_ARRIVED_ALERT,
            bookingId = booking.id
        )
    }

    /**
     * Notify patient that the care visit is completed.
     */
    @SuppressLint("MissingPermission")
    fun notifyPatientOfCompletion(booking: Booking) {
        val title = "🩺 Home Care Visit Completed"
        val content = "Care procedure for ${booking.serviceName} finished. Total ₦${"%,d".format(booking.totalCost)}."

        if (hasNotificationPermission()) {
            val builder = NotificationCompat.Builder(context, CHANNEL_PATIENT_UPDATES)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            try {
                NotificationManagerCompat.from(context)
                    .notify(NOTIFICATION_ID_PATIENT_COMPLETED, builder.build())
            } catch (_: SecurityException) {
            }
        }

        _inAppNotification.value = LifeSaversNotification(
            id = "notif-comp-${System.currentTimeMillis()}",
            title = title,
            message = content,
            type = NotificationType.PATIENT_COMPLETED_ALERT,
            bookingId = booking.id
        )
    }

    /**
     * Notify healthcare provider of administrator credential verification result.
     */
    @SuppressLint("MissingPermission")
    fun notifyProviderOfCredentialResult(
        providerName: String,
        isApproved: Boolean,
        adminNotes: String
    ) {
        val title = if (isApproved) "🎉 Credentials Verified & Approved" else "⚠️ Credential Review Update"
        val content = if (isApproved) {
            "Dr./Nurse $providerName: Your practicing license has been verified by the LifeSavers Medical Board. You are now authorized for home dispatches!"
        } else {
            "Dr./Nurse $providerName: Your credentials require attention. $adminNotes"
        }

        if (hasNotificationPermission()) {
            val builder = NotificationCompat.Builder(context, CHANNEL_DISPATCH_PROVIDER)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$content\n\nAdmin Sign-Off: $adminNotes"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            try {
                NotificationManagerCompat.from(context)
                    .notify(1005, builder.build())
            } catch (_: SecurityException) {
            }
        }

        _inAppNotification.value = LifeSaversNotification(
            id = "notif-cred-${System.currentTimeMillis()}",
            title = title,
            message = content,
            type = if (isApproved) NotificationType.PROVIDER_CREDENTIAL_VERIFIED else NotificationType.PROVIDER_CREDENTIAL_REJECTED
        )
    }

    fun dismissInAppNotification() {
        _inAppNotification.value = null
    }
}

typealias OmaNotificationManager = LifeSaversNotificationManager
