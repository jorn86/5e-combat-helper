package org.hertsig.dnd.dice

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiceSerializationTest {
    @Test
    fun parse() {
        assertEquals(MultiDice(Dice.NONE + 1), parse("1"))
        assertEquals(MultiDice(Dice.NONE - 3), parse("-3"))
        assertEquals(MultiDice(Dice(listOf(), 15, "bludgeoning")), parse("15 bludgeoning"))
        assertEquals(MultiDice(Dice.D20), parse("1d20"))
        assertEquals((MultiDice(Dice.D20 + 12)), parse("1d20 + 12"))
        assertEquals(MultiDice(Dice.D20 - 1), parse("1d20-1"))
        assertEquals(MultiDice((1 d 6)("poison")), parse("1d6 poison"))
        assertEquals(MultiDice(Dice(listOf(6), 4,"piercing"), Dice(listOf(8, 8),0, "poison")),
            parse("1d6+4 piercing + 2d8 poison"))
    }

    @Test
    fun asString() {
        assertEquals("-3 (-3)", Dice(listOf(), -3).asString(true))
        assertEquals("1d20 (10)", Dice.D20.asString(true))
        assertEquals("1d20 + 4 (14)", (Dice.D20 + 4).asString(true))
        assertEquals("1d20 - 1 (9)", (Dice.D20 - 1).asString(true))
        assertEquals("1d6 (3) poison", ((1 d 6)("poison")).asString(true))
        assertEquals("1d6 + 2d8 + 4 (16) bludgeoning", ((1 d 6) + (2 d 8) + 4)("bludgeoning").asString(true))
    }

    @Test
    fun asStringWithoutAverage() {
        assertEquals("-3", Dice(listOf(), -3).asString(false))
        assertEquals("1d20", Dice.D20.asString(false))
        assertEquals("1d20 + 12", (Dice.D20 + 12).asString(false))
        assertEquals("1d20 - 1", (Dice.D20 - 1).asString(false))
        assertEquals("1d6 poison", ((1 d 6)("poison")).asString(false))
        assertEquals("1d6 + 2d8 + 4 bludgeoning", ((1 d 6) + (2 d 8) + 4)("bludgeoning").asString(false))
    }
}
