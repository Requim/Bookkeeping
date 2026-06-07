package com.jizhang.smartledger.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jizhang.smartledger.di.AppContainer

/** Factory that creates presentation ViewModels from the manual app container. */
class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                observeDashboard = container.observeRemoteDashboard,
                categoryRepository = container.categoryRepository,
                ledgerReadRepository = container.ledgerReadRepository,
                settingsRepository = container.settingsRepository,
                confirmDraft = container.confirmDraftRemote,
                ignoreDraft = container.ignoreDraftRemote,
                captureImageExpense = container.captureImageRemote,
                captureManualText = container.captureManualTextRemote
            ) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}
