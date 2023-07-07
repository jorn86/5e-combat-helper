package org.hertsig.dnd.norr.bestiary

import org.hertsig.magic.Magic

interface Skills {
    fun acrobatics(): String?
    @Magic(name = "animal handling")
    fun animalHandling(): String? // FIXME actual name?
    fun arcana(): String?
    fun athletics(): String?
    fun deception(): String?
    fun history(): String?
    fun insight(): String?
    fun intimidation(): String?
    fun investigation(): String?
    fun medicine(): String?
    fun nature(): String?
    fun perception(): String?
    fun performance(): String?
    fun persuasion(): String?
    fun religion(): String?
    @Magic(name = "sleight of hand")
    fun sleightOfHand(): String?
    fun stealth(): String?
    fun survival(): String?
}
