package com.jizhang.smartledger.platform.notification

import android.app.Notification
import android.service.notification.StatusBarNotification

/** Extracts title and body text from Android notification extras. */
object NotificationTextExtractor {
    /** Returns normalized notification text or null when no useful text exists. */
    fun extract(sbn: StatusBarNotification): NotificationText? {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val body = listOfNotNull(text, bigText).distinct().joinToString("\n")
        if (title.isNullOrBlank() && body.isBlank()) {
            return null
        }
        return NotificationText(title = title, body = body)
    }
}

/** Normalized notification text passed into domain capture use cases. */
data class NotificationText(
    val title: String?,
    val body: String
)

