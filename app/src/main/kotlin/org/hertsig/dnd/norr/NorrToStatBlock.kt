package org.hertsig.dnd.norr

import org.hertsig.core.logger
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.element.*
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.magic.getAll
import java.util.*

private val log = logger {}

fun updateStatBlock(monster: Monster, original: StatBlock = StatBlock()): StatBlock {
    val cr = ChallengeRating(monster.cr())
    val (proficient, expertise) = monster.analyzeSkills(cr.proficiencyBonus)
    val (trait, action, bonus, reaction, legendary) = monster.analyzeAbilities()
    val spellcasting = monster.analyzeSpellcasting()
    val traits = trait.toMutableList()
    if (spellcasting != null) traits.add(Ability.Trait(spellcasting.name))
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
        armorClass = monster.ac().joinToString(", ") { it.display() },
        speed = monster.parseSpeed(),
        senses = monster.senses().orEmpty().joinToString(", ") { if (it.endsWith("ft")) "$it." else it }.cap(),
        languages = monster.languages().orEmpty().joinToString(", "),
        proficientSaves = monster.save().parse(),
        proficientSkills = proficient,
        expertiseSkills = expertise,
        damageResistances = displayDamageResist(monster.resist()),
        damageImmunities = displayDamageResist(monster.immune()),
        conditionImmunities = monster.conditionImmune().orEmpty().joinToString(", ") { it.display() }.cap(),
        traits = traits,
        actions = action,
        bonusActions = bonus,
        reactions = reaction,
        legendaryActions = legendary,
        legendaryActionUses = monster.parseLegendaryHeader(),
        casterAbility = spellcasting?.stat,
        spellSlots = spellcasting?.level ?: CasterLevel.NONE,
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
    val (bonusTraits, traits) = trait().getAll<Named>().map { it.parseAbility(this) }.split { it.mightBeBonusAction() }
    val (bonusActions, actions) = action().getAll<Named>().map { it.parseAbility(this) }.split { it.mightBeBonusAction() }
    val bonus = bonus().getAll<Named>().map { it.parseAbility(this) }
    val reaction = reaction().getAll<Named>().map {it.parseAbility(this) }
    val legendary = legendary().getAll<Named>().map { it.parseLegendaryAbility(this) }
    return Abilities(traits, actions, bonus + bonusTraits + bonusActions, reaction, legendary)
}

data class MonsterSpellcasting(val name: String, val stat: Stat?, val level: CasterLevel)

private fun Monster.analyzeSpellcasting(): MonsterSpellcasting? {
    val spellcasting = spellcasting()?.firstOrNull { it.name() == "Spellcasting" }
        ?: spellcasting()?.firstOrNull { it.name() == "Innate Spellcasting" }
        ?: return null
    val ability = when (spellcasting.ability()) {
        "int" -> Stat.INTELLIGENCE
        "wis" -> Stat.WISDOM
        "cha" -> Stat.CHARISMA
        else -> null
    }
    val headerText = spellcasting.headerEntries().joinToString(";")
    val warlock = headerText.contains("warlock", ignoreCase = true)
    val result = Regex("(\\d+)\\w+-level spellcaster").find(headerText)
    val level = result?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { CasterLevel(it, warlock) } ?: CasterLevel.NONE
    return MonsterSpellcasting(spellcasting.name(), ability, level)
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
    val text = legendaryHeader()?.firstOrNull() ?: legendary().first().get<Named>().entries().first()
    return text.substringAfter("can take ").substringBefore(" legendary actions").toIntOrNull() ?: 3
}

private fun Named.parseLegendaryAbility(monster: Monster): Ability {
    val name = name().orEmpty()
    val cost = Regex("Costs (\\d+) action", RegexOption.IGNORE_CASE).find(name) ?: return parseAbility(monster, name, 1)
    return parseAbility(monster, name.substringBefore(" ("), cost.groupValues[1].toInt())
}

private fun Named.parseAbility(monster: Monster, name: String = name().orEmpty(), legendaryCost: Int? = null): Ability {
    val (parsedName, recharge) = parseRecharge(name)
    val (finalName, use) = parseUse(parsedName)
    val text = entries().joinToString(" ")
    log.debug("Parsing ability $finalName: $text")
    val templates = mutableListOf<Template>()
    val parsedText = text.replace(templateRegex) {
        val replacement = templateValue(it)
        templates.add(replacement)
        replacement.text
    }
    val attack = templates.singleTypeOrNull<Template.Attack>()
    return if (attack != null) {
        val toHit = templates.singleType<Template.ToHit>()
        val damages = templates.filterIsInstance<Template.Damage>().map { it.dice }
        val stat = analyzeAttackStat(monster, toHit, if (attack.isSpell()) EnumSet.of(Stat.INTELLIGENCE, Stat.WISDOM, Stat.CHARISMA) else EnumSet.of(Stat.STRENGTH, Stat.DEXTERITY))
        val damageModifier = stat?.let { monster.abilityModifier(it) } ?: 0
        val damageTypes = DAMAGE_TYPE.findAll(parsedText).map { it.groupValues[1] }.toList()
        val typedDamages = damages.zip(damageTypes) { damage, type -> damage(type) }
        val damage = if (typedDamages.isEmpty()) MultiDice(Dice.NONE) else MultiDice(typedDamages) - damageModifier
        val reach = if (attack.isMelee()) parseReach(parsedText) else null
        val (range, longRange) = if (attack.isRanged()) parseRange(parsedText) else Pair(null, null)
        val extraHitModifier = if (stat == null) toHit.modifier else 0
        val extraText = parsedText.substringAfter("damage. ", "")
        Ability.Attack(finalName, stat, extraHitModifier, reach, range, longRange, damage, extraText, use, legendaryCost)
    } else {
        val displayText = text.replace(templateRegex) {
            val value = templateValue(it)
            if (value is Template.Damage) value.dice.asString(false) else value.text
        }
        val damage = templates.filterIsInstance<Template.Damage>().firstOrNull()
        val roll = templates.singleTypeOrNull<Template.Dice>()?.let { MultiDice(it.dice) } ?: damage?.let { MultiDice(it.dice) }
        Ability.Trait(finalName, recharge, displayText, roll, use, legendaryCost)
    }
}

fun parseRecharge(text: String): Pair<String, Recharge> {
    var recharge = Recharge.NO
    val parsedText = text.replace(templateRegex) {
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
    val proficiencyBonus = ChallengeRating.invoke(monster.cr()).proficiencyBonus
    return stats.firstOrNull { proficiencyBonus + monster.abilityModifier(it) == toHit.modifier }
}

private fun Ability.mightBeBonusAction() = when(this) {
    is Ability.Trait -> description.contains("bonus action", ignoreCase = true)
    is Ability.Attack -> extra.contains("bonus action", ignoreCase = true)
}

private fun Monster.abilityScore(stat: Stat) = when (stat) {
    Stat.STRENGTH -> str()
    Stat.DEXTERITY -> dex()
    Stat.CONSTITUTION -> con()
    Stat.INTELLIGENCE -> int()
    Stat.WISDOM -> wis()
    Stat.CHARISMA -> cha()
}

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

private inline fun <reified T> List<*>.singleType() = single { it is T } as T
private inline fun <reified T> List<*>.singleTypeOrNull() = singleOrNull { it is T } as T?

private val DAMAGE_TYPE = Regex("\\($DAMAGE_MARKER\\) (\\w+)")
private val REACH = Regex("reach (\\d+) ft.")
private val RANGE = Regex("range (\\d+)/?(\\d+)? ft.")
private val USE = Regex("(\\d+)/(\\w+)")
