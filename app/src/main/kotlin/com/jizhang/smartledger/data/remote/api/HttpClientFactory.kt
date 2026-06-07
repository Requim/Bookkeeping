package com.jizhang.smartledger.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Builds the Ktor HTTP client used by the thin Android client. */
object HttpClientFactory {
    /** Creates a JSON-configured Android HTTP client for FastAPI calls. */
    fun create(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
