package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.element.cap
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.norr.*
import org.hertsig.dnd.norr.spell.Spellcasting
import org.hertsig.dnd.norr.spell.one
import org.hertsig.dnd.norr.spell.three
import org.hertsig.dnd.norr.spell.two
import org.hertsig.logger.logger
import org.hertsig.magic.getAll
import org.hertsig.util.applyIf
import org.hertsig.util.distinctByKeepLast
import java.util.*

private val log = logger {}

fun updateStatBlock(monster: Monster, original: StatBlock = StatBlock()): StatBlock {
    monster.delegate()?.let {
        val delegate = getFromBestiary(it.name(), it.source().lowercase())
        if (delegate == null) {
            log.warn("No bestiary result for delegate $it of $monster")
        } else {
            log.debug { "Delegating to ${delegate.name()}: $monster" }
            return updateStatBlock(delegate, original).copy(name = monster.name())
        }
    }
    val cr = ChallengeRating(monster.cr().cr())
    val (proficient, expertise) = monster.analyzeSkills(cr.proficiencyBonus)
    val (trait, action, bonus, reaction, legendary) = monster.analyzeAbilities()
    val spellcasting = monster.analyzeSpellcasting()
    return original.copy(
        name = monster.name(),
        size = Size(monster.size().first()),
        type = monster.type().display(),
        challengeRating = cr,
        proficiencyBonus = cr.proficiencyBonus,
        maxHitPoints = monster.hp().average(),
        strength = monster.str(),
        dexterity = monster.dex(),
        constitution = monster.con(),
        intelligence = monster.int(),
        wisdom = monster.wis(),
        charisma = monster.cha(),
        armorClass = monster.ac().joinToString(", ") { it.display() }.parseNorrTemplateText(),
        speed = monster.parseSpeed(),
        senses = monster.senses().joinToString(", ") { if (it.endsWith("ft")) "$it." else it }.cap(),
        languages = monster.languages().joinToString(", "),
        proficientSaves = monster.save().parse(),
        proficientSkills = proficient,
        expertiseSkills = expertise,
        damageResistances = displayDamageResist(monster.resist()),
        damageImmunities = displayDamageResist(monster.immune()),
        conditionImmunities = monster.conditionImmune().joinToString(", ") { it.display() }.cap(),
        traits = trait,
        actions = action,
        bonusActions = bonus,
        reactions = reaction,
        legendaryActions = legendary,
        legendaryActionUses = monster.parseLegendaryHeader(),
        spellcasting = spellcasting,
    )
}

data class Abilities(
    val traits: List<Ability> = listOf(),
    val actions: List<Ability> = listOf(),
    val bonusActions: List<Ability> = listOf(),
    val reactions: List<Ability> = listOf(),
    val legendaryActions: List<Ability> = listOf(),
)

private fun Monster.analyzeAbilities(): Abilities {
    val (bonusTraits, traits) = trait().map { it.parseAbility(this) }.split { it.mightBeBonusAction() }
    val (bonusActions, actions) = action().map { it.parseAbility(this) }.split { it.mightBeBonusAction() }
    val (variantBonusActions, variantActions) = variant().map { it.parseAbility(this) }.split { it.mightBeBonusAction() }
    val bonus = bonus().map { it.parseAbility(this) }
    val reaction = reaction().map {it.parseAbility(this) }
    val legendary = legendary().map { it.parseLegendaryAbility(this) }
    return Abilities(traits, actions + variantActions,
        bonus + bonusActions + bonusTraits + variantBonusActions, reaction, legendary)
}

private fun Monster.analyzeSpellcasting() = spellcasting().flatMap {
    listOfNotNull(it.analyzeInnateSpellcasting(), it.analyzeListSpellcasting())
}

private fun Spellcasting.analyzeAbility() = when (val ability = ability()) {
    "int" -> Stat.INTELLIGENCE
    "wis" -> Stat.WISDOM
    "cha" -> Stat.CHARISMA
    else -> error("Unexpected spellcasting ability: $ability")
}

