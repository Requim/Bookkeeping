package com.jizhang.smartledger.platform.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.jizhang.smartledger.SmartLedgerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Notification listener that uploads payment-looking notifications to FastAPI. */
class PaymentNotificationListenerService : NotificationListenerService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val text = NotificationTextExtractor.extract(sbn) ?: return
        serviceScope.launch {
            appContainer().captureNotificationRemote(
                appPackage = sbn.packageName,
                title = text.title,
                text = text.body,
                postedAt = sbn.postTime
            )
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun appContainer() = (application as SmartLedgerApplication).container
}
