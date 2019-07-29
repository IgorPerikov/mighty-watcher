package com.github.igorperikov.mightywatcher.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore

val coroutineScope = CoroutineScope(Dispatchers.IO)
val parallelismLimiter = Semaphore(15)

fun <Input, Result> launchInParallel(inputs: List<Input>, function: Function1<Input, Result>): List<Result> =
    runBlocking {
        val listOfDeferredResults = ArrayList<Deferred<Result>>()
        for (input in inputs) {
            listOfDeferredResults += coroutineScope.async {
                try {
                    parallelismLimiter.acquire()
                    return@async function(input)
                } finally {
                    parallelismLimiter.release()
                }
            }
        }
        listOfDeferredResults.awaitAll()
    }
