package com.github.igorperikov.mightywatcher.entity

import java.time.Instant

class NamedTimestamp(val time: Instant, val name: String = "") : Comparable<NamedTimestamp> {
    override fun compareTo(other: NamedTimestamp): Int {
        return time.compareTo(other.time)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other is NamedTimestamp) {
            name == other.name
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "${name.toUpperCase()}:"
    }
}
