package com.github.igorperikov.mightywatcher.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class Issue(
    @JsonProperty("html_url", required = true) val htmlUrl: String,
    @JsonProperty("created_at", required = true) val createdAt: Instant,
    @JsonProperty("updated_at", required = true) val updatedAt: Instant,
    val title: String
) {
    override fun toString(): String {
        return "${getRepoName()}  \"$title\"  $htmlUrl"
    }

    fun getRepoName(): String {
        val split = htmlUrl.split("/")
        return split[split.size - 3]
    }
}
