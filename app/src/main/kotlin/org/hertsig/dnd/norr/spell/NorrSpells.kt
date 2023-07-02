package org.hertsig.dnd.norr.spell

import org.hertsig.dnd.combat.dto.Spell
import org.hertsig.dnd.combat.dto.SpellText
import org.hertsig.dnd.combat.dto.Stat
import org.hertsig.dnd.combat.dto.shortDisplay
import org.hertsig.dnd.norr.book.ClassSpellList
import org.hertsig.dnd.norr.listNorrFiles
import org.hertsig.dnd.norr.parseNorrTemplateText
import org.hertsig.dnd.norr.readJsonAsMap
import org.hertsig.magic.DynamicMap
import org.hertsig.magic.getAll
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

private fun load(): Map<String, Spells> {
    return listNorrFiles("spells", "spells-*.json").associate {
        val data: Map<String, Any> = readJsonAsMap(it)
        val name = it.fileName.name
        name.sub(7, -5) to magicMap(data)
    }
}


fun main() {
    val classSpells = mutableMapOf<String, MutableSet<String>>()
    // Only works for PHB
    listNorrFiles("book", "book-phb.json").map { readJsonAsMap(it) }.map { book ->
        DynamicMap(book)
            .wrapList("data")
            .filter { it.string("type") == "section" && it.string("name") == "Spells" }
            .flatMap { it.dynamicList("entries").getAll<ClassSpellList> { list -> list.type() == "entries" } }
            .forEach { classList ->
                val theClass = classList.name().removeSuffix(" Spells")
                classList.entries().flatMap { it.items() }.forEach {
                    classSpells.getOrPut(it.parseNorrTemplateText()) { mutableSetOf() }.add(theClass)
                }
            }
    }

    println("Name,Book,Die,Type,To hit,Range,Area,Classes,Extra")
    data.values.asSequence()
        .flatMap { it.spell() }
        .filter { it.level() == 0 }
        .filter { "(UA)" !in it.name() }
        .distinctBy { it.name() }
        .map { parseSpell(it) }
        .filter { it.damage != null }
        .sortedBy { it.name }
        .forEach {
            val name = it.name
            val book = it.source
//            val classes = it.
            val die = it.damage!!.main.sizes.single()
            val type = it.damage.main.type
            val range = it.range.removeSuffix(" radius").removeSuffix(" ft.")
            val area = it.text.filterIsInstance<SpellText.Text>().any { text ->
                text.text.contains("Each creature") || text.text.contains("All other creatures")
            }
            val toHit = parseToHit(it)
            val classes = classSpells[name].orEmpty().joinToString("/")
            // scaling: always true
            println("$name,$book,$die,$type,$toHit,$range,$area,$classes")
//            println(classes)
        }
}

private fun parseToHit(spell: Spell) = when {
    spell.attack == "M" -> "melee attack"
    spell.attack == "R" -> "ranged attack"
    spell.savingThrow.isNotBlank() -> Stat.valueOf(spell.savingThrow.uppercase()).shortDisplay + " save"
    else -> "-"
}
