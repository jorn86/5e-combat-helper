package org.hertsig.dnd.combat.element

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
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
import com.google.accompanist.flowlayout.FlowRow
import org.hertsig.compose.autoFocus
import org.hertsig.compose.component.*
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.compose.display
import org.hertsig.core.error
import org.hertsig.core.logger
import org.hertsig.dnd.combat.Page
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.parseOptional
import java.util.*

private val log = logger {}

@Composable
fun EditableSheet(page: Page.Edit, modifier: Modifier = Modifier) {
    val original = page.active
    page.updated = remember { mutableStateOf(original) }
    var updated by page.updated
    Column(modifier.padding(8.dp), Arrangement.spacedBy(4.dp)) {
        val name = remember { mutableStateOf(original.name) }
        val size = remember { mutableStateOf(original.size) }
        val type = remember { mutableStateOf(original.type) }
        val challengeRating = remember { mutableStateOf(original.challengeRating) }
        val proficiencyBonus = remember { mutableStateOf(original.proficiencyBonus) }
        val stats = Stat.values().associateWith { remember { mutableStateOf(original.scores[it]) } }
        val armorClass = remember { mutableStateOf(original.armorClass) }
        val maxHitPoints = remember { mutableStateOf(original.maxHitPoints) }
        val conditionImmunities = remember { mutableStateOf(original.conditionImmunities) }
        val damageImmunities = remember { mutableStateOf(original.damageImmunities) }
        val damageResistances = remember { mutableStateOf(original.damageResistances) }
        val speed = remember { mutableStateOf(original.speed) }
        val senses = remember { mutableStateOf(original.senses) }
        val languages = remember { mutableStateOf(original.languages) }
        val casterLevel = remember { mutableStateOf(original.spellSlots) }
        val casterAbility = remember { mutableStateOf(original.casterAbility) }
        val proficientSaves = remember { original.proficientSaves.toMutableStateList() }
        val proficientSkills = remember { original.proficientSkills.toMutableStateList() }
        val expertiseSkills = remember { original.expertiseSkills.toMutableStateList() }
        val traits = remember { original.traits.toMutableStateList() }
        val actions = remember { original.actions.toMutableStateList() }
        val bonusActions = remember { original.bonusActions.toMutableStateList() }
        val reactions = remember { original.reactions.toMutableStateList() }
        val legendaryActions = remember { original.legendaryActions.toMutableStateList() }
        val legendaryActionUses = remember { mutableStateOf(original.legendaryActionUses) }

        ScrollableFlowColumn(16.dp, 16.dp) {
            Column(Modifier.padding(bottom = 16.dp), Arrangement.spacedBy(4.dp)) {
                FormRow("Name") {
                    BasicEditText(name, Modifier.weight(1f).autoFocus()) { updated = updated.copy(name = it) }
                }
                FormRow("Size") {
                    BasicDropdown(size, Modifier.weight(1f), onUpdate = { updated = updated.copy(size = it) })
                }
                FormRow("Type") {
                    BasicEditText(type, Modifier.weight(1f)) { updated = updated.copy(type = it) }
                }
                FormRow("Challenge rating") {
                    BasicDropdown(challengeRating, Modifier.width(40.dp), TextAlign.End, TextAlign.Center, {
                        updated = updated.copy(challengeRating = it, proficiencyBonus = it.proficiencyBonus)
                        proficiencyBonus.value = it.proficiencyBonus
                    }) { it.display }
                    RowTextLine("(${challengeRating.value.xp} XP)")
                }
                FormRow("Proficiency bonus") {
                    BasicEditNumber(proficiencyBonus, 2, 9) { updated = updated.copy(proficiencyBonus = it) }
                }
                FormRow("Abilities") {
                    FlowRow(mainAxisSpacing = 2.dp, crossAxisSpacing = 2.dp) {
                        Stat.values().forEach { stat ->
                            EditAbilityScore(stat.display, stats.getValue(stat)) { updated = updated.copy(stat, it) }
                        }
                    }
                }
                FormRow("Armor class") {
                    BasicEditText(armorClass, Modifier.weight(1f)) { updated = updated.copy(armorClass = it) }
                }
                FormRow("Hit points") {
                    BasicEditNumber(maxHitPoints, 1, 999, 1, 40.dp) { updated = updated.copy(maxHitPoints = it) }
                }
                FormRow("Condition immunities") {
                    BasicEditText(conditionImmunities, Modifier.weight(1f)) { updated = updated.copy(conditionImmunities = it) }
                }
                FormRow("Damage immunities") {
                    BasicEditText(damageImmunities, Modifier.weight(1f)) { updated = updated.copy(damageImmunities = it) }
                }
                FormRow("Damage resistances") {
                    BasicEditText(damageResistances, Modifier.weight(1f)) { updated = updated.copy(damageResistances = it) }
                }
                FormRow("Speed") {
                    BasicEditText(speed, Modifier.weight(1f)) { updated = updated.copy(speed = it) }
                }
                FormRow("Senses") {
                    BasicEditText(senses, Modifier.weight(1f)) { updated = updated.copy(senses = it) }
                }
                FormRow("Languages") {
                    BasicEditText(languages, Modifier.weight(1f)) { updated = updated.copy(languages = it) }
                }
                FormRow("Spellcasting") {
                    BasicDropdown(casterLevel, Modifier.width(30.dp), onUpdate = {
                        updated = updated.copy(spellSlots = it)
                    }) { it.display }
                    if (casterLevel.value != CasterLevel.NONE) {
                        BasicDropdown(casterAbility, Modifier.width(100.dp), onUpdate = {
                            updated = updated.copy(casterAbility = it)
                        })
                    }
                }
                if (legendaryActions.isNotEmpty()) {
                    FormRow("Legendary actions") {
                        BasicEditNumber(legendaryActionUses, max = 5) {
                            updated = updated.copy(legendaryActionUses = it)
                        }
                    }
                }

                ProficiencyBlock("Saving throws", proficientSaves) { updated = updated.copy(proficientSaves = it) }
                ProficiencyBlock("Proficient skills", proficientSkills) { updated = updated.copy(proficientSkills = it) }
                ProficiencyBlock("Expertise skills", expertiseSkills) { updated = updated.copy(expertiseSkills = it) }
            }

            AbilityBlock("Traits", traits, false) { updated = updated.copy(traits = it) }
            AbilityBlock("Actions", actions) { updated = updated.copy(actions = it) }
            AbilityBlock("Bonus actions", bonusActions) { updated = updated.copy(bonusActions = it) }
            AbilityBlock("Reactions", reactions) { updated = updated.copy(reactions = it) }
            AbilityBlock("Legendary actions", legendaryActions, legendary = true) { updated = updated.copy(legendaryActions = it) }
        }
    }
}

