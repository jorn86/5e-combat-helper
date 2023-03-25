package org.hertsig.dnd.norr.spell

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.hertsig.core.logger
import org.hertsig.core.warn
import org.hertsig.dnd.combat.dto.Spell
import org.hertsig.dnd.combat.dto.SpellText
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.norr.DAMAGE_TYPE
import org.hertsig.dnd.norr.Entry
import org.hertsig.dnd.norr.Template
import org.hertsig.dnd.norr.parseNorrTemplate

private val log = logger {}

private val cache: LoadingCache<String, Spell?> = Caffeine.newBuilder()
    .maximumSize(1000)
    .build { name: String -> getNorrSpell(name)?.let(::parseSpell) }

fun getSpell(name: String) = cache[name]

internal fun parseSpell(spell: NorrSpell) = SpellParser(spell).parsed

private class SpellParser(private val spell: NorrSpell) {
    private val text = mutableListOf<SpellText>()
    private val rolls = mutableListOf<MultiDice>()
    val parsed by lazy { parse() }

    private fun parse(): Spell {
//        spell.analyze("NorrSpell")
        parseText()
        return Spell(
            spell.name(),
            spell.level(),
            text.toList(),
            parseTime(spell.time()),
            parseDuration(spell.duration()),
            spell.components().display(),
            spell.range().display(),
            spell.spellAttack().orEmpty().joinToString(";"),
            spell.savingThrow().orEmpty().joinToString(";"),
            spell.scalingLevelDice().isNotEmpty(),
            rolls.firstOrNull(),
        )
    }

    private fun parseText(): Pair<List<SpellText>, List<MultiDice>> {
        spell.entries().forEach {
            when {
                it.test<String>() -> text.addAll(parseSpellText(it.get()))
                it.isMap() -> when (val type = it.getMapValue<String>("type")) {
                    "entry" -> text.addAll(parseEntry(it.get()))
                    "table" -> text.add(parseTable(it.get()))
                    "list" -> text.addAll(parseList(it.get()))
                    else -> { log.warn { "Unexpected entry type $type" }; it.analyze("Entry") }
                }
                else -> { log.warn("Unexpected entry class ${it.javaClass.simpleName}"); it.analyze("Entry") }
            }
        }
        return Pair(text, emptyList())
    }

    private fun parseSpellText(text: String): List<SpellText> {
        val (text, templates) = text.parseNorrTemplate()
        val damages = templates.filterIsInstance<Template.Damage>().map { it.dice }
        val damageTypes = DAMAGE_TYPE.findAll(text).map { it.groupValues[1] }.toList()
        val typedDamages = damages.zip(damageTypes) { damage, type -> damage(type) }
        if (typedDamages.isNotEmpty()) rolls.add(MultiDice(typedDamages))
        return listOf(SpellText.Text(text))
    }

    private fun parseEntry(entry: Entry): List<SpellText> {
        return entry.entries().flatMap(::parseSpellText)
    }

    private fun parseTable(table: Table): SpellText {
        return SpellText.Text(table.toString())
    }

    private fun parseList(list: EntryList): List<SpellText> {
        return list.items().flatMap(::parseSpellText)
    }

    private fun parseTime(time: List<Time>) = time.joinToString("; ") { it.display() }
    private fun parseDuration(duration: List<Duration>) = duration.joinToString("; ") { it.display() }
}
