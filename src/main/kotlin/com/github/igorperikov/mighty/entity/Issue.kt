package com.github.igorperikov.mighty.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class Issue(
    @JsonProperty("html_url") val htmlUrl: String,
    @JsonProperty("created_at") val createdAt: Instant,
    val title: String,
    val labels: Set<Label>
) {
    override fun toString(): String {
        return "${getRepoName()}  $title  ${labels.joinToString("; ", "[", "]") { it.name }}  $htmlUrl"
    }

    private fun getRepoName(): String {
        val split = htmlUrl.split("/")
        return split[split.size - 3]
    }
}
