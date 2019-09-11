package com.github.igorperikov.mightywatcher.service

class EasyLabelsStorage {
    private val easyLabels = hashSetOf(
        "accepting prs",
        "adoptme",
        "beginner",
        "beginner friendly",
        "contributions welcome",
        "difficulty:easy",
        "easy",
        "easy fix",
        "easy to fix",
        "effort-low",
        "e-easy",
        "e-help-wanted",
        "e-mentor",
        "e-medium",
        "e-needstest",
        "good as first pr",
        "good first bug",
        "good-first-contribution",
        "good first issue",
        "good-first-issue",
        "good for beginners",
        "hacktoberfest",
        "help wanted",
        "help-wanted",
        "help wanted (easy)",
        "ideal for contribution",
        "low hanging fruit",
        "meta: good first issue",
        "noob friendly",
        "pr welcome",
        "up-for-grabs"
    )

    fun getEasyLabels(): Set<String> {
        return easyLabels
    }
}
