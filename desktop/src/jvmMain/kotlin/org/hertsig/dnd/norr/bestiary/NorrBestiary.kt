package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.norr.listNorrFiles
import org.hertsig.dnd.norr.readJsonAsMap
import org.hertsig.magic.magicMap
import org.hertsig.util.sub
import kotlin.io.path.name

fun getFromBestiary(name: String) = index[name.lowercase()]
fun getFromBestiary(name: String, book: String) = data[book].orEmpty()
    .firstOrNull { it.name().equals(name, ignoreCase = true) }

@Suppress("UNCHECKED_CAST")
fun getAllFromBestiary(name: String) = data.mapValues { (_,it) -> it.singleOrNull { m -> m.name().equals(name, true) } }
    .filter { (_, v) -> v != null } as Map<String, Monster>

private val data by lazy { load() }
private val index by lazy { data.values.flatten().associateBy { it.name().lowercase() } }

private fun load(): Map<String, List<Monster>> {
    return listNorrFiles("data/bestiary", "bestiary-*.json").associate {
        val data = readJsonAsMap(it)
        val name = it.fileName.name
        name.sub(9, -5) to magicMap<Bestiary>(data).monster()
    }
}
