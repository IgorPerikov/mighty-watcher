package com.github.igorperikov.mightywatcher.entity

interface ResultLine

data class IssueLine(val repoName: String, val title: String, val htmlUrl: String) : ResultLine {
    override fun toString(): String {
        return "$repoName  \"$title\"  $htmlUrl"
    }
}

data class TimestampLine(val timestamp: NamedTimestamp) : ResultLine {
    override fun toString(): String {
        return "${timestamp.name.toUpperCase()}:"
    }
}