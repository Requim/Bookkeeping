package com.jizhang.smartledger.domain.settings

import com.jizhang.smartledger.domain.model.Settings
import kotlinx.coroutines.flow.Flow

/** Persists privacy and capture settings for local-only bookkeeping. */
interface SettingsRepository {
    /** Emits settings whenever the user changes capture preferences. */
    val settings: Flow<Settings>

    /** Enables or disables experimental screen capture. */
    suspend fun setScreenCaptureEnabled(enabled: Boolean)

    /** Enables or disables the experimental accessibility reader. */
    suspend fun setAccessibilityCaptureEnabled(enabled: Boolean)
}

