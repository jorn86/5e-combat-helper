package org.hertsig.dnd.norr.spell

import org.hertsig.dnd.combat.dto.Spell
import org.hertsig.dnd.combat.dto.Stat
import org.hertsig.dnd.combat.dto.shortDisplay
import org.hertsig.dnd.norr.listNorrFiles
import org.hertsig.dnd.norr.readJsonAsMap
import org.hertsig.magic.magicMap
import org.hertsig.util.sub
import kotlin.io.path.name

fun getNorrSpell(name: String) = index[name.lowercase()]
fun getNorrSpell(name: String, book: String) = data[book]?.spell().orEmpty()
    .firstOrNull { it.name().equals(name, ignoreCase = true) }

fun findNorrSpells(text: String, limit: Int = 10) = index.keys
    .filter { it.contains(text.lowercase()) }
    .sortedWith(compareBy<String> { !it.startsWith(text, ignoreCase = true) }.thenComparing(String.CASE_INSENSITIVE_ORDER))
    .take(limit)

private val data by lazy { load() }
private val index by lazy { data.values.flatMap { it.spell() }.associateBy { it.name().lowercase() } }

private fun load(): Map<String, Spells> = listNorrFiles("data/spells", "spells-*.json")
    .filterNot { it.fileName.name.startsWith("spells-ua-") }
    .associate {
        val data: Map<String, Any> = readJsonAsMap(it)
        val name = it.fileName.name
        name.sub(7, -5) to magicMap(data)
    }

private fun parseToHit(spell: Spell) = when {
    spell.attack == "M" -> "melee attack"
    spell.attack == "R" -> "ranged attack"
    spell.savingThrow.isNotBlank() -> Stat.valueOf(spell.savingThrow.uppercase()).shortDisplay + " save"
    else -> "-"
}
