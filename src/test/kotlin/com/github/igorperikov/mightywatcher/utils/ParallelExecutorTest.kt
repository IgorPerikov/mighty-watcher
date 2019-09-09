package com.github.igorperikov.mightywatcher.utils

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParallelExecutorTest {
    @Test
    fun everyInputIsExecuted() {
        val size = 10
        val completeFlags = BooleanArray(size) { false }
        val inputs = IntArray(size) { index -> index }.toList()
        ParallelExecutor().execute(inputs) { index -> completeFlags[index] = true }
        for (completeFlag in completeFlags) {
            assertTrue(completeFlag)
        }
    }
}
