package org.hertsig.dnd.combat

import org.hertsig.dnd.norr.parseNorrTemplateText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NorrTemplateTest {
    @Test
    fun parseDC() {
         assertEquals("DC 12", "{@dc 12}".parseNorrTemplateText())
    }
}
