package org.hertsig.dnd.norr.spell

import org.hertsig.dnd.combat.service.mapper
import org.hertsig.magic.magicMap
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun getNorrSpell(name: String) = index[name.lowercase()]
fun getNorrSpell(name: String, book: String) = data[book]?.spell().orEmpty()
    .firstOrNull { it.name().equals(name, ignoreCase = true) }

private val data by lazy { load() }
private val index by lazy { data.values.flatMap { it.spell() }.associateBy { it.name().lowercase() } }

@Suppress("UNCHECKED_CAST")
private fun load(): Map<String, Spells> {
    val folder = System.getProperty("bestiaryFolder") ?: return emptyMap()
    return Path(folder).resolve("../spells").listDirectoryEntries("spells-*.json").associate {
        val data: Map<String, Any> = it.bufferedReader().use { reader ->
            mapper.readValue(reader, Map::class.java) as Map<String, Any>
        }
        val name = it.fileName.name
        name.substring(7, name.length - 5) to magicMap(data)
    }
}
