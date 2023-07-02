package org.hertsig.dnd.norr.spell

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.hertsig.core.logger
import org.hertsig.core.warn
import org.hertsig.dnd.combat.dto.Spell
import org.hertsig.dnd.combat.dto.SpellSchool
import org.hertsig.dnd.combat.dto.SpellText
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.norr.Entry
import org.hertsig.dnd.norr.Template
import org.hertsig.dnd.norr.parseNorrTemplate
import org.hertsig.magic.DynamicEntry

private val log = logger {}

private val cache: LoadingCache<String, Spell?> = Caffeine.newBuilder()
    .build { name: String -> getNorrSpell(name)?.let(::parseSpell) }

fun getSpell(name: String) = cache[name]

internal fun parseSpell(spell: NorrSpell) = SpellParser(spell).parsed

private class SpellParser(private val spell: NorrSpell) {
    private val text = mutableListOf<SpellText>()
    private val rolls = mutableListOf<MultiDice>()
    val parsed by lazy { parse() }

    private fun parse(): Spell {
        parseText()
        return Spell(
            spell.name(),
            spell.source(),
            spell.level(),
            text.toList(),
            parseTime(spell.time()),
            parseDuration(spell.duration()),
            spell.components().display(),
            SpellSchool.get(spell.school()),
            spell.range().display(),
            spell.spellAttack().orEmpty().joinToString(";"),
            spell.savingThrow().orEmpty().joinToString(";"),
            spell.scalingLevelDice().isNotEmpty(),
            rolls.firstOrNull(),
        )
    }

    private fun parseText(): Pair<List<SpellText>, List<MultiDice>> {
        spell.entries().forEach(::parseSpellEntry)
        if (spell.entriesHigherLevel().isNotEmpty()) {
            text.add(SpellText.Text("\nAt higher levels:"))
            spell.entriesHigherLevel().forEach(::parseSpellEntry)
        }
        return Pair(text, emptyList())
    }

    private fun parseSpellEntry(e: DynamicEntry) {
        when {
            e.test<String>() -> text.addAll(parseSpellText(e.get()))
            e.isMap() -> when (val type = e.getMapValue<String>("type")) {
                "entry", "entries" -> text.addAll(parseEntry(e.get()))
                "table" -> text.add(parseTable(e.get()))
                "list" -> text.addAll(parseList(e.get()))
                else -> { log.warn { "Unexpected entry type $type" }; e.analyze("Entry") }
            }
            else -> { log.warn("Unexpected entry class ${e.javaClass.simpleName}"); e.analyze("Entry") }
        }
    }

    private fun parseEntry(entry: Entry): List<SpellText> {
        return entry.entries().flatMap(::parseSpellText)
    }

    private fun parseSpellText(rawText: String): List<SpellText> {
        val (text, templates) = rawText.parseNorrTemplate()
        val damages = templates.filterIsInstance<Template.DamgeWithType>().map { MultiDice(it.dice) }
        rolls.addAll(damages)
        return listOf(SpellText.Text(text))
    }

    private fun parseTable(table: Table): SpellText {
        return SpellText.Text("(${table.caption()} table omitted)")
    }

    private fun parseList(list: EntryList): List<SpellText> {
        return list.items().flatMap(::parseSpellText)
    }

    private fun parseTime(time: List<Time>) = time.joinToString("; ") { it.display() }
    private fun parseDuration(duration: List<Duration>) = duration.joinToString("; ") { it.display() }
}
