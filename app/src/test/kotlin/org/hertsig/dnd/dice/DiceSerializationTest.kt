package org.hertsig.dnd.dice

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiceSerializationTest {
    @Test
    fun parse() {
        assertEquals((1 d 20), Dice.parse("1d20"))
        assertEquals((1 d 20) + 12, Dice.parse("1d20 + 12"))
        assertEquals((1 d 20) - 1, Dice.parse("1d20 - 1"))
        assertEquals((1 d 6)("poison"), Dice.parse("1d6 poison"))
        assertEquals(((1 d 6) + (2 d 8) + 4)("bludgeoning"), Dice.parse("1d6 + 2d8 + 4 bludgeoning"))
    }

    @Test
    fun asString() {
        assertEquals("1d20 (10)", (1 d 20).asString())
        assertEquals("1d20 + 4 (14)", ((1 d 20) + 4).asString())
        assertEquals("1d20 - 1 (9)", ((1 d 20) - 1).asString())
        assertEquals("1d6 (3) poison", ((1 d 6)("poison")).asString())
        assertEquals("1d6 + 2d8 + 4 (16) bludgeoning", ((1 d 6) + (2 d 8) + 4)("bludgeoning").asString())
    }

    @Test
    fun asStringWithoutAverage() {
        assertEquals("1d20", (1 d 20).asString(false))
        assertEquals("1d20 + 12", ((1 d 20) + 12).asString(false))
        assertEquals("1d20 - 1", ((1 d 20) - 1).asString(false))
        assertEquals("1d6 poison", ((1 d 6)("poison")).asString(false))
        assertEquals("1d6 + 2d8 + 4 bludgeoning", ((1 d 6) + (2 d 8) + 4)("bludgeoning").asString(false))
    }
}
