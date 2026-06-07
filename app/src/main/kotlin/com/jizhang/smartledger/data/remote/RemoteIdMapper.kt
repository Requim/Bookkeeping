package com.jizhang.smartledger.data.remote

import kotlin.math.absoluteValue

/** Keeps backend string ids addressable by existing Android Long-based UI models. */
class RemoteIdMapper {
    private val remoteByLocal = mutableMapOf<Long, String>()

    /** Converts a backend id into a stable local id and remembers the mapping. */
    fun localId(remoteId: String): Long {
        val localId = remoteId.hashCode().toLong().absoluteValue
        remoteByLocal[localId] = remoteId
        return localId
    }

    /** Returns the backend id for a local id or falls back to the local id string. */
    fun remoteId(localId: Long): String {
        return remoteByLocal[localId] ?: localId.toString()
    }
}
