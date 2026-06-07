package com.jizhang.smartledger.di

import android.content.Context
import androidx.room.Room
import com.jizhang.smartledger.data.classifier.RuleBasedExpenseClassifier
import com.jizhang.smartledger.data.duplicate.ShaDuplicateDetector
import com.jizhang.smartledger.data.local.SmartLedgerDatabase
import com.jizhang.smartledger.data.parser.ChinesePaymentTextParser
import com.jizhang.smartledger.data.repository.DataStoreSettingsRepository
import com.jizhang.smartledger.data.repository.RoomCaptureRepository
import com.jizhang.smartledger.data.repository.RoomCategoryRepository
import com.jizhang.smartledger.data.repository.RoomDraftRepository
import com.jizhang.smartledger.data.repository.RoomTransactionRepository
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.repository.CategoryRepository
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.repository.TransactionRepository
import com.jizhang.smartledger.domain.settings.SettingsRepository
import com.jizhang.smartledger.domain.usecase.CaptureImageExpenseUseCase
import com.jizhang.smartledger.domain.usecase.CaptureManualTextUseCase
import com.jizhang.smartledger.domain.usecase.CaptureNotificationExpenseUseCase
import com.jizhang.smartledger.domain.usecase.ConfirmDraftUseCase
import com.jizhang.smartledger.domain.usecase.IgnoreDraftUseCase
import com.jizhang.smartledger.domain.usecase.ObserveDashboardUseCase
import com.jizhang.smartledger.domain.usecase.ProcessRawCaptureUseCase
import com.jizhang.smartledger.platform.ocr.MlKitChineseOcrEngine
import com.jizhang.smartledger.platform.time.SystemClock

/** Manual dependency graph that wires platform, data, domain, and presentation layers. */
class AppContainer(
    context: Context
) {
    private val appContext = context.applicationContext
    private val database = Room.databaseBuilder(
        appContext,
        SmartLedgerDatabase::class.java,
        "smart-ledger.db"
    ).build()

    private val clock = SystemClock()
    private val parser = ChinesePaymentTextParser()
    private val duplicateDetector = ShaDuplicateDetector()

    /** Repository for raw capture evidence. */
    val captureRepository: CaptureRepository = RoomCaptureRepository(database.captureDao())

    /** Repository for parsed transaction candidates. */
    val draftRepository: DraftRepository = RoomDraftRepository(database.draftDao())

    /** Repository for confirmed transactions. */
    val transactionRepository: TransactionRepository =
        RoomTransactionRepository(database.transactionDao())

    /** Repository for categories and local classification rules. */
    val categoryRepository: CategoryRepository = RoomCategoryRepository(database.categoryDao())

    /** Repository for capture and privacy settings. */
    val settingsRepository: SettingsRepository = DataStoreSettingsRepository(appContext)

    private val classifier = RuleBasedExpenseClassifier(categoryRepository)
    private val ocrEngine = MlKitChineseOcrEngine(appContext)

    /** Use case for processing raw captured evidence into pending drafts. */
    val processRawCapture = ProcessRawCaptureUseCase(
        parser = parser,
        classifier = classifier,
        duplicateDetector = duplicateDetector,
        draftRepository = draftRepository,
        captureRepository = captureRepository,
        clock = clock
    )

    /** Use case for capturing supported payment notifications. */
    val captureNotificationExpense = CaptureNotificationExpenseUseCase(
        captureRepository = captureRepository,
        processRawCapture = processRawCapture,
        settingsRepository = settingsRepository,
        clock = clock
    )

    /** Use case for recognizing a screenshot and creating a pending draft. */
    val captureImageExpense = CaptureImageExpenseUseCase(
        ocrEngine = ocrEngine,
        captureRepository = captureRepository,
        processRawCapture = processRawCapture,
        clock = clock
    )

    /** Use case for manually entered payment text. */
    val captureManualText = CaptureManualTextUseCase(
        captureRepository = captureRepository,
        processRawCapture = processRawCapture,
        clock = clock
    )

    /** Use case for dashboard read state. */
    val observeDashboard = ObserveDashboardUseCase(
        draftRepository = draftRepository,
        transactionRepository = transactionRepository,
        clock = clock
    )

    /** Use case for confirming pending drafts into ledger records. */
    val confirmDraft = ConfirmDraftUseCase(
        draftRepository = draftRepository,
        transactionRepository = transactionRepository,
        clock = clock
    )

    /** Use case for ignoring pending drafts. */
    val ignoreDraft = IgnoreDraftUseCase(draftRepository)
}
