package com.github.igorperikov.mightywatcher.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore

class ParallelExecutor(
    parallelismLevel: Int,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val parallelismLimiter = Semaphore(parallelismLevel)

    fun <Input, Result> execute(inputs: List<Input>, function: Function1<Input, Result>): List<Result> =
        runBlocking {
            inputs
                .map { input -> coroutineScope.async { execWithSemaphore(input, function) } }
                .awaitAll()
        }

    private suspend fun <Input, Result> execWithSemaphore(input: Input, function: Function1<Input, Result>): Result {
        try {
            parallelismLimiter.acquire()
            return function(input)
        } finally {
            parallelismLimiter.release()
        }
    }
}
