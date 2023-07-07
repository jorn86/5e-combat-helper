package org.hertsig.dnd.norr

import org.hertsig.dnd.combat.service.mapper
import org.hertsig.logger.logger
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.listDirectoryEntries

private val log = logger {}

private val folder = getNorrFolder()

fun getNorrFolder(): Path? {
    val folder = System.getProperty("norrFolder")
    if (folder == null) {
        log.warn("Norr folder not configured")
        return null
    }
    return try {
        val path = Path(folder).toRealPath()
        log.info("Loading Norr data from $path")
        path
    } catch (e: IOException) {
        log.error("Invalid Norr path configured", e)
        null
    }
}

fun listNorrFiles(subfolder: String, glob: String): List<Path> {
    val path = folder?.resolve(subfolder) ?: return emptyList()
    return path.listDirectoryEntries(glob)
}

@Suppress("UNCHECKED_CAST")
internal fun readJsonAsMap(path: Path): Map<String, Any> {
    val data: Map<String, Any> = path.bufferedReader().use { reader ->
        mapper.readValue(reader, Map::class.java) as Map<String, Any>
    }
    return data
}
