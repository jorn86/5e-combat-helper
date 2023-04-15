package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.norr.listNorrFiles
import org.hertsig.dnd.norr.readJsonAsMap
import org.hertsig.magic.magicMap
import kotlin.io.path.name

fun getFromBestiary(name: String) = index[name.lowercase()]
fun getFromBestiary(name: String, book: String) = data[book]?.monster().orEmpty()
    .firstOrNull { it.name().equals(name, ignoreCase = true) }

private val data by lazy { load() }
private val index by lazy { data.values.flatMap { it.monster() }.associateBy { it.name().lowercase() } }

private fun load(): Map<String, Bestiary> {
    return listNorrFiles("bestiary", "bestiary-*.json").associate {
        val data = readJsonAsMap(it)
        val name = it.fileName.name
        name.substring(9, name.length - 5) to magicMap(data)
    }
}
