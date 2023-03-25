package org.hertsig.dnd.norr.spell

interface Roll {
    fun exact(): Int?
    fun min(): Int?
    fun max(): Int?

    fun display() = if (exact() != null) "${exact()}" else "${min()} - ${max()}"
}
