package org.hertsig.dnd.combat

lateinit var logEntries: MutableList<LogEntry>
fun log(entry: LogEntry) = logEntries.add(entry)
