package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class InputParameters(
    @JsonProperty("username") val username: String,
    @JsonProperty("languages") val languages: Set<String>,
    @JsonProperty("labels") val labels: Set<String>,
    @JsonProperty("ignored-repos") val ignoredRepos: Set<String>,
    @JsonProperty("ignored-issues") val ignoredIssues: Set<String>
)
