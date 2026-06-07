package com.jizhang.smartledger.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.Settings
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.repository.CategoryRepository
import com.jizhang.smartledger.domain.repository.LedgerReadRepository
import com.jizhang.smartledger.domain.settings.SettingsRepository
import com.jizhang.smartledger.domain.usecase.CaptureImageRemoteUseCase
import com.jizhang.smartledger.domain.usecase.CaptureManualTextRemoteUseCase
import com.jizhang.smartledger.domain.usecase.ObserveRemoteDashboardUseCase
import com.jizhang.smartledger.domain.usecase.RemoteConfirmDraftUseCase
import com.jizhang.smartledger.domain.usecase.RemoteIgnoreDraftUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ViewModel that exposes app state and user actions to Compose screens. */
class AppViewModel(
    observeDashboard: ObserveRemoteDashboardUseCase,
    private val categoryRepository: CategoryRepository,
    private val ledgerReadRepository: LedgerReadRepository,
    private val settingsRepository: SettingsRepository,
    private val confirmDraft: RemoteConfirmDraftUseCase,
    private val ignoreDraft: RemoteIgnoreDraftUseCase,
    private val captureImageExpense: CaptureImageRemoteUseCase,
    private val captureManualText: CaptureManualTextRemoteUseCase
) : ViewModel() {
    private val message = MutableStateFlow<String?>(null)

    /** Combined UI state rendered by SmartLedgerApp. */
    val uiState: StateFlow<AppUiState> = combine(
        observeDashboard(),
        categoryRepository.observeCategories(),
        settingsRepository.settings,
        message
    ) { dashboard, categories, settings, currentMessage ->
        AppUiState(
            todayExpense = Money(dashboard.todayExpenseCents),
            pendingDrafts = dashboard.pendingDrafts,
            recentTransactions = dashboard.recentTransactions,
            categoryOptions = categories.map { CategoryOption(it.id, it.name) },
            settings = settings,
            message = currentMessage
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    init {
        refresh()
    }

    /** Refreshes dashboard state from the FastAPI backend. */
    fun refresh() {
        viewModelScope.launch {
            runAction { ledgerReadRepository.refresh() }
        }
    }

    /** Confirms a draft using its current parsed values. */
    fun confirm(draft: TransactionDraft) {
        viewModelScope.launch {
            runAction {
                val categoryId = draft.categoryId ?: defaultCategoryId()
                val input = draft.toConfirmedInput(categoryId)
                confirmDraft(draft.id, input)
                message.value = "已入账：${draft.merchant}"
            }
        }
    }

    /** Confirms a draft with user-edited values from the review dialog. */
    fun confirm(draftId: Long, input: ConfirmedDraftInput) {
        viewModelScope.launch {
            runAction {
                confirmDraft(draftId, input)
                message.value = "已入账：${input.merchant}"
            }
        }
    }

    /** Ignores a parsed draft so it no longer appears in pending review. */
    fun ignore(draftId: Long) {
        viewModelScope.launch {
            runAction {
                ignoreDraft(draftId)
                message.value = "已忽略候选账单"
            }
        }
    }

    /** Imports a selected screenshot through OCR and parsing. */
    fun importImage(uri: String) {
        viewModelScope.launch {
            runAction {
                val result = captureImageExpense(uri)
                message.value = "截图识别结果：$result"
            }
        }
    }

    /** Parses manually entered text through the normal capture pipeline. */
    fun submitManualText(text: String) {
        viewModelScope.launch {
            runAction {
                val result = captureManualText(text)
                message.value = "文本识别结果：$result"
            }
        }
    }

    /** Enables or disables experimental MediaProjection screen capture setting. */
    fun setScreenCaptureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setScreenCaptureEnabled(enabled)
        }
    }

    /** Enables or disables experimental accessibility setting. */
    fun setAccessibilityEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAccessibilityCaptureEnabled(enabled)
        }
    }

    /** Clears the current transient message. */
    fun clearMessage() {
        message.value = null
    }

    private suspend fun defaultCategoryId(): Long {
        categoryRepository.ensureDefaults()
        val categories = categoryRepository.categories()
        val fallback = categories.firstOrNull { it.name == "其他" } ?: categories.firstOrNull()
        return requireNotNull(fallback).id
    }

    private suspend fun runAction(block: suspend () -> Unit) {
        runCatching { block() }
            .onFailure { message.value = "操作失败：${it.message ?: "未知错误"}" }
    }

    private fun TransactionDraft.toConfirmedInput(categoryId: Long): ConfirmedDraftInput {
        return ConfirmedDraftInput(
            amountCents = money.amountCents,
            type = type,
            merchant = merchant,
            categoryId = categoryId,
            paidAt = paidAt
        )
    }
}

/** Immutable UI state for the main app screen. */
data class AppUiState(
    val todayExpense: Money = Money(0),
    val pendingDrafts: List<TransactionDraft> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val categoryOptions: List<CategoryOption> = emptyList(),
    val settings: Settings = Settings(),
    val message: String? = null
)

/** Lightweight category option for UI selection and labels. */
data class CategoryOption(
    val id: Long,
    val name: String
)

private fun TransactionType.displayName(): String {
    return when (this) {
        TransactionType.EXPENSE -> "支出"
        TransactionType.INCOME -> "收入"
        TransactionType.TRANSFER -> "转账"
        TransactionType.REFUND -> "退款"
    }
}

/** Returns the Chinese label for a transaction type. */
fun TransactionType.label(): String {
    return displayName()
}
