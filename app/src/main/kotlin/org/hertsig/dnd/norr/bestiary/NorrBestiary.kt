package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.combat.service.mapper
import org.hertsig.magic.magicMap
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun getFromBestiary(name: String) = index[name.lowercase()]
fun getFromBestiary(name: String, book: String) = data[book]?.monster().orEmpty()
    .firstOrNull { it.name().equals(name, ignoreCase = true) }

private val data by lazy { load() }
private val index by lazy { data.values.flatMap { it.monster() }.associateBy { it.name().lowercase() } }

@Suppress("UNCHECKED_CAST")
private fun load(): Map<String, Bestiary> {
    val folder = System.getProperty("bestiaryFolder") ?: return emptyMap()
    return Path(folder).listDirectoryEntries("bestiary-*.json").associate {
        val data: Map<String, Any> = it.bufferedReader().use { reader ->
            mapper.readValue(reader, Map::class.java) as Map<String, Any>
        }
        val name = it.fileName.name
        name.substring(9, name.length - 5) to magicMap(data)
    }
}
