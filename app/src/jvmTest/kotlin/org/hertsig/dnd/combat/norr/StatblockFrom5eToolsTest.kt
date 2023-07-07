package org.hertsig.dnd.combat.norr

import org.hertsig.dnd.combat.dto.*
import org.hertsig.dnd.dice.Dice
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.d
import org.hertsig.dnd.norr.bestiary.getFromBestiary
import org.hertsig.dnd.norr.bestiary.singleType
import org.hertsig.dnd.norr.bestiary.updateStatBlock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class StatblockFrom5eToolsTest {
    @Test
    fun testSimple() {
        val actual = updateStatBlock(getFromBestiary("Riding horse")!!)
        assertEquals("Riding Horse", actual.name)
        assertEquals(Size.LARGE, actual.size)
        assertEquals("Beast", actual.type)
        assertEquals(ChallengeRating.QUARTER, actual.challengeRating)
        assertEquals(2, actual.proficiencyBonus)
        assertEquals(13, actual.maxHitPoints)
        assertEquals(16, actual.strength)
        assertEquals(10, actual.dexterity)
        assertEquals(12, actual.constitution)
        assertEquals(2, actual.intelligence)
        assertEquals(11, actual.wisdom)
        assertEquals(7, actual.charisma)
        assertEquals("10", actual.armorClass)
        assertEquals("60 ft.", actual.speed)
        assertEquals("", actual.senses)
        assertEquals("", actual.languages)
        assertEquals(emptySet<Stat>(), actual.proficientSaves)
        assertEquals(emptySet<Skill>(), actual.proficientSkills)
        assertEquals(emptySet<Skill>(), actual.expertiseSkills)
        assertEquals("", actual.conditionImmunities)
        assertEquals("", actual.damageImmunities)
        assertEquals("", actual.damageResistances)
        assertEquals(emptyList<Ability>(), actual.traits)
        assertEquals(listOf(Ability.Attack("Hooves", Stat.STRENGTH, reach = 5, damage = MultiDice((2 d 4)("bludgeoning")))), actual.actions)
        assertEquals(emptyList<Ability>(), actual.bonusActions)
        assertEquals(emptyList<Ability>(), actual.reactions)
        assertEquals(emptyList<Ability>(), actual.legendaryActions)
        assertEquals(0, actual.legendaryActionUses)
        assertEquals(emptyList<SpellcastingTrait>(), actual.spellcasting)
    }

    @Test
    fun testBonusActionOldStyle() {
        val actual = updateStatBlock(getFromBestiary("Cranium rat", "vgm")!!)
        assertEquals(listOf(
            Ability.Trait("Telepathic Shroud", description = "The cranium rat is immune to any effect that would sense its emotions or read its thoughts, as well as to all divination spells.")
        ), actual.traits)
        assertEquals(listOf(
            Ability.Attack("Bite", Stat.DEXTERITY, reach = 5, damage = MultiDice(Dice.NONE)), // can't parse type if damage isn't linked
        ), actual.actions)
        assertEquals(listOf(
            Ability.Trait("Illumination", description = "As a bonus action, the cranium rat can shed dim light from its brain in a 5-foot radius or extinguish the light."),
        ), actual.bonusActions)
    }

    @Test
    fun testBonusAction() {
        val actual = updateStatBlock(getFromBestiary("Cranium rat", "mpmm")!!)
        assertEquals(listOf(
            Ability.Trait("Telepathic Shroud", description = "The cranium rat is immune to any effect that would sense its emotions or read its thoughts, as well as to all divination spells.")
        ), actual.traits)
        assertEquals(listOf(
            Ability.Attack("Bite", Stat.DEXTERITY, reach = 5, damage = MultiDice(Dice.NONE)), // can't parse type if damage isn't linked
        ), actual.actions)
        assertEquals(listOf(
            Ability.Trait("Illumination", description = "The cranium rat sheds dim light from its exposed brain in a 5-foot radius or extinguishes the light."),
        ), actual.bonusActions)
    }

    @Test
    fun testReaction() {
        val actual = updateStatBlock(getFromBestiary("Dragon Chosen")!!)
        assertEquals(listOf(
            Ability.Trait("Multiattack", description = "The chosen makes one Handaxe attack and two Shortsword attacks."),
            Ability.Attack("Handaxe", Stat.STRENGTH, 0, 5, 20, 60, MultiDice((1 d 6)("slashing")), "Hit or Miss: The handaxe magically returns to the chosen's hand immediately after a ranged attack."),
            Ability.Attack("Shortsword", Stat.STRENGTH, reach = 5, damage = MultiDice((1 d 6)("piercing")))
        ), actual.actions)
        assertEquals(listOf(
            Ability.Trait("Biting Rebuke", description = "Immediately after the chosen takes damage from a creature within 5 feet of it, it can make a Shortsword attack with advantage against that creature.")
        ), actual.reactions)
    }

    @Test
    fun testRecharge() {
        val actual = updateStatBlock(getFromBestiary("Air Elemental")!!)
        assertEquals(listOf(
            Ability.Trait("Multiattack", description = "The elemental makes two slam attacks."),
            Ability.Attack("Slam", Stat.DEXTERITY, 0, 5, damage = MultiDice((2 d 8)("bludgeoning"))),
            Ability.Trait("Whirlwind", Recharge.FOUR, "Each creature in the elemental's space must make a DC 13 Strength saving throw. On a failure, a target takes 15 (3d8 + 2) bludgeoning damage and is flung up 20 feet away from the elemental in a random direction and knocked prone. If a thrown target strikes an object, such as a wall or floor, the target takes 3 (1d6) bludgeoning damage for every 10 feet it was thrown. If the target is thrown at another creature, that creature must succeed on a DC 13 Dexterity saving throw or take the same damage and be knocked prone. If the saving throw is successful, the target takes half the bludgeoning damage and isn't flung away or knocked prone.",
                MultiDice((3 d 8) + 2)),
        ), actual.actions)
    }

    @Test
    fun testLegendary() {
        val actual = updateStatBlock(getFromBestiary("Adult White Dragon")!!)
        assertEquals(3, actual.legendaryActionUses)
        assertEquals(listOf(
            Ability.Trait("Ice Walk", description = "The dragon can move across and climb icy surfaces without needing to make an ability check. Additionally, difficult terrain composed of ice or snow doesn't cost it extra movement."),
            Ability.Trait("Legendary Resistance", use = Use.Limited(3, "day"), description = "If the dragon fails a saving throw, it can choose to succeed instead."),
        ), actual.traits)
        assertEquals(Ability.Attack("Bite", Stat.STRENGTH, 0, 10, damage = MultiDice((2 d 10)("piercing"), (1 d 8)("cold"))), actual.actions[1])
        assertEquals(listOf(
            Ability.Trait("Detect", description = "The dragon makes a Wisdom (Perception) check.", legendaryCost = 1),
            Ability.Trait("Tail Attack", description = "The dragon makes a tail attack.", legendaryCost = 1),
            Ability.Trait("Wing Attack", description = "The dragon beats its wings. Each creature within 10 feet of the dragon must succeed on a DC 19 Dexterity saving throw or take 13 (2d6 + 6) bludgeoning damage and be knocked prone. The dragon can then fly up to half its flying speed.",
                roll = MultiDice((2 d 6) + 6), legendaryCost = 2),
        ), actual.legendaryActions)
    }

    @Test
    fun testSpellcasting() {
        val actual = updateStatBlock(getFromBestiary("Mage")!!)
        val trait = actual.spellcasting.singleType<SpellListCasting>()
        assertEquals("Spellcasting", trait.name)
        assertEquals(Stat.INTELLIGENCE, trait.stat)
        assertEquals(CasterLevel.NINE, trait.level)
        assertEquals(listOf("fire bolt", "light", "mage hand", "prestidigitation"),
            trait.spellsByLevel[0]?.map { it.name })
        assertEquals(listOf("detect magic", "mage armor", "magic missile", "shield"),
            trait.spellsByLevel[1]?.map { it.name })
        assertEquals(listOf("misty step", "suggestion"),
            trait.spellsByLevel[2]?.map { it.name })
        assertEquals(listOf("counterspell", "fireball", "fly"),
            trait.spellsByLevel[3]?.map { it.name })
        assertEquals(listOf("greater invisibility", "ice storm"),
            trait.spellsByLevel[4]?.map { it.name })
        assertEquals(listOf("cone of cold"),
            trait.spellsByLevel[5]?.map { it.name })
        trait.spellsByLevel.assertResolvable()
    }

    @Test
    fun testSpellcastingSelfOnly() {
        val actual = updateStatBlock(getFromBestiary("Warlock of the Fiend", "mpmm")!!)
        val trait = actual.spellcasting.singleType<InnateSpellcasting>()
        assertEquals("Spellcasting", trait.name)
        assertEquals(Stat.CHARISMA, trait.stat)
        val atWill = trait.spellsWithLimit[0].orEmpty()
        assertEquals(listOf("alter self", "mage armor", "mage hand", "minor illusion", "prestidigitation"), atWill.map { it.name })
        assertEquals(StatblockSpell("mage armor", "(self only)"), atWill[1])
        assertEquals(listOf("banishment", "plane shift", "suggestion"), trait.spellsWithLimit[1]?.map { it.name })
        trait.spellsWithLimit.assertResolvable()
    }

    @Test
    fun testMultipleSpellcasting() {
        val actual = updateStatBlock(getFromBestiary("Warlock of the Fiend", "vgm")!!)
        val innateTrait = actual.spellcasting.singleType<InnateSpellcasting>()
        assertEquals(Stat.CHARISMA, innateTrait.stat)
        assertEquals("Innate Spellcasting", innateTrait.name)
        assertEquals(5, innateTrait.spellsWithLimit[0]?.size)
        assertEquals(3, innateTrait.spellsWithLimit[1]?.size)
        innateTrait.spellsWithLimit.flatMap { it.value }.forEach { assertNotNull(it.resolve(), "Was ${it.name}") }

        val listTrait = actual.spellcasting.singleType<SpellListCasting>()
        assertEquals("Spellcasting", listTrait.name)
        assertEquals(Stat.CHARISMA, listTrait.stat)
        assertEquals(CasterLevel.WARLOCK_17, listTrait.level)
        // CBA to type out 17 spells
        assertEquals(7, listTrait.spellsByLevel[0]?.size)
        assertEquals(10, listTrait.spellsByLevel[5]?.size)
        listTrait.spellsByLevel.assertResolvable()
    }

    @Test
    fun testInnateSpellcasting() {
        val actual = updateStatBlock(getFromBestiary("Dragon Speaker")!!)
        assertEquals(listOf(
            Ability.Trait("Multiattack", description = "The speaker makes two Thunder Bolt attacks."),
            Ability.Attack("Thunder Bolt", Stat.CHARISMA, reach = 5, range = 60, damage = MultiDice(((3 d 8) - 3)("thunder")))
        ), actual.actions)
        assertEquals(listOf(
            Ability.Trait("Disarming Words", Recharge.NO, "When a creature the speaker can see within 60 feet of it makes a damage roll, the speaker can roll a d6 and subtract the number rolled from that damage roll.", MultiDice(1 d 6), Use.Limited(3, "day")),
        ), actual.reactions)
        val trait = actual.spellcasting.singleType<InnateSpellcasting>()
        assertEquals(Stat.CHARISMA, trait.stat)
        assertEquals(listOf("dancing lights"), trait.spellsWithLimit[0]?.map { it.name })
        assertEquals(listOf("calm emotions", "charm person", "command", "comprehend languages"),
            trait.spellsWithLimit[1]?.map { it.name })
        trait.spellsWithLimit.assertResolvable()
    }

    private fun Map<*, List<StatblockSpell>>.assertResolvable() {
        values.forEach {
            it.forEach { spell ->
                assertNotNull(spell.resolve(), "Was ${spell.name}")
            }
        }
    }
}
