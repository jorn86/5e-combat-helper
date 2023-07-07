package org.hertsig.dnd.combat

import org.hertsig.dnd.dice.d
import org.hertsig.dnd.norr.Template
import org.hertsig.dnd.norr.parseNorrTemplate
import org.hertsig.dnd.norr.parseNorrTemplateText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NorrTemplateTest {
    @Test
    fun parseDC() {
         assertEquals("a DC 12 save", "a {@dc 12} save".parseNorrTemplateText())
    }

    @Test
    fun parseDamage() {
        val (text, templates) = "deals {@damage 4d8} cold damage".parseNorrTemplate()
        assertEquals("deals 4d8 cold damage", text)
        assertEquals(Template.DamgeWithType((4 d 8)("cold")), templates.single())
    }

    @Test
    fun parseUntypedDamage() {
        val (text, templates) = "{@damage 4d8}.".parseNorrTemplate()
        assertEquals("4d8.", text)
        assertEquals(Template.DamgeWithType((4 d 8)("")), templates.single())
    }
}
