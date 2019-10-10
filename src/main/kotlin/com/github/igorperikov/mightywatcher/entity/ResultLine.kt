package com.github.igorperikov.mightywatcher.entity

interface ResultLine

data class IssueLine(val repoName: String, val title: String, val htmlUrl: String) : ResultLine {

}
data class TimestampLine(val name: NamedTimestamp) : ResultLine