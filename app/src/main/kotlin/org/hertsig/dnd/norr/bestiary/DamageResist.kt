package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.combat.element.cap
import org.hertsig.magic.DynamicList
import org.hertsig.magic.Magic
import org.hertsig.magic.getAll

interface DamageResist {
    @Magic(elementType = DamageResist::class)
    fun resist(): DynamicList
    @Magic(elementType = DamageResist::class)
    fun immune(): DynamicList
    fun preNote(): String?
    fun note(): String?
    fun cond(): Boolean?

    fun display() = displayDamageResist(resist().ifEmpty { immune() }, preNote(), note())
}

fun displayDamageResist(resist: DynamicList, preNote: String? = null, note: String? = null): String {
    val content = makeString(
        resist.getAll<String>().joinToString(", ").cap(),
        resist.getAll<DamageResist>().joinToString(", ") { it.display() }.cap(),
    ).joinToString("; ")
    if (content.isBlank()) return ""
    return makeString(preNote, content, note).joinToString(" ")
}

private fun makeString(vararg text: String?) = listOf(*text).filter { !it.isNullOrBlank() }
