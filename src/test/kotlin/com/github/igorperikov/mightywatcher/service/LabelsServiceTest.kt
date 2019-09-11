package com.github.igorperikov.mightywatcher.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LabelsServiceTest {
    @Test
    fun `labels should be lower case only`() {
        for (easyLabel in LabelsService.getEasyLabels()) {
            assertTrue(easyLabel == easyLabel.toLowerCase(), "label '$easyLabel' contains upper case letters")
        }
    }
}
