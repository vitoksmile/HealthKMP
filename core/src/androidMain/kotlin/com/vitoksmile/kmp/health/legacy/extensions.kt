package com.vitoksmile.kmp.health.legacy

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun <T> Task<T>.await(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
): T {
    val task = this
    val executor = (coroutineContext[ContinuationInterceptor] as? CoroutineDispatcher)
        ?.asExecutor() ?: Dispatchers.Unconfined.asExecutor()

    return suspendCancellableCoroutine { continuation ->
        task
            .addOnSuccessListener(executor) { value ->
                if (!continuation.isActive) return@addOnSuccessListener

                continuation.resume(value)
            }
            .addOnFailureListener(executor) { error ->
                if (!continuation.isActive) return@addOnFailureListener

                continuation.resumeWithException(error)
            }
            .addOnCanceledListener(executor) {
                continuation.cancel()
            }
    }
}