package org.hertsig.dnd.combat

import org.hertsig.dnd.combat.dto.CasterLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CasterLevelTest {
    @Test
    fun none() {
        assertEquals(CasterLevel.NONE, CasterLevel(0))
    }

    @Test
    fun some() {
        assertEquals(CasterLevel.ONE, CasterLevel(1))
        assertEquals(CasterLevel.TEN, CasterLevel(10))
        assertEquals(CasterLevel.TWENTY, CasterLevel(20))
    }

    @Test
    fun warlock() {
        assertEquals(CasterLevel.WARLOCK_01, CasterLevel(1, true))
        assertEquals(CasterLevel.WARLOCK_07, CasterLevel(8, true))
        assertEquals(CasterLevel.WARLOCK_17, CasterLevel(20, true))
    }
}