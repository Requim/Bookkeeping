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
                observeDashboard = container.observeDashboard,
                categoryRepository = container.categoryRepository,
                settingsRepository = container.settingsRepository,
                confirmDraft = container.confirmDraft,
                ignoreDraft = container.ignoreDraft,
                captureImageExpense = container.captureImageExpense,
                captureManualText = container.captureManualText
            ) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}
