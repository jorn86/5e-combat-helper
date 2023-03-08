package org.hertsig.dnd.combat

import org.hertsig.dnd.norr.Template
import org.hertsig.dnd.norr.templateRegex
import org.hertsig.dnd.norr.templateValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NorrTemplateTest {
    @Test
    fun parseDC() {
        assertEquals("DC 12", "{@dc 12}".parseTemplates())
    }

    private fun String.parseTemplates(replacement: (MatchResult) -> Template = ::templateValue) =
        replace(templateRegex) { replacement(it).text }
}

