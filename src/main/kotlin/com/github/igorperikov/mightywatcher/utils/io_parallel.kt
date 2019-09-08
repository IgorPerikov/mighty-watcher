package com.github.igorperikov.mightywatcher.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore

private val coroutineScope = CoroutineScope(Dispatchers.IO)
private val parallelismLimiter = Semaphore(15)

fun <Input, Result> executeInParallel(inputs: List<Input>, function: Function1<Input, Result>): List<Result> =
    runBlocking {
        val listOfDeferredResults = ArrayList<Deferred<Result>>()
        for (input in inputs) {
            listOfDeferredResults += coroutineScope.async {
                execWithSemaphore(input, function)
            }
        }
        listOfDeferredResults.awaitAll()
    }

private suspend fun <Input, Result> execWithSemaphore(input: Input, function: Function1<Input, Result>): Result {
    try {
        parallelismLimiter.acquire()
        return function(input)
    } finally {
        parallelismLimiter.release()
    }
}
