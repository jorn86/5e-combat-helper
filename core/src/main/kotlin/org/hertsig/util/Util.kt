package org.hertsig.util

fun count(number: Int) = when {
    number < 0 -> error("Invalid argument $number")
    number == 1 -> "1st"
    number == 2 -> "2nd"
    number == 3 -> "3rd"
    else -> "${number}th"
}

fun plural(count: Int, singular: String) = if (count == 1) "1 $singular" else "$count ${singular}s"

fun String.sub(start: Int = 0, end: Int = length) = substring(start, if (end < 0) end + length else end)
