package com.jizhang.smartledger.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/** Opt-in placeholder for future payment screen text capture experiments. */
class ExperimentalLedgerAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intentionally empty until the user explicitly enables and validates this experiment.
    }

    override fun onInterrupt() = Unit
}

