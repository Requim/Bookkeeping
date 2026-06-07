package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.settings.SettingsRepository
import com.jizhang.smartledger.domain.time.Clock
import kotlinx.coroutines.flow.first

/** Uploads watched payment notifications to FastAPI. */
class CaptureNotificationRemoteUseCase(
    private val remoteCaptureRaw: RemoteCaptureRawUseCase,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock
) {
    /** Captures watched notification text and sends it to the backend. */
    suspend operator fun invoke(
        appPackage: String,
        title: String?,
        text: String,
        postedAt: Long
    ): RemoteCaptureResult {
        if (!isWatched(appPackage) || isEmpty(title, text)) {
            return RemoteCaptureResult.Ignored
        }
        return remoteCaptureRaw(buildCapture(appPackage, title, text, postedAt))
    }

    private suspend fun isWatched(appPackage: String): Boolean {
        val settings = settingsRepository.settings.first()
        return settings.notificationCaptureEnabled && appPackage in settings.watchedPackages
    }

    private fun isEmpty(title: String?, text: String): Boolean {
        return title.isNullOrBlank() && text.isBlank()
    }

    private fun buildCapture(
        appPackage: String,
        title: String?,
        text: String,
        postedAt: Long
    ): RawCapture {
        return RawCapture(
            source = CaptureSource.NOTIFICATION,
            appPackage = appPackage,
            title = title,
            text = listOfNotNull(title, text).joinToString("\n"),
            capturedAt = postedAt.takeIf { it > 0 } ?: clock.nowMillis()
        )
    }
}
