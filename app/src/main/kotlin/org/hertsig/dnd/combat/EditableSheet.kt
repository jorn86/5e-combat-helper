package org.hertsig.dnd.combat

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mu.KotlinLogging
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.component.*
import org.hertsig.dnd.dice.Dice
import java.util.*

private val log = KotlinLogging.logger {}

@Composable
fun EditableSheet(statBlock: StatBlock, state: AppState, modifier: Modifier = Modifier) {
    Column(modifier.padding(8.dp), Arrangement.spacedBy(4.dp)) {
        val name = remember { mutableStateOf(statBlock.name) }
        val size = remember { mutableStateOf(statBlock.size) }
        val type = remember { mutableStateOf(statBlock.type) }
        val challengeRating = remember { mutableStateOf(statBlock.challengeRating) }
        val proficiencyBonus = remember { mutableStateOf(statBlock.proficiencyBonus) }
        val strength = remember { mutableStateOf(statBlock.strength) }
        val dexterity = remember { mutableStateOf(statBlock.dexterity) }
        val constitution = remember { mutableStateOf(statBlock.constitution) }
        val intelligence = remember { mutableStateOf(statBlock.intelligence) }
        val wisdom = remember { mutableStateOf(statBlock.wisdom) }
        val charisma = remember { mutableStateOf(statBlock.charisma) }
        val armorClass = remember { mutableStateOf(statBlock.armorClass) }
        val maxHitPoints = remember { mutableStateOf(statBlock.maxHitPoints) }
        val conditionImmunities = remember { mutableStateOf(statBlock.conditionImmunities) }
        val damageImmunities = remember { mutableStateOf(statBlock.damageImmunities) }
        val damageResistances = remember { mutableStateOf(statBlock.damageResistances) }
        val speed = remember { mutableStateOf(statBlock.speed) }
        val senses = remember { mutableStateOf(statBlock.senses) }
        val languages = remember { mutableStateOf(statBlock.languages) }
        val casterLevel = remember { mutableStateOf(statBlock.casterLevel) }
        val casterAbility = remember { mutableStateOf(statBlock.casterAbility) }
        val proficientSaves = remember { statBlock.proficientSaves.toMutableStateList() }
        val proficientSkills = remember { statBlock.proficientSkills.toMutableStateList() }
        val expertiseSkills = remember { statBlock.expertiseSkills.toMutableStateList() }
        val traits = remember { statBlock.traits.toMutableStateList() }
        val actions = remember { statBlock.actions.toMutableStateList() }
        val bonusActions = remember { statBlock.bonusActions.toMutableStateList() }
        val reactions = remember { statBlock.reactions.toMutableStateList() }
        val legendaryActions = remember { statBlock.legendaryActions.toMutableStateList() }
        val legendaryActionUses = remember { mutableStateOf(statBlock.legendaryActionUses) }

        fun save() {
            state.update(StatBlock(
                name.value, size.value, type.value, challengeRating.value, proficiencyBonus.value, maxHitPoints.value,
                strength.value, dexterity.value, constitution.value, intelligence.value, wisdom.value, charisma.value,
                armorClass.value, speed.value, senses.value, languages.value,
                proficientSaves.toEnumSet(), proficientSkills.toEnumSet(), expertiseSkills.toEnumSet(),
                conditionImmunities.value, damageImmunities.value, damageResistances.value,
                traits.toList(), actions.toList(), bonusActions.toList(), reactions.toList(), legendaryActions.toList(),
                legendaryActionUses.value, casterLevel.value, casterAbility.value,
            ))
        }

        ScrollableFlowColumn(16.dp, 16.dp) {
            Column(Modifier.padding(bottom = 16.dp), Arrangement.spacedBy(4.dp)) {
                FormRow("Name") { EditText(name, Modifier.weight(1f).autoFocus()) { save() } }
                FormRow("Size") { DropDown(size, Modifier.weight(1f), onUpdate = { save() }) }
                FormRow("Type") { EditText(type, Modifier.weight(1f)) { save() } }
                FormRow("Challenge rating") {
                    DropDown(challengeRating, Modifier.width(40.dp), TextAlign.End, TextAlign.Center, { proficiencyBonus.value = it.proficiencyBonus; save() }) { it.display }
                    RowTextLine("(${challengeRating.value.xp} XP)")
                }
                FormRow("Proficiency bonus") { EditNumber(proficiencyBonus, 2, 9) { save() } }
                FormRow("Abilities") {
                    FlowRow(mainAxisSpacing = 2.dp, crossAxisSpacing = 2.dp) {
                        EditAbilityScore("Strength", strength, ::save)
                        EditAbilityScore("Dexterity", dexterity, ::save)
                        EditAbilityScore("Constitution", constitution, ::save)
                        EditAbilityScore("Intelligence", intelligence, ::save)
                        EditAbilityScore("Wisdom", wisdom, ::save)
                        EditAbilityScore("Charisma", charisma, ::save)
                    }
                }
                FormRow("Armor class") { EditText(armorClass, Modifier.weight(1f)) { save() } }
                FormRow("Hit points") { EditNumber(maxHitPoints, 1, 999, 1, 40.dp) { save() } }
                FormRow("Condition immunities") { EditText(conditionImmunities, Modifier.weight(1f)) { save() } }
                FormRow("Damage immunities") { EditText(damageImmunities, Modifier.weight(1f)) { save() } }
                FormRow("Damage resistances") { EditText(damageResistances, Modifier.weight(1f)) { save() } }
                FormRow("Speed") { EditText(speed, Modifier.weight(1f)) { save() } }
                FormRow("Senses") { EditText(senses, Modifier.weight(1f)) { save() } }
                FormRow("Languages") { EditText(languages, Modifier.weight(1f)) { save() } }
                FormRow("Spellcasting") {
                    DropDown(casterLevel, Modifier.width(30.dp), onUpdate = { save() }) { it.display }
                    if (casterLevel.value != CasterLevel.NONE)
                        DropDown(casterAbility, Modifier.width(100.dp), onUpdate = { save() })
                }
                if (legendaryActions.isNotEmpty())
                    FormRow("Legendary actions") { EditNumber(legendaryActionUses, 0, 5) { save() } }

                ProficiencyBlock("Saving throws", proficientSaves, ::save)
                ProficiencyBlock("Proficient skills", proficientSkills, ::save)
                ProficiencyBlock("Expertise skills", expertiseSkills, ::save)
            }

            AbilityBlock("Traits", traits, ::save, false)
            AbilityBlock("Actions", actions, ::save)
            AbilityBlock("Bonus actions", bonusActions, ::save)
            AbilityBlock("Reactions", reactions, ::save)
            AbilityBlock("Legendary actions", legendaryActions, ::save, legendary = true)
        }
    }
}

