package org.hertsig.dnd.combat.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Encounter(
    val name: String = "",
    val created: LocalDateTime = LocalDateTime.now(),
    val groups: List<String> = emptyList(),
    val singles: List<String> = emptyList(),
)
