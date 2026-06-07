package com.jizhang.smartledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jizhang.smartledger.presentation.SmartLedgerApp

/** Main Android activity hosting the Compose application. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as SmartLedgerApplication).container
        setContent {
            SmartLedgerApp(container)
        }
    }
}