@Composable
private inline fun <reified E: Enum<E>> ProficiencyBlock(
    label: String,
    proficiencies: SnapshotStateList<E>,
    crossinline save: () -> Unit,
    crossinline display: (E) -> String = { it.display }
) {
    val values = enumValues<E>().asList()
    val missingValues = values - proficiencies
    var show by remember { mutableStateOf(false) }
    FormRow(label) {
        FlowRow {
            proficiencies.forEach {
                Row(Modifier.padding(horizontal = 2.dp), Arrangement.spacedBy(2.dp)) {
                    RowTextLine(display(it))
                    IconButton({ proficiencies.remove(it); save() }, Icons.Default.Close, iconSize = 16.dp)
                }
            }

            IconButton({ show = true }, Icons.Default.Add, iconSize = 16.dp)
            CursorDropdownMenu(show, { show = false }) {
                missingValues.forEach {
                    Item(display(it)) { proficiencies.add(it); save() }
                }
            }
        }
    }
}

@Composable
private fun FormRow(label: String, content: @Composable RowScope.() -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        RowTextLine(label, Modifier.width(160.dp))
        content()
    }
}

@Composable
private fun EditAbilityScore(ability: String, state: MutableState<Int>, save: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        RowTextLine(ability, Modifier.width(90.dp)) // FIXME width is hack until we can properly table-layout this
        EditNumber(state, 0, 40) { save() }
    }
}