private fun Spellcasting.analyzeInnateSpellcasting(): InnateSpellcasting? {
    val will = will().orEmpty()
    val daily = daily()
    if (will.isEmpty() && daily == null) return null
    val spells = mapOf(
        0 to will,
        3 to daily?.three().orEmpty(),
        2 to daily?.two().orEmpty(),
        1 to daily?.one().orEmpty(),
    ).mapValues { (_, it) -> it.map(String::parseSpellNameTemplate) }
        .filterValues { it.isNotEmpty() }
    return InnateSpellcasting(name(), analyzeAbility(), spells)
}

private fun Spellcasting.analyzeListSpellcasting(): SpellListCasting? {
    val spells = spells() ?: return null
//    val trait = spellcasting.spells().mapKeys { (level, _) -> level.toInt() }
    val headerText = headerEntries().joinToString(";")
    val warlock = headerText.contains("warlock", ignoreCase = true)
    val result = Regex("(\\d+)\\w+-level spellcaster").find(headerText)
    val level = result?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { CasterLevel(it, warlock) } ?: CasterLevel.NONE
    val list = Regex("following (\\w+) spells").find(headerText)?.groupValues?.getOrNull(1)
        ?: Regex("the (\\w+) spell list").find(headerText)?.groupValues?.getOrNull(1)
        ?: "?"
    val spellList = SpellList.values().singleOrNull { it.name.equals(list, true) }
    val ability = analyzeAbility()
    if (spellList?.stat != ability) {
        log.warn("Spellcasting mismatch: $spellList with $ability")
    }
    val parsedSpells = mapOf(
        0 to spells.cantrips(),
        1 to spells.firstLevel(),
        2 to spells.secondLevel(),
        3 to spells.thirdLevel(),
        4 to spells.fourthLevel(),
        5 to spells.fifthLevel(),
        6 to spells.sixthLevel(),
        7 to spells.seventhLevel(),
        8 to spells.eighthLevel(),
        9 to spells.ninthLevel(),
    ).filterValuesNotNull()
        // consider checking it.slots() with parsed caster level
        .mapValues { (_, it) -> it.spells().map { it.parseSpellNameTemplate() } }
    return SpellListCasting(name(), spellList ?: SpellList.WIZARD, level, parsedSpells)
}

private fun String.parseSpellNameTemplate(): StatblockSpell {
    var template: Template.Spell? = null
    val text = parseNorrTemplateText { it ->
        val spellTemplate = templateValue(it)
        if (spellTemplate is Template.Spell) template = spellTemplate
        ""
    }
    val name = template?.name ?: error("Invalid spell name template: $this")
    return StatblockSpell(name, text.trim()) // TODO parse "(self only)"
}

private fun SavingThrows?.parse(): EnumSet<Stat> {
    val result = EnumSet.noneOf(Stat::class.java)
    // TODO check values against skill + pb
    if (this?.str() != null) result.add(Stat.STRENGTH)
    if (this?.dex() != null) result.add(Stat.DEXTERITY)
    if (this?.con() != null) result.add(Stat.CONSTITUTION)
    if (this?.int() != null) result.add(Stat.INTELLIGENCE)
    if (this?.wis() != null) result.add(Stat.WISDOM)
    if (this?.cha() != null) result.add(Stat.CHARISMA)
    return result
}

