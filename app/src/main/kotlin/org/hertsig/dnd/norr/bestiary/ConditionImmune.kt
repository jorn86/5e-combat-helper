package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.combat.element.cap
import org.hertsig.magic.Magic
import org.hertsig.magic.Mapper

@Magic(mapper = ConditionImmuneMapper::class)
interface ConditionImmune {
    fun conditionImmune(): List<String>
    fun preNote(): String?

    fun display(): String {
        val text = conditionImmune().joinToString(", ")
        return if (preNote().isNullOrBlank()) text else "${preNote()} ${text.cap()}"
    }
}

object ConditionImmuneMapper: Mapper {
    override fun invoke(value: Any?) = if (value is String) mapOf("conditionImmune" to listOf(value)) else value
}