@Composable
private fun AbilityBlock(
    label: String,
    abilities: SnapshotStateList<Ability>,
    save: () -> Unit,
    showAttacks: Boolean = true,
    legendary: Boolean = false,
) {
    Column(Modifier.padding(bottom = 16.dp)) {
        TextLine(label, style = MaterialTheme.typography.h6)
        HorizontalDivider()
        Column(Modifier.padding(vertical = 4.dp), Arrangement.spacedBy(4.dp)) {
            abilities.forEach {
                EditSingleAbility(it, abilities, save, if (legendary && it !is LegendaryAbility) 1 else 0)
            }
            fun add(ability: Ability) {
                abilities.add(if (legendary) LegendaryAbility(ability) else ability)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Don't call save() yet when adding empty new abilities
                Button({ add(Ability.Trait()) }) { RowTextLine("Trait") }
                if (showAttacks) {
                    Button({ add(Ability.MeleeAttack()) }) { RowTextLine("Melee attack") }
                    Button({ add(Ability.RangedAttack()) }) { RowTextLine("Ranged attack") }
                }
                Button({ add(Ability.Custom()) }) { RowTextLine("Custom") }
            }
        }
    }
}

@Composable
private fun EditSingleAbility(ability: Ability, abilities: SnapshotStateList<Ability>, save: () -> Unit, legendaryUses: Int = 0) {
    when (ability) {
        is Ability.Trait -> EditTrait(abilities, ability, save, legendaryUses)
        is Ability.MeleeAttack -> EditMeleeAttack(abilities, ability, save, legendaryUses)
        is Ability.RangedAttack -> EditRangedAttack(abilities, ability, save, legendaryUses)
        is Ability.Custom -> EditCustom(abilities, ability, save, legendaryUses)
        is LegendaryAbility -> {
            require(legendaryUses == 0) { "Nested legendary: $ability" }
            EditLegendary(abilities, ability, save)
        }
        else -> log.error("No renderer for $ability")
    }
}

@Composable
private fun EditTrait(abilities: SnapshotStateList<Ability>, trait: Ability.Trait, save: () -> Unit, legendaryUses: Int = 0) {
    val name = remember(trait) { mutableStateOf(trait.name) }
    val description = remember(trait) { mutableStateOf(trait.description) }
    val use = remember(trait) { mutableStateOf(trait.use) }
    val legendary = remember(trait) { mutableStateOf(legendaryUses) }
    fun update(n: String = name.value, l: Int = legendary.value, d: String = description.value, u: Use = use.value) {
        update(abilities, trait, Ability.Trait(n, d, u), l)
        save()
    }
    EditAbility(trait, name, use, { n, l, u -> update(n = n, l = l, u = u) }, abilities, save, legendary, false) {
        EditText(description, Modifier.fillMaxWidth(), "Description") { update(d = it) }
    }
}

