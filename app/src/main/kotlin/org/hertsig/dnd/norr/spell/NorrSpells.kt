package org.hertsig.dnd.norr.spell

import org.hertsig.dnd.norr.listNorrFiles
import org.hertsig.dnd.norr.readJsonAsMap
import org.hertsig.magic.magicMap
import kotlin.io.path.name

fun getNorrSpell(name: String) = index[name.lowercase()]
fun getNorrSpell(name: String, book: String) = data[book]?.spell().orEmpty()
    .firstOrNull { it.name().equals(name, ignoreCase = true) }

private val data by lazy { load() }
private val index by lazy { data.values.flatMap { it.spell() }.associateBy { it.name().lowercase() } }

private fun load(): Map<String, Spells> {
    return listNorrFiles("spells", "spells-*.json").associate {
        val data: Map<String, Any> = readJsonAsMap(it)
        val name = it.fileName.name
        name.substring(7, name.length - 5) to magicMap(data)
    }
}
