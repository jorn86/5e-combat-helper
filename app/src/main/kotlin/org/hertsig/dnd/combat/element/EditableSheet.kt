package org.hertsig.dnd.combat.element

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.compose.display
import org.hertsig.core.logger
import org.hertsig.dnd.combat.Page
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.element.AbilityType.*
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.parseOptional
import org.hertsig.dnd.norr.bestiary.Monster
import org.hertsig.dnd.norr.bestiary.getFromBestiary
import org.hertsig.dnd.norr.bestiary.updateStatBlock
import java.util.*

private val log = logger {}

@Composable
fun EditableSheet(state: AppState, page: Page.Edit, modifier: Modifier = Modifier) {
    val original = page.active
    val updatedState = remember(page) { page.updated }
    var updated by updatedState

    SpacedColumn(modifier.padding(8.dp)) {
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
        val casterAbility = remember { mutableStateOf(original.casterAbility ?: Stat.INTELLIGENCE) }
        val proficientSaves = remember { original.proficientSaves.toMutableStateList() }
        val proficientSkills = remember { original.proficientSkills.toMutableStateList() }
        val expertiseSkills = remember { original.expertiseSkills.toMutableStateList() }
        val abilityState = AbilityState(
            remember { original.traits.toMutableStateList() },
            remember { original.actions.toMutableStateList() },
            remember { original.bonusActions.toMutableStateList() },
            remember { original.reactions.toMutableStateList() },
            remember { original.legendaryActions.toMutableStateList() },
        )
        val legendaryActionUses = remember { mutableStateOf(original.legendaryActionUses) }

        var bestiaryEntry by remember { mutableStateOf<Monster?>(null) }
        LaunchedEffect(name.value) { bestiaryEntry = getFromBestiary(name.value) }
        fun loadFromBestiary() {
            updated = updateStatBlock(bestiaryEntry ?: return, updated)
            if (state.statBlocks.update(original, updated)) state.page = Page.Show(updated)
        }

        ScrollableFlowColumn(16.dp, 16.dp, ReorderStrategy) {
            SpacedColumn {
                FormRow("Name") {
                    BasicEditText(name, Modifier.weight(1f).autoFocus()) {
                        updated = updated.copy(name = it)
                    }
                    SmallButton(::loadFromBestiary, enabled = bestiaryEntry != null) { TextLine("Load") }
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
                    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
                        SpacedColumn(spacing = 2.dp) {
                            @Composable
                            fun EditAbilityScore(stat: Stat) {
                                BasicEditNumber(stats.getValue(stat), 0, 40, 1, 100.dp, stat.display.lowercase(), false) {
                                    updated = updated.copy(stat, it)
                                }
                            }
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                EditAbilityScore(Stat.STRENGTH)
                                EditAbilityScore(Stat.DEXTERITY)
                                EditAbilityScore(Stat.CONSTITUTION)
                            }
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                EditAbilityScore(Stat.INTELLIGENCE)
                                EditAbilityScore(Stat.WISDOM)
                                EditAbilityScore(Stat.CHARISMA)
                            }
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
                        updated = updated.copy(spellSlots = it,
                            casterAbility = if (it == CasterLevel.NONE) null else casterAbility.value)
                    }) { it.display }
                    if (casterLevel.value != CasterLevel.NONE) {
                        BasicDropdown(casterAbility, Modifier.width(100.dp), onUpdate = {
                            updated = updated.copy(casterAbility = it)
                        })
                    }
                }
                if (abilityState.legendaryActions.isNotEmpty()) {
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

            AbilityBlock(TRAITS, abilityState, updatedState)
            AbilityBlock(ACTIONS, abilityState, updatedState)
            AbilityBlock(BONUS_ACTIONS, abilityState, updatedState)
            AbilityBlock(REACTIONS, abilityState, updatedState)
            AbilityBlock(LEGENDARY_ACTIONS, abilityState, updatedState)
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
    SpacedRow(vertical = Alignment.CenterVertically) {
        RowTextLine(label, Modifier.width(160.dp))
        content()
    }
}

@Composable
private fun AbilityBlock(
    type: AbilityType,
    state: AbilityState,
    updatedState: MutableState<StatBlock>,
) {
    val abilities = state[type]

    Column {
        TextLine(type.display, style = MaterialTheme.typography.h6)
        HorizontalDivider()
        SpacedColumn(Modifier.padding(vertical = 4.dp)) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
                abilities.forEachIndexed { index, _ -> EditSingleAbility(type, index, state, updatedState) }
            }

            SpacedRow {
                val defaultCost = if (type == LEGENDARY_ACTIONS) 1 else null
                // Don't call save() yet when adding empty new abilities
                if (type != TRAITS) {
                    SmallButton({ abilities.add(Ability.Attack(legendaryCost = defaultCost)) }) { RowTextLine("Attack") }
                }
                SmallButton({ abilities.add(Ability.Trait(legendaryCost = defaultCost)) }) { RowTextLine("Trait") }
            }
        }
    }
}