@Composable
private fun EditMeleeAttack(abilities: SnapshotStateList<Ability>, attack: Ability.MeleeAttack, save: () -> Unit, legendaryUses: Int = 0) {
    val name = remember(attack) { mutableStateOf(attack.name) }
    val stat = remember(attack) { mutableStateOf(attack.stat) }
    val modifier = remember(attack) { mutableStateOf(attack.modifier) }
    val proficient = remember(attack) { mutableStateOf(attack.proficient) }
    val reach = remember(attack) { mutableStateOf(attack.reach) }
    val target = remember(attack) { mutableStateOf(attack.target) }
    val damage = remember(attack) { mutableStateOf(attack.damage) }
    val use = remember(attack) { mutableStateOf(attack.use) }
    val legendary = remember(attack) { mutableStateOf(legendaryUses) }
    fun update(n: String = name.value, l: Int = legendary.value, s: Stat? = stat.value, m: Int = modifier.value, p: Boolean = proficient.value, r: Int = reach.value, t: String = target.value, d: Dice = damage.value, u: Use = use.value) {
        update(abilities, attack, Ability.MeleeAttack(n, s, m, p, r, t, d, u), l)
        save()
    }
    EditAbility(attack, name, use, { n, l, u -> update(n = n, l = l, u = u) }, abilities, save, legendary) {
        DropDown(stat, listOf(null, Stat.STRENGTH, Stat.DEXTERITY), Modifier.width(90.dp), onUpdate = { update(s = it) }) { it?.display ?: "Custom" }
        EditNumber(modifier, -5, 10, width = 50.dp, suffix = "to hit") { update(m = it) }
        TextLine("Reach")
        EditNumber(reach, 0, 20, 5, 36.dp, "ft.") { update(r = it) }
        EditDamage(damage.value) {
            if (it != null) {
                damage.value = it
                update(d = it)
            }
        }
    }
}

@Composable
private fun EditRangedAttack(abilities: SnapshotStateList<Ability>, attack: Ability.RangedAttack, save: () -> Unit, legendaryUses: Int = 0) {
    val name = remember(attack) { mutableStateOf(attack.name) }
    val stat = remember(attack) { mutableStateOf(attack.stat) }
    val modifier = remember(attack) { mutableStateOf(attack.modifier) }
    val proficient = remember(attack) { mutableStateOf(attack.proficient) }
    val range = remember(attack) { mutableStateOf(attack.range) }
    val longRange = remember(attack) { mutableStateOf(attack.longRange) }
    val target = remember(attack) { mutableStateOf(attack.target) }
    val damage = remember(attack) { mutableStateOf(attack.damage) }
    val use = remember(attack) { mutableStateOf(attack.use) }
    val legendary = remember(attack) { mutableStateOf(legendaryUses) }
    fun update(n: String = name.value, l: Int = legendary.value, s: Stat? = stat.value, m: Int = modifier.value, p: Boolean = proficient.value, r: Int = range.value, lr: Int = longRange.value, t: String = target.value, d: Dice = damage.value, u: Use = use.value) {
        update(abilities, attack, Ability.RangedAttack(n, s, m, p, r, lr, t, d, u), l)
        save()
    }
    EditAbility(attack, name, use, { n, l, u -> update(n = n, l = l, u = u) }, abilities, save, legendary) {
        DropDown(stat, listOf(null, Stat.STRENGTH, Stat.DEXTERITY), Modifier.width(90.dp), onUpdate = { update(s = it) }) { it?.display ?: "Custom" }
        EditNumber(modifier, -5, 10, width = 50.dp, suffix = "to hit") { update(m = it) }
        TextLine("Range")
        EditNumber(range, 10, 300, 5, 36.dp, "ft.") { update(r = it) }
        TextLine("/")
        EditNumber(longRange, 10, 600, 5, 36.dp, "ft.") { update(lr = it) }
        EditDamage(damage.value) {
            if (it != null) {
                damage.value = it
                update(d = it)
            }
        }
    }
}

@Composable
private fun EditCustom(abilities: SnapshotStateList<Ability>, ability: Ability.Custom, save: () -> Unit, legendaryUses: Int = 0) {
    val name = remember(ability) { mutableStateOf(ability.name) }
    val recharge = remember(ability) { mutableStateOf(ability.recharge) }
    val description = remember(ability) { mutableStateOf(ability.description) }
    val roll = remember(ability) { mutableStateOf(ability.roll) }
    val use = remember(ability) { mutableStateOf(ability.use) }
    val legendary = remember(ability) { mutableStateOf(legendaryUses) }

    fun update(n: String = name.value, l: Int = legendary.value, rc: Recharge = recharge.value, d: String = description.value, r: Dice? = roll.value, u: Use = use.value) {
        update(abilities, ability, Ability.Custom(n, rc, d, r, u), l)
        save()
    }
    EditAbility(ability, name, use, { n, l, u -> update(n = n, l = l, u = u) }, abilities, save, legendary) {
        DropDown(recharge, Modifier.width(30.dp), onUpdate = { update(rc = it) }) { it.display }
        EditDamage(ability.roll) { update(r = it) }
        EditText(description, Modifier.fillMaxWidth(), "Description") { update(d = it) }
    }
}

