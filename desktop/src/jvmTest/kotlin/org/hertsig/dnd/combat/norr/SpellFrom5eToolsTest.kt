package org.hertsig.dnd.combat.norr

import org.hertsig.dnd.combat.dto.SpellSchool
import org.hertsig.dnd.combat.dto.SpellText
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.d
import org.hertsig.dnd.norr.spell.HIGHER_LEVELS
import org.hertsig.dnd.norr.spell.getSpell
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SpellFrom5eToolsTest {
    @Test
    fun testMending() {
        val spell = getSpell("Mending")!!
        assertEquals("Mending", spell.name)
        assertEquals(0, spell.level)
        assertEquals(SpellSchool.TRANSMUTATION, spell.school)
        assertEquals(listOf(
            SpellText.Text("This spell repairs a single break or tear in an object you touch, such as broken chain link, two halves of a broken key, a torn cloak, or a leaking wineskin. As long as the break or tear is no larger than 1 foot in any dimension, you mend it, leaving no trace of the former damage."),
            SpellText.Text("This spell can physically repair a magic item or construct, but the spell can't restore magic to such an object."),
        ), spell.text)
        assertEquals("1 minute", spell.time)
        assertEquals("instantaneous", spell.duration)
        assertEquals("vsm", spell.components)
        assertEquals("touch", spell.range)
        assertNull(spell.damage)
    }

    @Test
    fun testShockingGrasp() {
        val spell = getSpell("Shocking Grasp")!!
        val damage = MultiDice((1 d 8)("lightning"))

        assertEquals("Shocking Grasp", spell.name)
        assertEquals(0, spell.level)
        assertEquals(SpellSchool.EVOCATION, spell.school)
        assertEquals(listOf(
            SpellText.Text("Lightning springs from your hand to deliver a shock to a creature you try to touch. Make a melee spell attack against the target. You have advantage on the attack roll if the target is wearing armor made of metal. On a hit, the target takes 1d8 lightning damage, and it can't take reactions until the start of its next turn."),
            SpellText.Text("The spell's damage increases by d8 when you reach 5th level (2d8), 11th level (3d8), and 17th level (4d8)."),
        ), spell.text)
        assertEquals("Action", spell.time)
        assertEquals("instantaneous", spell.duration)
        assertEquals("vs", spell.components)
        assertEquals("touch", spell.range)
        assertEquals(damage, spell.damage)
    }

    @Test
    fun testBlindingSmite() {
        val spell = getSpell("Blinding Smite")!!
        val damage = MultiDice((3 d 8)("radiant"))

        assertEquals(3, spell.level)
        assertEquals(SpellSchool.EVOCATION, spell.school)
        assertEquals(listOf(
            SpellText.Text("The next time you hit a creature with a melee weapon attack during this spell's duration, your weapon flares with bright light, and the attack deals an extra 3d8 radiant damage to the target. Additionally, the target must succeed on a Constitution saving throw or be blinded until the spell ends."),
            SpellText.Text("A creature blinded by this spell makes another Constitution saving throw at the end of each of its turns. On a successful save, it is no longer blinded."),
        ), spell.text)
        assertEquals("Blinding Smite", spell.name)
        assertEquals("Bonus action", spell.time)
        assertEquals("1 minute (concentration)", spell.duration)
        assertEquals("v", spell.components)
        assertEquals("self", spell.range)
        assertEquals(damage, spell.damage)
    }

    @Test
    fun testFireball() {
        val spell = getSpell("Fireball")!!
        val damage = MultiDice((8 d 6)("fire"))

        assertEquals("Fireball", spell.name)
        assertEquals(3, spell.level)
        assertEquals(SpellSchool.EVOCATION, spell.school)
        assertEquals(listOf(
            SpellText.Text("A bright streak flashes from your pointing finger to a point you choose within range and then blossoms with a low roar into an explosion of flame. Each creature in a 20-foot-radius sphere centered on that point must make a Dexterity saving throw. A target takes 8d6 fire damage on a failed save, or half as much damage on a successful one."),
            SpellText.Text("The fire spreads around corners. It ignites flammable objects in the area that aren't being worn or carried."),
            SpellText.Text(HIGHER_LEVELS),
            SpellText.Text("When you cast this spell using a spell slot of 4th level or higher, the damage increases by 1d6 for each slot level above 3rd."),
        ), spell.text)
        assertEquals("Action", spell.time)
        assertEquals("instantaneous", spell.duration)
        assertEquals("vsm", spell.components)
        assertEquals("150 ft.", spell.range)
        assertEquals(damage, spell.damage)
    }

    @Test
    fun testConfusion() {
        val spell = getSpell("Confusion")!!
        assertEquals("Confusion", spell.name)
        assertEquals(4, spell.level)
        assertEquals(SpellSchool.ENCHANTMENT, spell.school)
        assertEquals(listOf(
            SpellText.Text("This spell assaults and twists creatures' minds, spawning delusions and provoking uncontrolled action. Each creature in a 10-foot-radius sphere centered on a point you choose within range must succeed on a Wisdom saving throw when you cast this spell or be affected by it."),
            SpellText.Text("An affected target can't take reactions and must roll a d10 at the start of each of its turns to determine its behavior for that turn."),
            SpellText.Text("(Confusion Behavior table omitted)"),
            SpellText.Text("At the end of each of its turns, an affected target can make a Wisdom saving throw. If it succeeds, this effect ends for that target."),
            SpellText.Text(HIGHER_LEVELS),
            SpellText.Text("When you cast this spell using a spell slot of 5th level or higher, the radius of the sphere increases by 5 feet for each slot level above 4th."),
        ), spell.text)
        assertEquals("Action", spell.time)
        assertEquals("1 minute (concentration)", spell.duration)
        assertEquals("vsm", spell.components)
        assertEquals("90 ft.", spell.range)
        assertNull(spell.damage)
    }

    @Test
    fun testConeOfCold() {
        val spell = getSpell("Cone of Cold")!!
        val damage = MultiDice((8 d 8)("cold"))

        assertEquals("Cone of Cold", spell.name)
        assertEquals(5, spell.level)
        assertEquals(SpellSchool.EVOCATION, spell.school)
        assertEquals(listOf(
            SpellText.Text("A blast of cold air erupts from your hands. Each creature in a 60-foot cone must make a Constitution saving throw. A creature takes 8d8 cold damage on a failed save, or half as much damage on a successful one."),
            SpellText.Text("A creature killed by this spell becomes a frozen statue until it thaws."),
            SpellText.Text(HIGHER_LEVELS),
            SpellText.Text("When you cast this spell using a spell slot of 6th level or higher, the damage increases by 1d8 for each slot level above 5th."),
        ), spell.text)
        assertEquals("Action", spell.time)
        assertEquals("instantaneous", spell.duration)
        assertEquals("vsm", spell.components)
        assertEquals("60 ft. cone", spell.range)
        assertEquals(damage, spell.damage)
    }
}
