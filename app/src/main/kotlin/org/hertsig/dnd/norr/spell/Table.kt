package org.hertsig.dnd.norr.spell

import org.hertsig.magic.DynamicList

interface Table {
    fun isTable() = type() == "table"

    fun type(): String
    fun caption(): String
    fun colLabels(): List<String>
    fun colStyles(): List<String>
//    @Magic(elementType = ) // TODO support List<List<Cell>>
    fun rows(): DynamicList
}

interface Cell {
    fun isCell() = type() == "cell"

    fun type(): String
    fun roll(): Roll
}