@Composable
private fun EditLegendary(abilities: SnapshotStateList<Ability>, ability: LegendaryAbility, save: () -> Unit) {
    EditSingleAbility(ability.ability, abilities, save, ability.cost)
}

@Composable
private fun EditAbility(
    ability: Ability,
    name: MutableState<String>,
    use: MutableState<Use>,
    onUpdate: (String, Int, Use) -> Unit,
    traits: SnapshotStateList<Ability>,
    save: () -> Unit,
    legendary: MutableState<Int>,
    flow: Boolean = true,
    content: @Composable () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        @Composable fun innerContent() {
            val modifier = if (name.value.isBlank()) Modifier.autoFocus() else Modifier
            EditText(name, modifier.width(100.dp), "Name") { onUpdate(it, legendary.value, use.value) }
            if (legendary.value > 0) {
                EditNumber(legendary, 1, 5) { onUpdate(name.value, it, use.value) }
            } else {
                EditUse(use) { onUpdate(name.value, legendary.value, it) }
            }
            content()
        }

        if (flow) {
            FlowRow(Modifier.weight(1f), mainAxisSpacing = 4.dp, crossAxisSpacing = 2.dp) { innerContent() }
        } else {
            Row(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) { innerContent() }
        }
        IconButton({ traits.remove(ability); save() }, Icons.Default.Close, iconSize = 16.dp)
    }
}

@Composable
private fun EditDamage(initial: Dice?, modifier: Modifier = Modifier, width: Dp = 200.dp, onUpdate: (Dice?) -> Unit) {
    val display = remember { mutableStateOf(initial?.asString(false).orEmpty()) }
    var error by remember { mutableStateOf(false) }
    EditText(display, modifier.width(width), "Damage") {
        error = try {
            onUpdate(Dice.parseOptional(it))
            false
        } catch (e: RuntimeException) {
            true
        }
    }
    if (error) Icon(Icons.Default.Error, "Parse error", Modifier.size(16.dp), MaterialTheme.colors.error)
}

@Composable
private fun EditUse(use: MutableState<Use>, onUpdate: (Use) -> Unit) {
    val amount = remember(use.value) { mutableStateOf(use.value.amount) }
    val type = remember(use.value) { mutableStateOf(use.value.reset) }
    fun create(amount: Int, type: String) = if (type == "unlimited") Use.Unlimited else Use.Limited(amount, type)
    if (type.value != "unlimited")
        EditNumber(amount, 0, 5, suffix = "/") { onUpdate(create(it, type.value)) }
    DropDown(type, listOf("unlimited", "short rest", "long rest", "day"), Modifier.width(70.dp),
        onUpdate = { onUpdate(create(amount.value, it)) })
}

private inline fun <reified E : Enum<E>> Collection<E>.toEnumSet() =
    if (isEmpty()) EnumSet.noneOf(E::class.java) else EnumSet.copyOf(this)

private fun update(abilities: MutableList<Ability>, old: Ability, new: Ability, legendaryUses: Int = 0) {
    var currentIndex = abilities.indexOf(old)
    if (currentIndex < 0) currentIndex = abilities.indexOfFirst { it is LegendaryAbility && it.ability == old }
    if (currentIndex < 0) {
        log.warn("Invalid index $currentIndex in ${abilities.joinToString(", ", "[", "]") { it.name }}")
        return
    }
    abilities[currentIndex] = if (legendaryUses > 0) LegendaryAbility(new, legendaryUses) else new
}
