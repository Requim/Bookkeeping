package com.jizhang.smartledger.data.remote.api

/** Backend connection defaults for local FastAPI development. */
object BackendConfig {
    /** Emulator-friendly URL for a FastAPI server running on the host machine. */
    const val DEFAULT_BASE_URL = "http://10.0.2.2:8000"
}
