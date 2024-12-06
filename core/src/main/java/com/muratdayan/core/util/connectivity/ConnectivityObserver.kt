package com.muratdayan.core.util.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected:Flow<Boolean>
}