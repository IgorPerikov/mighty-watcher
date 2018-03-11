package com.github.igorperikov.mighty.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Issue(
        @JsonProperty("html_url") val htmlUrl: String,
        val title: String
) {
    override fun toString(): String {
        return "${getRepoName()}  $title  $htmlUrl"
    }

    private fun getRepoName(): String {
        val split = htmlUrl.split("/")
        return split[split.size - 3]
    }
}