private fun Monster.analyzeSkills(proficiencyBonus: Int): Pair<EnumSet<Skill>, EnumSet<Skill>> {
    val proficient = EnumSet.noneOf(Skill::class.java)
    val expertise = EnumSet.noneOf(Skill::class.java)
    val bonuses: MutableMap<Skill, String?> = EnumMap(Skill::class.java)
    val skills = skill()
    bonuses[Skill.ACROBATICS] = skills?.acrobatics()
    bonuses[Skill.ANIMAL_HANDLING] = skills?.animalHandling()
    bonuses[Skill.ARCANA] = skills?.arcana()
    bonuses[Skill.ATHLETICS] = skills?.athletics()
    bonuses[Skill.DECEPTION] = skills?.deception()
    bonuses[Skill.HISTORY] = skills?.history()
    bonuses[Skill.INSIGHT] = skills?.insight()
    bonuses[Skill.INTIMIDATION] = skills?.intimidation()
    bonuses[Skill.INVESTIGATION] = skills?.investigation()
    bonuses[Skill.MEDICINE] = skills?.medicine()
    bonuses[Skill.NATURE] = skills?.nature()
    bonuses[Skill.PERCEPTION] = skills?.perception()
    bonuses[Skill.PERFORMANCE] = skills?.performance()
    bonuses[Skill.PERSUASION] = skills?.persuasion()
    bonuses[Skill.RELIGION] = skills?.religion()
    bonuses[Skill.SLEIGHT_OF_HAND] = skills?.sleightOfHand()
    bonuses[Skill.STEALTH] = skills?.stealth()
    bonuses[Skill.SURVIVAL] = skills?.survival()
    bonuses.filterValuesNotNull { !it.isNullOrBlank() }.mapValues { it.value.toInt() }.forEach { (skill, modifier) ->
        val ability = abilityModifier(skill.stat)
        val expectedProficientModifier = ability + proficiencyBonus
        val expectedExpertiseModifier = expectedProficientModifier + proficiencyBonus
        when (modifier) {
            expectedProficientModifier -> proficient.add(skill)
            expectedExpertiseModifier -> expertise.add(skill)
            else -> log.warn("Unexpected bonus for $skill: Was $modifier, expected $expectedProficientModifier or $expectedExpertiseModifier")
        }
    }
    return proficient to expertise
}

private fun Monster.parseSpeed(): String {
    val speeds = speed()
    return mapOf("" to speeds.walk(), "fly " to speeds.fly(), "climb " to speeds.climb(), "burrow " to speeds.burrow(), "swim " to speeds.swim())
        .filterValuesNotNull()
        .mapValues { (_, speed) -> speed.display() }
        .entries.joinToString(", ") { (prefix, amount) -> "$prefix$amount" }
        .cap()
}

private fun Monster.parseLegendaryHeader(): Int {
    if (legendary().isEmpty()) return 0
    val text = legendaryHeader().firstOrNull() ?: legendary().first().entries().getAll<String>().first()
    return text.substringAfter("can take ").substringBefore(" legendary actions").toIntOrNull() ?: 3
}

private fun Entry.parseLegendaryAbility(monster: Monster): Ability {
    val name = name().orEmpty()
    val cost = Regex("Costs (\\d+) action", RegexOption.IGNORE_CASE).find(name) ?: return parseAbility(monster, name, 1)
    return parseAbility(monster, name.substringBefore(" ("), cost.groupValues[1].toInt())
}

private fun Entry.parseAbility(monster: Monster, name: String = name().orEmpty(), legendaryCost: Int? = null): Ability {
    val (parsedName, recharge) = parseRecharge(name)
    val (finalName, use) = parseUse(parsedName)
    val text = entries().getAll<String>().joinToString(" ")
    if (type() == "variant") {
        entries().getAll<Entry>().singleOrNull()?.let {
            val variantAbility = parseAbility(monster)
            val fullName = "Variant: ${variantAbility.name}"
            return when (variantAbility) {
                is Ability.Trait -> variantAbility.copy(fullName, description = text + variantAbility.description)
                else -> variantAbility.baseCopy(fullName)
            }
        }
    }
    log.trace("Parsing ability $finalName: $text")
    val (parsedText, templates) = text.parseNorrTemplate()
    val attack = templates.singleTypeOrNull<Template.Attack>()
    return if (attack != null) {
        val toHit = templates.firstType<Template.ToHit>()
        val damages = templates.filterIsInstance<Template.DamageWithType>().map { it.dice }
            // Take only the last (two-handed) damage value per type. Should keep original order.
            .distinctByKeepLast { it.type }.toList()
        val stat = analyzeAttackStat(monster, toHit, if (attack.isSpell()) EnumSet.of(Stat.INTELLIGENCE, Stat.WISDOM, Stat.CHARISMA) else EnumSet.of(Stat.STRENGTH, Stat.DEXTERITY))
        val damageModifier = stat?.let { monster.abilityModifier(it) } ?: 0
        val damage = if (damages.isEmpty()) MultiDice(Dice.NONE) else MultiDice(damages) - damageModifier
        val reach = if (attack.isMelee()) parseReach(parsedText) else null
        val (range, longRange) = if (attack.isRanged()) parseRange(parsedText) else Pair(null, null)
        val extraHitModifier = if (stat == null) toHit.modifier - monster.proficiencyBonus else 0
        val extraText = parsedText.substringAfter("damage. ", "")
        Ability.Attack(finalName, stat, extraHitModifier, reach, range, longRange, damage, extraText, use, legendaryCost)
    } else {
        val displayText = text.parseNorrTemplateText { it ->
            val value = templateValue(it)
            if (value is Template.DamageWithType) value.dice.asString(false) else value.text
        }
        val damage = templates.filterIsInstance<Template.DamageWithType>().firstOrNull()
        val roll = templates.singleTypeOrNull<Template.Dice>()?.let { MultiDice(it.dice) } ?: damage?.let { MultiDice(it.dice) }
        Ability.Trait(finalName, recharge, displayText, roll, use, legendaryCost)
    }
}

