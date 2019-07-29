package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    val language: String?,
    @JsonProperty("full_name") val fullName: String,
    @JsonProperty("has_issues") val hasIssues: Boolean
) {
    fun getOwner(): String {
        return fullName.split("/")[0]
    }

    fun getRepo(): String {
        return fullName.split("/")[1]
    }
}
