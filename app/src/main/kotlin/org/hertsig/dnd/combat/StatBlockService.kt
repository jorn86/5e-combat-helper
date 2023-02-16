package org.hertsig.dnd.combat

import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.hertsig.dnd.combat.dto.StatBlock
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.*

private val log = KotlinLogging.logger {}

object StatBlockService {
    private val dataFolder = Path("./data").createDirectories()
    private val file = dataFolder.resolve("statblocks.json")

    suspend fun load(): List<StatBlock> = withContext(Dispatchers.IO) {
        if (!file.isRegularFile()) {
            log.debug { "No file, loading with empty list" }
            return@withContext listOf(StatBlock(""))
        }
        val statBlocks = file.bufferedReader().use { mapper.readValue(it, object : TypeReference<List<StatBlock>>() {}) }
        log.debug { "Loaded ${statBlocks.size} creatures" }
        log(LogEntry.Text("Loaded with ${statBlocks.size} creatures"))
        System.getProperty("backupFolder")?.let { backup(Path(it)) }
        statBlocks.ifEmpty { listOf(StatBlock("")) }
    }

    suspend fun save(statBlocks: List<StatBlock>) = withContext(Dispatchers.IO) {
        log.debug { "Saving ${statBlocks.size} creatures" }
        file.bufferedWriter().use { mapper
//            .writerWithDefaultPrettyPrinter()
            .writeValue(it, statBlocks)
        }
    }

    private fun backup(backupFolder: Path) {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val target = backupFolder.createDirectories().resolve("statblocks-$date.json")
        file.copyTo(target, overwrite = true)
        log.info { "Backed up stat block list to $target" }
    }
}
