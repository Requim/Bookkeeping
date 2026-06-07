package com.jizhang.smartledger.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.jizhang.smartledger.domain.model.Settings
import com.jizhang.smartledger.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.smartLedgerDataStore by preferencesDataStore(name = "smart_ledger_settings")

/** DataStore-backed implementation for local privacy and capture settings. */
class DataStoreSettingsRepository(
    context: Context
) : SettingsRepository {
    private val dataStore = context.smartLedgerDataStore

    override val settings: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            screenCaptureEnabled = preferences[SCREEN_CAPTURE_ENABLED] ?: false,
            accessibilityCaptureEnabled = preferences[ACCESSIBILITY_ENABLED] ?: false
        )
    }

    override suspend fun setScreenCaptureEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[SCREEN_CAPTURE_ENABLED] = enabled }
    }

    override suspend fun setAccessibilityCaptureEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[ACCESSIBILITY_ENABLED] = enabled }
    }

    private companion object {
        val SCREEN_CAPTURE_ENABLED = booleanPreferencesKey("screen_capture_enabled")
        val ACCESSIBILITY_ENABLED = booleanPreferencesKey("accessibility_capture_enabled")
    }
}

