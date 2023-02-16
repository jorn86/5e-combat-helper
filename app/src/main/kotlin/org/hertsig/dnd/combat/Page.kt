package org.hertsig.dnd.combat

import org.hertsig.dnd.combat.dto.StatBlock

sealed interface Page {
    data class Show(val statBlock: StatBlock): Page
    data class Edit(val statBlock: StatBlock): Page
    object PrepareCombat: Page
    object Combat: Page
}
