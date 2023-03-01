package org.hertsig.dnd.combat

import org.hertsig.dnd.combat.dto.LogEntry

lateinit var logEntries: MutableList<LogEntry>
fun log(entry: LogEntry) = logEntries.add(entry)
