package com.github.igorperikov.mightywatcher.utils

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class ParallelExecutorTest {
    private val parallelismLevel = 5

    @Test
    fun `every input is executed`() {
        val size = 10
        val completeFlags = BooleanArray(size) { false }
        val inputs = IntArray(size) { index -> index }.toList()
        ParallelExecutor(parallelismLevel).execute(inputs) { index -> completeFlags[index] = true }
        for (completeFlag in completeFlags) {
            assertTrue(completeFlag)
        }
    }

    @Test
    fun `at the same moment no more than 'parallelismLevel' tasks is in work`() {
        val inputsSize = parallelismLevel * 2
        val countDownLatch = CountDownLatch(parallelismLevel)
        val executionFlags = BooleanArray(inputsSize) { false }
        val executor = ParallelExecutor(parallelismLevel = parallelismLevel)
        val inputs = IntArray(inputsSize) { index -> index }.toList()

        thread(isDaemon = true) {
            executor.execute(inputs) { taskNumber ->
                executionFlags[taskNumber] = true
                countDownLatch.countDown()
                simulateWork()
            }
        }

        countDownLatch.await()

        assertTrue(
            executionFlags.count { it == true } == parallelismLevel,
            "only $parallelismLevel tasks should've been launched at this point of time"
        )
    }

    private fun simulateWork() {
        sleep(3000)
    }
}
