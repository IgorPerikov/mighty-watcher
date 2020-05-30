package com.github.igorperikov.mightywatcher.service

class EasyLabelsStorage {
    // Keep it in lower case only and alphabetically sorted (first letter)
    private val easyLabels = LinkedHashSet(
        listOf(
            "accepting prs",
            "adoptme",
            "available",
            "backlog-easy",
            "beginner",
            "beginner friendly",
            "beginner-friendly",
            "complexity/easy",
            "contribution",
            "contribution welcome",
            "contributions welcome",
            "difficulty:easy",
            "easy",
            "easy fix",
            "easy to fix",
            "easy win",
            "easy-fix",
            "effort-low",
            "effort/easy",
            "e-easy",
            "e-help-wanted",
            "e-mentor",
            "e-medium",
            "e-needstest",
            "first contribution",
            "first-contribution",
            "good as first pr",
            "good first bug",
            "good first bugs",
            "good-first-contribution",
            "good first issue",
            "good first task",
            "good-first-issue",
            "good for beginners",
            "hacktoberfest",
            "help needed",
            "help wanted",
            "help wanted :octocat:",
            "help-wanted",
            "help wanted (easy)",
            "ideal for contribution",
            "jump in",
            "jump-in",
            "junior job",
            "junior jobs",
            "level:starter",
            "low hanging fruit",
            "low-hanging fruit",
            "low-hanging-fruit",
            "low_hanging_fruit",
            "meta: good first issue",
            "needhelp",
            "newbie",
            "newbie friendly",
            "newbie-friendly",
            "noob friendly",
            "please contribute",
            "pr welcome",
            "pr-welcome",
            "status: first-timers-only",
            "status: ideal-for-contribution",
            "trivial",
            "up for grabs",
            "up-for-grabs"
        )
    )

    /**
     * Return labels appropriate for contributions, all in lower case
     */
    fun getEasyLabels(): Set<String> {
        return easyLabels
    }
}
