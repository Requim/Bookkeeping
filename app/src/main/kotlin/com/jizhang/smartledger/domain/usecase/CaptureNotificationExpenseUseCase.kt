package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.settings.SettingsRepository
import com.jizhang.smartledger.domain.time.Clock
import kotlinx.coroutines.flow.first

/** Stores payment notification text and converts it into a pending draft. */
class CaptureNotificationExpenseUseCase(
    private val captureRepository: CaptureRepository,
    private val processRawCapture: ProcessRawCaptureUseCase,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock
) {
    /** Captures a notification when its package is part of the watched payment set. */
    suspend operator fun invoke(
        appPackage: String,
        title: String?,
        text: String,
        postedAt: Long
    ): ProcessRawCaptureResult {
        if (!isWatched(appPackage) || isEmpty(title, text)) {
            return ProcessRawCaptureResult.Ignored
        }
        val capture = buildCapture(appPackage, title, text, postedAt)
        val id = captureRepository.save(capture)
        return processRawCapture(capture.copy(id = id))
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
