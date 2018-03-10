package com.github.igorperikov.mighty

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RepoDefinition(
        val id: Int,
        val name: String,
        val url: String,
        val language: String?,
        @JsonProperty("full_name") val fullName: String,
        @JsonProperty("issues_url") val issuesUrl: String,
        @JsonProperty("has_issues") val hasIssues: Boolean
)
