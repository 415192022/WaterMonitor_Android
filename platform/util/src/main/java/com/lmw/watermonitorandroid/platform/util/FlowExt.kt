package com.lmw.watermonitorandroid.platform.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retry

fun <T> Flow<T>.retryWithBackoff(retries: Long = 3): Flow<T> = retry(retries)