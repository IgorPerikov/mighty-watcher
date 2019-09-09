package com.github.igorperikov.mightywatcher.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore

class ParallelExecutor(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    parallelismLevel: Int = 15
) {
    private val parallelismLimiter = Semaphore(parallelismLevel)

    fun <Input, Result> execute(inputs: List<Input>, function: Function1<Input, Result>): List<Result> =
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
}
