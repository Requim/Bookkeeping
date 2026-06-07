package com.jizhang.smartledger.domain.model

/** User preferences that control capture and privacy-sensitive experimental features. */
data class Settings(
    val notificationCaptureEnabled: Boolean = true,
    val imageOcrEnabled: Boolean = true,
    val screenCaptureEnabled: Boolean = false,
    val accessibilityCaptureEnabled: Boolean = false,
    val watchedPackages: Set<String> = defaultWatchedPaymentPackages()
)

/** Returns the first-party payment packages watched by default in the MVP. */
fun defaultWatchedPaymentPackages(): Set<String> {
    return setOf(
        "com.tencent.mm",
        "com.eg.android.AlipayGphone",
        "com.unionpay",
        "com.cmbchina.ccd.pluto.cmbActivity"
    )
}