@Composable
private fun EditSingleAbility(type: AbilityType, index: Int, state: AbilityState, updatedState: MutableState<StatBlock>) {
    when (val ability = state[type][index]) {
        is Ability.Attack -> EditAttack(type, index, state, ability, updatedState)
        is Ability.Trait -> EditCustom(type, index, state, ability, updatedState)
    }
}

@Composable
private fun EditAttack(type: AbilityType, index: Int, state: AbilityState, ability: Ability.Attack, updatedState: MutableState<StatBlock>) {
    val stat = remember(ability) { mutableStateOf(ability.stat) }
    val modifier = remember(ability) { mutableStateOf(ability.modifier) }
    val reach = remember(ability) { mutableStateOf(ability.reach ?: 0) }
    val range = remember(ability) { mutableStateOf(ability.range ?: 0) }
    val longRange = remember(ability) { mutableStateOf(ability.longRange ?: 0) }
    val damage = remember(ability) { mutableStateOf(ability.damage) }
    val extra = remember(ability) { mutableStateOf(ability.extra) }
    val abilities = state[type]
    var updated by updatedState
    val update = remember(type, index, abilities) {
        { it: Ability ->
            abilities[index] = it
            updated = updated.copy(type, abilities)
        }
    }

    EditAbility(type, index, state, ability, updatedState) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            SpacedRow {
                val attackStats = listOf(null, Stat.STRENGTH, Stat.DEXTERITY, Stat.INTELLIGENCE, Stat.WISDOM, Stat.CHARISMA)
                BasicDropdown(stat, attackStats, Modifier.width(90.dp), onUpdate = { update(ability.copy(stat = it)) }) { it?.display ?: "Custom" }
                RowTextLine(modifier(updated.modifierFor(stat.value, true), " +"))
                BasicEditNumber(modifier, -5, 10, width = 50.dp, suffix = "to hit") { update(ability.copy(modifier = it)) }
            }

            SpacedRow {
                RowTextLine("Reach")
                BasicEditNumber(reach, 0, 25, 5, 36.dp, "ft.") { update(ability.copy(reach = it.takeIf { it > 0 })) }
            }

            SpacedRow {
                RowTextLine("Range")
                BasicEditNumber(range, 0, 300, 5, 36.dp, "ft.") { update(ability.copy(range = it.takeIf { it > 0 })) }
                RowTextLine("/")
                BasicEditNumber(longRange, 0, 600, 5, 36.dp, "ft.") { update(ability.copy(longRange = it.takeIf { it > 0 })) }
            }
        }

        SpacedRow {
            RowTextLine("Damage")
            EditRoll(damage.value, Modifier.weight(1f)) {
                if (it != null) {
                    damage.value = it
                    update(ability.copy(damage = it))
                }
            }
        }

        SpacedRow {
            // This doesn't get any vertical space if it's not inside a row (??)
            BasicEditText(extra, Modifier.weight(1f), "Description", 5) {
                update(ability.copy(extra = it))
            }
        }
    }
}

@Composable
private fun EditCustom(type: AbilityType, index: Int, state: AbilityState, ability: Ability.Trait, updatedState: MutableState<StatBlock>) {
    val recharge = remember(ability) { mutableStateOf(ability.recharge) }
    val description = remember(ability) { mutableStateOf(ability.description) }
    val abilities = state[type]
    var updated by updatedState
    val update = remember(type, index, abilities, updatedState) {
        { it: Ability ->
            abilities[index] = it
            updated = updated.copy(type, abilities)
        }
    }

    EditAbility(type, index, state, ability, updatedState) {
        SpacedRow {
            RowTextLine("Recharge")
            BasicDropdown(recharge, Modifier.width(30.dp), onUpdate = { update(ability.copy(recharge = it)) }) { it.display }
            RowTextLine("Roll")
            EditRoll(ability.roll, Modifier.weight(1f)) { update(ability.copy(roll = it)) }
        }

        SpacedRow {
            // This doesn't get any vertical space if it's not inside a row (??)
            BasicEditText(description, Modifier.weight(1f), "Description", 5) {
                update(ability.copy(description = it))
            }
        }
    }
}

