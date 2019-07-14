package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class InputParameters(
    @JsonProperty("include-languages") val includedLanguages: Set<String>,
    @JsonProperty("exclude-languages") val excludedLanguages: Set<String>,
    @JsonProperty("exclude-repositories") val excludedRepositories: Set<String>
)
