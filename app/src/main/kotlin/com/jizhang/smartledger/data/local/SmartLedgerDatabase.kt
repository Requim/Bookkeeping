package com.jizhang.smartledger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jizhang.smartledger.data.local.dao.CaptureDao
import com.jizhang.smartledger.data.local.dao.CategoryDao
import com.jizhang.smartledger.data.local.dao.DraftDao
import com.jizhang.smartledger.data.local.dao.TransactionDao
import com.jizhang.smartledger.data.local.entity.CategoryEntity
import com.jizhang.smartledger.data.local.entity.CategoryRuleEntity
import com.jizhang.smartledger.data.local.entity.RawCaptureEntity
import com.jizhang.smartledger.data.local.entity.TransactionDraftEntity
import com.jizhang.smartledger.data.local.entity.TransactionEntity

/** Local Room database for SmartLedger's private on-device data. */
@Database(
    entities = [
        RawCaptureEntity::class,
        TransactionDraftEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        CategoryRuleEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SmartLedgerDatabase : RoomDatabase() {
    /** Returns DAO for raw capture evidence. */
    abstract fun captureDao(): CaptureDao

    /** Returns DAO for transaction candidates. */
    abstract fun draftDao(): DraftDao

    /** Returns DAO for confirmed ledger records. */
    abstract fun transactionDao(): TransactionDao

    /** Returns DAO for categories and local classification rules. */
    abstract fun categoryDao(): CategoryDao
}

