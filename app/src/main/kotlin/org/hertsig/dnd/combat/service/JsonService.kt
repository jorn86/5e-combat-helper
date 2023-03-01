package org.hertsig.dnd.combat.service

import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hertsig.core.debug
import org.hertsig.core.info
import org.hertsig.core.logger
import org.hertsig.dnd.combat.dto.LogEntry
import org.hertsig.dnd.combat.log
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.*

private val log = logger {}

class JsonService<T: Any>(private val filename: String, private val type: TypeReference<List<T>>) {
    companion object {
        private val dataFolder = Path("./data").createDirectories()
        private val backupFolder = System.getProperty("backupFolder")?.let(::Path)
    }

    private val file = dataFolder.resolve("$filename.json")

    suspend fun load(): List<T> = withContext(Dispatchers.IO) {
        if (!file.isRegularFile()) {
            log.debug("No $filename file, loading with empty list")
            return@withContext emptyList()
        }
        val data = file.bufferedReader().use { mapper.readValue(it, type) }
        log.debug { "Loaded ${data.size} $filename" }
        log(LogEntry.Text("Loaded ${data.size} $filename"))
        backupFolder?.let(::backup)
        data
    }

    suspend fun save(data: List<T>) = withContext(Dispatchers.IO) {
        log.debug { "Saving ${data.size} $filename" }
        file.bufferedWriter().use { mapper
//            .writerWithDefaultPrettyPrinter()
            .writeValue(it, data)
        }
    }

    private fun backup(backupFolder: Path) {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val target = backupFolder.createDirectories().resolve("$filename-$date.json")
        file.copyTo(target, overwrite = true)
        log.info { "Backed up $filename data to $target" }
    }
}