@Composable
private fun EditAbility(
    type: AbilityType,
    index: Int,
    state: AbilityState,
    ability: Ability,
    updatedState: MutableState<StatBlock>,
    content: @Composable ColumnScope.() -> Unit,
) {
    val name = remember(ability) { mutableStateOf(ability.name) }
    val use = remember(ability) { mutableStateOf(ability.use) }
    val abilities = state[type]
    var updated by updatedState
    val save: (AbilityType, List<Ability>) -> Unit = remember {
        { type, abilities -> updated = updated.copy(type, abilities) }
    }
    val update = remember(type, index, abilities, updatedState) {
        { it: Ability ->
            abilities[index] = it
            updated = updated.copy(type, abilities)
        }
    }

    SpacedRow(Modifier.padding(vertical = 8.dp), vertical = Alignment.CenterVertically) {
        Column {
            IconButton({ abilities.swap(index, index - 1); save(type, abilities) },
                Icons.Default.KeyboardArrowUp, enabled = index > 0, iconSize = 16.dp)
            IconButton({ abilities.swap(index, index + 1); save(type, abilities) },
                Icons.Default.KeyboardArrowDown, enabled = index < abilities.size - 1, iconSize = 16.dp)
        }
        SpacedColumn(Modifier.weight(1f), 2.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val modifier = if (name.value.isBlank()) Modifier.autoFocus() else Modifier
                BasicEditText(name, modifier.weight(1f), "Name") { update(ability.baseCopy(name = it)) }
                if (type == LEGENDARY_ACTIONS) {
                    val legendary = remember(ability) { mutableStateOf(ability.legendaryCost ?: 1) }
                    BasicEditNumber(legendary, 1, 5, 1, 70.dp, "action(s)") {
                        update(ability.baseCopy(legendaryCost = it))
                    }
                } else {
                    EditUse(use) { update(ability.baseCopy(use = it)) }
                }
            }
            content()
        }
        Column {
            val showMove = remember { mutableStateOf(false) }
            IconButton({ showMove.value = true }, Icons.Default.ArrowRightAlt, iconSize = 16.dp)
            val targets = remember { EnumSet.allOf(AbilityType::class.java).apply { remove(type) } }
            DropdownMenu(showMove, targets, { it.display }) {
                var toMove = abilities.removeAt(index)
                if (type == LEGENDARY_ACTIONS) toMove = toMove.baseCopy(legendaryCost = null)
                else if (it == LEGENDARY_ACTIONS) toMove = toMove.baseCopy(legendaryCost = 1)

                val targetList = state[it]
                targetList.add(toMove)
                save(type, abilities)
                save(it, targetList)
                showMove.value = false
            }
            IconButton({ abilities.removeAt(index); save(type, abilities) }, Icons.Default.Close, iconSize = 16.dp)
        }
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

inline fun <reified E : Enum<E>> Collection<E>.toEnumSet(): EnumSet<E> =
    if (isEmpty()) EnumSet.noneOf(E::class.java) else EnumSet.copyOf(this)

fun String?.cap() = orEmpty().replaceFirstChar { it.uppercase() }

private data class AbilityState(
    val traits: MutableList<Ability>,
    val actions: MutableList<Ability>,
    val bonusActions: MutableList<Ability>,
    val reactions: MutableList<Ability>,
    val legendaryActions: MutableList<Ability>,
) {
    operator fun get(type: AbilityType) = when(type) {
        TRAITS -> traits
        ACTIONS -> actions
        BONUS_ACTIONS -> bonusActions
        REACTIONS -> reactions
        LEGENDARY_ACTIONS -> legendaryActions
    }
}

private enum class AbilityType {
    TRAITS, ACTIONS, BONUS_ACTIONS, REACTIONS, LEGENDARY_ACTIONS
}

private fun StatBlock.copy(type: AbilityType, value: List<Ability>): StatBlock {
    val copy = value.toList()
    return when (type) {
        TRAITS -> copy(traits = copy)
        ACTIONS -> copy(actions = copy)
        BONUS_ACTIONS -> copy(bonusActions = copy)
        REACTIONS -> copy(reactions = copy)
        LEGENDARY_ACTIONS -> copy(legendaryActions = copy)
    }
}

private fun <T> MutableList<T>.swap(firstIndex: Int, secondIndex: Int) {
    val element = this[firstIndex]
    this[firstIndex] = this[secondIndex]
    this[secondIndex] = element
}
