package com.jizhang.smartledger

import android.app.Application
import com.jizhang.smartledger.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Application entry that owns the manual dependency container. */
class SmartLedgerApplication : Application() {
    /** App-wide dependency graph for platform services and UI factories. */
    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        seedDefaults()
    }

    private fun seedDefaults() {
        appScope.launch {
            container.categoryRepository.ensureDefaults()
        }
    }
}

