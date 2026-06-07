package com.jizhang.smartledger.platform.screen

import android.app.Service
import android.content.Intent
import android.os.IBinder

/** Placeholder foreground service for future opt-in MediaProjection capture. */
class ScreenCaptureForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf(startId)
        return START_NOT_STICKY
    }
}