fun parseRecharge(text: String): Pair<String, Recharge> {
    var recharge = Recharge.NO
    val parsedText = text.parseNorrTemplateText { it ->
        val value = templateValue(it)
        if (value is Template.Recharge) recharge = value.recharge
        value.text
    }
    return parsedText.trim() to recharge
}

private fun parseUse(text: String): Pair<String, Use> {
    val match = USE.find(text) ?: return Pair(text, Use.Unlimited)
    val limit = match.groupValues[1].toInt()
    val use = Use.Limited(limit, match.groupValues[2].trim().lowercase())
    val name = text.substringBefore(" (").trim()
    return Pair(name, use)
}

private fun parseReach(text: String) = REACH.find(text)!!.groupValues[1].toInt()
private fun parseRange(text: String): Pair<Int, Int?> {
    val match = RANGE.find(text)!!
    return Pair(match.groupValues[1].toInt(), match.groupValues.getOrNull(2)?.toIntOrNull())
}

private fun analyzeAttackStat(monster: Monster, toHit: Template.ToHit, stats: EnumSet<Stat>): Stat? {
    val proficiencyBonus = monster.proficiencyBonus
    return stats.firstOrNull { proficiencyBonus + monster.abilityModifier(it) == toHit.modifier }
}

private fun Ability.mightBeBonusAction(): Boolean {
    val text = when(this) {
        is Ability.Trait -> description
        is Ability.Attack -> extra
    }
    return text.contains("bonus action", ignoreCase = true)
            && !text.contains("takes a bonus action to", ignoreCase = true)
}

private fun Monster.abilityScore(stat: Stat) = when (stat) {
    Stat.STRENGTH -> str()
    Stat.DEXTERITY -> dex()
    Stat.CONSTITUTION -> con()
    Stat.INTELLIGENCE -> int()
    Stat.WISDOM -> wis()
    Stat.CHARISMA -> cha()
}

private val Monster.proficiencyBonus get() = ChallengeRating.invoke(cr().cr()).proficiencyBonus
private fun Monster.abilityModifier(stat: Stat) = mod(abilityScore(stat))

@Suppress("UNCHECKED_CAST")
private fun <K, V> Map<K, V?>.filterValuesNotNull(filter: (V?) -> Boolean = { it != null }): Map<K, V> =
    filterValues(filter) as Map<K, V>

private fun <T> List<T>.split(condition: (T) -> Boolean): Pair<List<T>, List<T>> {
    val match = mutableListOf<T>()
    val noMatch = mutableListOf<T>()
    forEach { (if (condition(it)) match else noMatch).add(it) }
    return Pair(match, noMatch)
}

inline fun <reified T> List<*>.singleType(): T = singleTypeOrNull()
    ?: error("Expected one ${T::class.simpleName}, but got $this")
inline fun <reified T> List<*>.singleTypeOrNull() = singleOrNull { it is T } as T?
inline fun <reified T> List<*>.firstType() = first { it is T } as T

private val REACH = Regex("reach (\\d+) ft.")
private val RANGE = Regex("range (\\d+)/?(\\d+)? ft.")
private val USE = Regex("(\\d+)/(\\w+)")