@Composable
private inline fun <reified E: Enum<E>> ProficiencyBlock(
    label: String,
    proficiencies: SnapshotStateList<E>,
    noinline display: (E) -> String = { it.display },
    noinline save: (EnumSet<E>) -> Unit,
) {
    ProficiencyBlock(enumValues<E>().asList(), label, proficiencies, display) { save(it.toEnumSet()) }
}

@Composable
private fun <E: Enum<E>> ProficiencyBlock(
    values: List<E>,
    label: String,
    proficiencies: SnapshotStateList<E>,
    display: (E) -> String = { it.display },
    save: (List<E>) -> Unit,
) {
    val missingValues = values - proficiencies
    val showState = remember { mutableStateOf(false) }
    var show by showState
    FormRow(label) {
        FlowRow {
            proficiencies.forEach {
                Row(Modifier.padding(horizontal = 2.dp), Arrangement.spacedBy(2.dp)) {
                    RowTextLine(display(it))
                    IconButton({ proficiencies.remove(it); save(proficiencies) }, Icons.Default.Close, iconSize = 16.dp)
                }
            }

            IconButton({ show = true }, Icons.Default.Add, iconSize = 16.dp)
            DropdownMenu(showState, missingValues, display) { proficiencies.add(it); save(proficiencies) }
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
private fun EditAbilityScore(ability: String, state: MutableState<Int>, save: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        RowTextLine(ability, Modifier.width(90.dp)) // FIXME width is hack until we can properly table-layout this
        BasicEditNumber(state, max = 40) { save(it) }
    }
}

@Composable
private fun AbilityBlock(
    label: String,
    abilities: SnapshotStateList<Ability>,
    showAttacks: Boolean = true,
    legendary: Boolean = false,
    save: (List<Ability>) -> Unit,
) {
    Column(Modifier.padding(bottom = 16.dp)) {
        TextLine(label, style = MaterialTheme.typography.h6)
        HorizontalDivider()
        Column(Modifier.padding(vertical = 4.dp), Arrangement.spacedBy(4.dp)) {
            val defaultCost = if (legendary) 1 else null
                abilities.forEachIndexed { index, it ->
                val saveSingle: (Ability?) -> Unit = remember(abilities, index, it) {
                    {
                        if (it == null) {
                            abilities.removeAt(index)
                        } else {
                            abilities[index] = it
                        }
                        save(abilities.toList())
                    }
                }
                EditSingleAbility(it, saveSingle)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Don't call save() yet when adding empty new abilities
                if (showAttacks) {
                    Button({ abilities.add(Ability.Attack(legendaryCost = defaultCost)) }) { RowTextLine("Attack") }
                }
                Button({ abilities.add(Ability.Trait(legendaryCost = defaultCost)) }) { RowTextLine("Trait") }
            }
        }
    }
}

@Composable
private fun EditSingleAbility(ability: Ability, save: (Ability?) -> Unit) {
    when (ability) {
        is Ability.Attack -> EditAttack(ability, save)
        is Ability.Trait -> EditCustom(ability, save)
        else -> log.error{"No renderer for $ability"}
    }
}

@Composable
private fun EditAttack(ability: Ability.Attack, save: (Ability?) -> Unit) {
    val stat = remember(ability) { mutableStateOf(ability.stat) }
    val modifier = remember(ability) { mutableStateOf(ability.modifier) }
    val reach = remember(ability) { mutableStateOf(ability.reach ?: 0) }
    val range = remember(ability) { mutableStateOf(ability.range ?: 0) }
    val longRange = remember(ability) { mutableStateOf(ability.longRange ?: 0) }
    val damage = remember(ability) { mutableStateOf(ability.damage) }
    val extra = remember(ability) { mutableStateOf(ability.extra) }

    EditAbility(ability, save) {
        BasicDropdown(stat, listOf(null, Stat.STRENGTH, Stat.DEXTERITY), Modifier.width(90.dp), onUpdate = {
            save(ability.copy(stat = it))
        }) { it?.display ?: "Custom" }
        BasicEditNumber(modifier, -5, 10, width = 50.dp, suffix = "to hit") { save(ability.copy(modifier = it)) }
        // TODO nicer UI for combined melee / ranged
        TextLine("Reach")
        BasicEditNumber(reach, 0, 25, 5, 36.dp, "ft.") { save(ability.copy(reach = it.takeIf { it > 0 })) }
        TextLine("Range")
        BasicEditNumber(range, 0, 300, 5, 36.dp, "ft.") { save(ability.copy(range = it.takeIf { it > 0 })) }
        TextLine("/")
        BasicEditNumber(longRange, 0, 600, 5, 36.dp, "ft.") { save(ability.copy(longRange = it.takeIf { it > 0 })) }
        EditRoll(damage.value) {
            if (it != null) {
                damage.value = it
                save(ability.copy(damage = it))
            }
        }
        BasicEditText(extra) { save(ability.copy(extra = it)) }
    }
}

@Composable
private fun EditCustom(ability: Ability.Trait, save: (Ability?) -> Unit) {
    val recharge = remember(ability) { mutableStateOf(ability.recharge) }
    val description = remember(ability) { mutableStateOf(ability.description) }

    EditAbility(ability, save) {
        BasicDropdown(recharge, Modifier.width(30.dp), onUpdate = { save(ability.copy(recharge = it)) }) { it.display }
        EditRoll(ability.roll) { save(ability.copy(roll = it)) }
        BasicEditText(description, Modifier.fillMaxWidth(), "Description") { save(ability.copy(description = it)) }
    }
}

@Composable
private fun EditAbility(
    ability: Ability,
    save: (Ability?) -> Unit,
    flow: Boolean = true,
    content: @Composable () -> Unit
) {
    val name = remember(ability) { mutableStateOf(ability.name) }
    val use = remember(ability) { mutableStateOf(ability.use) }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        @Composable fun innerContent() {
            val modifier = if (name.value.isBlank()) Modifier.autoFocus() else Modifier
            BasicEditText(name, modifier.width(100.dp), "Name") { save(ability.baseCopy(name = it)) }
            if (ability.legendaryCost != null) {
                val legendary = remember(ability) { mutableStateOf(ability.legendaryCost!!) }
                BasicEditNumber(legendary, 1, 5) { save(ability.baseCopy(legendaryCost = it)) }
            } else {
                EditUse(use) { save(ability.baseCopy(use = it)) }
            }
            content()
        }

        if (flow) {
            FlowRow(Modifier.weight(1f), mainAxisSpacing = 4.dp, crossAxisSpacing = 2.dp) { innerContent() }
        } else {
            Row(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) { innerContent() }
        }
        IconButton({ save(null) }, Icons.Default.Close, iconSize = 16.dp)
    }
}

@Composable
private fun EditRoll(initial: MultiDice?, modifier: Modifier = Modifier, width: Dp = 200.dp, onUpdate: (MultiDice?) -> Unit) {
    val display = remember { mutableStateOf(initial?.asString(false).orEmpty()) }
    var error by remember { mutableStateOf("") }
    BasicEditText(display, modifier.width(width), "Damage") {
        error = try {
            onUpdate(parseOptional(it))
            ""
        } catch (e: RuntimeException) {
            e.message ?: "Parse error"
        }
    }
    if (error.isNotBlank()) {
        TooltipText(error) {
            Icon(Icons.Default.Error, "Parse error", Modifier.size(16.dp), MaterialTheme.colors.error)
        }
    }
}

@Composable
private fun EditUse(use: MutableState<Use>, onUpdate: (Use) -> Unit) {
    val amount = remember(use.value) { mutableStateOf(use.value.amount) }
    val type = remember(use.value) { mutableStateOf(use.value.reset) }
    fun create(amount: Int, type: String) = if (type == "unlimited") Use.Unlimited else Use.Limited(amount, type)
    if (type.value != "unlimited")
        BasicEditNumber(amount, max = 5, suffix = "/") { onUpdate(create(it, type.value)) }
    BasicDropdown(type, listOf("unlimited", "short rest", "long rest", "day"), Modifier.width(70.dp),
        onUpdate = { onUpdate(create(amount.value, it)) })
}

private inline fun <reified E : Enum<E>> Collection<E>.toEnumSet() =
    if (isEmpty()) EnumSet.noneOf(E::class.java) else EnumSet.copyOf(this)
