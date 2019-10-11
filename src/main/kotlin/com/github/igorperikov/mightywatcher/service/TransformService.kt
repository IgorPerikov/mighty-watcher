package com.github.igorperikov.mightywatcher.service

import com.github.igorperikov.mightywatcher.Issues
import com.github.igorperikov.mightywatcher.entity.IssueLine
import com.github.igorperikov.mightywatcher.entity.NamedTimestamp
import com.github.igorperikov.mightywatcher.entity.ResultLine
import com.github.igorperikov.mightywatcher.entity.TimestampLine

/**
 * Convert grouped issues into list of results that are convenient for further transformations
 */
class TransformService {
    fun transform(issues: LinkedHashMap<NamedTimestamp, Issues>): List<ResultLine> {
        val resultLines = mutableListOf<ResultLine>()
        for ((timeGroupName, issuesInTimeGroup) in issues) {
            if (issuesInTimeGroup.isEmpty()) continue
            resultLines.add(TimestampLine(timeGroupName))
            for (issue in issuesInTimeGroup) {
                resultLines.add(IssueLine(issue.getRepoName(), issue.title, issue.htmlUrl))
            }
        }
        return resultLines
    }
}
