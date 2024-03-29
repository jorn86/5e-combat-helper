package org.hertsig.dnd.combat.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.hertsig.compose.component.*
import org.hertsig.compose.component.flow.ReorderStrategy
import org.hertsig.compose.component.flow.ScrollableFlowColumn
import org.hertsig.compose.util.autoFocus
import org.hertsig.dnd.combat.Page
import org.hertsig.dnd.combat.component.modifier
import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.combat.dto.SpellList.*
import org.hertsig.dnd.combat.element.AbilityType.*
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.parseOptional
import org.hertsig.dnd.norr.bestiary.Monster
import org.hertsig.dnd.norr.bestiary.getAllFromBestiary
import org.hertsig.dnd.norr.bestiary.updateStatBlock
import org.hertsig.dnd.norr.spell.findNorrSpells
import org.hertsig.util.display
import org.hertsig.util.nullsFirst
import org.hertsig.util.sortedBy
import java.util.*

@Composable
fun EditableSheet(state: AppState, page: Page.Edit, modifier: Modifier = Modifier) {
    val original = page.active
    val updatedState = remember(page) { page.updated }
    var updated by updatedState

    SpacedColumn(modifier.padding(8.dp)) {
        val name = remember { mutableStateOf(original.name) }
        val image = remember { mutableStateOf(original.image.orEmpty()) }
        val size = remember { mutableStateOf(original.size) }
        val type = remember { mutableStateOf(original.type) }
        val challengeRating = remember { mutableStateOf(original.challengeRating) }
        val proficiencyBonus = remember { mutableStateOf(original.proficiencyBonus) }
        val stats = Stat.entries.associateWith { remember { mutableStateOf(original.scores[it]) } }
        val armorClass = remember { mutableStateOf(original.armorClass) }
        val maxHitPoints = remember { mutableStateOf(original.maxHitPoints) }
        val conditionImmunities = remember { mutableStateOf(original.conditionImmunities) }
        val damageImmunities = remember { mutableStateOf(original.damageImmunities) }
        val damageResistances = remember { mutableStateOf(original.damageResistances) }
        val speed = remember { mutableStateOf(original.speed) }
        val senses = remember { mutableStateOf(original.senses) }
        val languages = remember { mutableStateOf(original.languages) }
        val spellcasting = remember { original.spellcasting.toMutableStateList() }
        val proficientSaves = remember { original.proficientSaves.toMutableStateList() }
        val proficientSkills = remember { original.proficientSkills.toMutableStateList() }
        val expertiseSkills = remember { original.expertiseSkills.toMutableStateList() }
        val abilityState = AbilityState(
            remember { original.traits.toMutableStateList() },
            remember { original.actions.toMutableStateList() },
            remember { original.bonusActions.toMutableStateList() },
            remember { original.reactions.toMutableStateList() },
            remember { original.legendaryActions.toMutableStateList() },
            remember { original.lairActions.toMutableStateList() },
        )
        val legendaryActionUses = remember { mutableStateOf(original.legendaryActionUses) }
        var unique by remember { mutableStateOf(original.unique) }

        var bestiaryEntries by remember { mutableStateOf<Map<String, Monster>>(mapOf()) }
        LaunchedEffect(name.value) { bestiaryEntries = getAllFromBestiary(name.value) }
        fun loadFromBestiary(monster: Monster) {
            updated = updateStatBlock(monster, updated)
            if (state.statBlocks.update(original, updated)) state.page = Page.Show(updated)
        }

        ScrollableFlowColumn(16.dp, 16.dp, ReorderStrategy) {
            SpacedColumn {
                FormRow("Name") {
                    BasicEditText(name, Modifier.weight(1f).autoFocus()) {
                        updated = updated.copy(name = it)
                    }
                    Checkbox(unique, { unique = it; updated = updated.copy(unique = it)})
                    TextLine("Unique")

                    val showBestiaryDropdown = remember { mutableStateOf(false) }
                    SmallButton({
                        if (bestiaryEntries.size == 1) loadFromBestiary(bestiaryEntries.values.single())
                        else showBestiaryDropdown.value = true
                    }, enabled = bestiaryEntries.isNotEmpty()) {
                        TextLine("Load")
                        DropdownMenu(showBestiaryDropdown, bestiaryEntries.keys,) {
                            loadFromBestiary(bestiaryEntries.getValue(it))
                        }
                    }
                }
                FormRow("Image") {
                    BasicEditText(image, Modifier.weight(1f)) {
                        updated = updated.copy(image = it.takeIf { it.isNotBlank() })
                    }
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
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
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

            SpellcastingBlock(spellcasting, updatedState)
            AbilityBlock(TRAITS, abilityState, updatedState)
            AbilityBlock(ACTIONS, abilityState, updatedState)
            AbilityBlock(BONUS_ACTIONS, abilityState, updatedState)
            AbilityBlock(REACTIONS, abilityState, updatedState)
            AbilityBlock(LEGENDARY_ACTIONS, abilityState, updatedState)
            AbilityBlock(LAIR_ACTIONS, abilityState, updatedState)
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
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                proficiencies.forEach {
                    ProficiencyEntry(display(it)) { proficiencies.remove(it); save(proficiencies) }
                }
            }
            IconButton({ show = true }, Icons.Default.Add, iconSize = 12.dp)
            DropdownMenu(showState, missingValues, display) {
                proficiencies.add(it)
                save(proficiencies)
            }
        }
    }
}

@Composable
private fun ProficiencyEntry(text: String, action: () -> Unit) {
    Row(Modifier.background(Color.LightGray.copy(alpha = .2f), CircleShape).padding(horizontal = 4.dp),
        Arrangement.spacedBy(2.dp),
        Alignment.CenterVertically
    ) {
        RowTextLine(text)
        IconButton(action, Icons.Default.Close, iconSize = 14.dp)
    }
}

@Composable
private fun FormRow(
    label: String,
    labelWidh: Dp = 160.dp,
    labelAlign: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    SpacedRow(vertical = Alignment.CenterVertically) {
        RowTextLine(label, Modifier.width(labelWidh), labelAlign = labelAlign)
        content()
    }
}

@Composable
private fun SpellcastingBlock(traits: MutableList<SpellcastingTrait>, updatedState: MutableState<StatBlock>) {
    Column {
        if (traits.isEmpty()) {
            TextLine("Spellcasting", style = MaterialTheme.typography.titleLarge)
            HorizontalDivider()
        }
        traits.forEachIndexed { index, it ->
            TextLine(it.name, style = MaterialTheme.typography.titleLarge)
            HorizontalDivider()
            SpacedColumn {
                when (it) {
                    is InnateSpellcasting -> EditInnateSpellcasting(traits, it, index, updatedState)
                    is SpellListCasting -> EditSpellListCasting(traits, it, index, updatedState)
                }
            }
        }
        SpacedRow {
            SmallButton({ traits.add(SpellListCasting("Spellcasting", WIZARD, CasterLevel.ONE, mapOf())) }) {
                RowTextLine("Spell list")
            }
            SmallButton({ traits.add(InnateSpellcasting("Innate spellcasting", Stat.CHARISMA, mapOf())) }) {
                RowTextLine("Innate")
            }
        }
    }
}

@Composable
private fun EditInnateSpellcasting(traits: MutableList<SpellcastingTrait>, trait: InnateSpellcasting, index: Int, updatedState: MutableState<StatBlock>) {
    val state = InnateSpellcastingState(
        remember { mutableStateOf(trait.name) },
        remember { mutableStateOf(trait.stat) },
        remember { trait.spellsWithLimit[0].orEmpty().toMutableStateList() },
        remember { trait.spellsWithLimit[3].orEmpty().toMutableStateList() },
        remember { trait.spellsWithLimit[2].orEmpty().toMutableStateList() },
        remember { trait.spellsWithLimit[1].orEmpty().toMutableStateList() },
    )

    var updated by updatedState
    val update = remember(traits, index) { { it: SpellcastingTrait ->
        traits[index] = it
        updated = updated.copy(spellcasting = traits.toList())
    } }
    FormRow("Name") {
        BasicEditText(state.name) { update(trait.copy(name = it)) }
    }
    FormRow("Spellcasting ability") {
        BasicDropdown(state.stat, onUpdate = { update(trait.copy(stat = it)) })
    }
    EditSpellList(innateLabel(0), state.atWill) { update(trait.copy(spellsWithLimit = trait.spellsWithLimit.update(0, it))) }
    EditSpellList(innateLabel(3), state.threePerDay) { update(trait.copy(spellsWithLimit = trait.spellsWithLimit.update(3, it))) }
    EditSpellList(innateLabel(2), state.twoPerDay) { update(trait.copy(spellsWithLimit = trait.spellsWithLimit.update(2, it))) }
    EditSpellList(innateLabel(1), state.onePerDay) { update(trait.copy(spellsWithLimit = trait.spellsWithLimit.update(1, it))) }
}

@Composable
private fun EditSpellListCasting(traits: MutableList<SpellcastingTrait>, trait: SpellListCasting, index: Int, updatedState: MutableState<StatBlock>) {
    val state = SpellListCastingState(
        remember { mutableStateOf(trait.name) },
        remember { mutableStateOf(trait.list) },
        remember { mutableStateOf(trait.level) },
        remember { trait.spellsByLevel.values.flatten().toMutableStateList() },
    )
    var updated by updatedState
    val update = remember(traits, index) { { it: SpellcastingTrait ->
        traits[index] = it
        updated = updated.copy(spellcasting = traits.toList())
    } }
    FormRow("Class & level") {
        BasicDropdown(state.list, onUpdate = {
            val listUpdated = trait.updateList(it)
            update(listUpdated)
            state.level.value = listUpdated.level
        })
        val levels = when (state.list.value) {
            WARLOCK -> CasterLevel.WARLOCK
            RANGER, PALADIN, ARTIFICER -> CasterLevel.HALF
            else -> CasterLevel.FULL
        }
        BasicDropdown(state.level, levels, onUpdate = {
            update(trait.copy(level = it))
        }) { it.display }
    }
    EditSpellList("Spells known", state.spells) {
        update(trait.copy(spellsByLevel = it.orEmpty().groupBy { it.resolved!!.level }.toSortedMap()))
    }
}

@Composable
private fun EditSpellList(title: String, spells: MutableList<StatblockSpell>, update: (List<StatblockSpell>?) -> Unit) {
    FormRow(title, labelAlign = Alignment.Top) {
        SpacedColumn {
            spells.forEachIndexed { index, it ->
                EditSpell(spells, index, it, update)
            }
            SpacedRow {
                Autocompleter(
                    { if (it.isBlank()) emptyList() else findNorrSpells(it.trim()) },
                    Modifier.weight(1f),
                    "Add new spell",
                ) { name ->
                    spells.add(StatblockSpell(name))
                    spells.sortedBy(Spell.order.nullsFirst()) { it.resolved }
                    update(spells)
                }
            }
        }
    }
}

@Composable
private fun EditSpell(
    spells: MutableList<StatblockSpell>,
    index: Int,
    spell: StatblockSpell,
    update: (List<StatblockSpell>) -> Unit,
) {
    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
        SpacedRow {
            val comment = remember(spell) { mutableStateOf(spell.comment) }
            SpellDisplay(spell.resolved!!, tooltipModifier = Modifier.weight(1f))
            BasicEditText(comment, Modifier.weight(1f), "(comment)") { spells[index] = spell.copy(comment = it); update(spells) }
            IconButton({ spells.removeAt(index); update(spells) }, Icons.Default.Close, iconSize = 16.dp)
        }
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
        TextLine(type.display, style = MaterialTheme.typography.headlineSmall)
        HorizontalDivider()
        SpacedColumn(Modifier.padding(vertical = 4.dp)) {
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                abilities.forEachIndexed { index, _ -> EditSingleAbility(type, index, state, updatedState) }
            }

            SpacedRow {
                val defaultCost = if (type == LEGENDARY_ACTIONS) 1 else null
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
    val update = remember(type, index, abilities) { { it: Ability ->
            abilities[index] = it
            updated = updated.copy(type, abilities)
    } }

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
            Icon(Icons.Default.Error, "Parse error", Modifier.size(16.dp), MaterialTheme.colorScheme.error)
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

private fun <T> MutableList<T>.swap(firstIndex: Int, secondIndex: Int) {
    val element = this[firstIndex]
    this[firstIndex] = this[secondIndex]
    this[secondIndex] = element
}

private fun <K, V> Map<K, List<V>>.update(key: K, value: List<V>?): Map<K, List<V>> {
    val result = this as? MutableMap<K, List<V>> ?: toMutableMap()
    if (value.isNullOrEmpty()) result.remove(key) else result[key] = value
    return result
}
