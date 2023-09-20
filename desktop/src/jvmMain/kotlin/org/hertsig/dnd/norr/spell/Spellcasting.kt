package org.hertsig.dnd.norr.spell

import org.hertsig.magic.Magic

interface Spellcasting {
    fun name(): String
    fun headerEntries(): List<String>
    fun ability(): String
    fun will(): List<String>?
    fun daily(): DailySpells?
    fun spells(): SpellList?
    fun displayAs(): String?
}

interface SpellList {
    @Magic("0")
    fun cantrips(): SpellListLevel?
    @Magic("1")
    fun firstLevel(): SpellListLevel?
    @Magic("2")
    fun secondLevel(): SpellListLevel?
    @Magic("3")
    fun thirdLevel(): SpellListLevel?
    @Magic("4")
    fun fourthLevel(): SpellListLevel?
    @Magic("5")
    fun fifthLevel(): SpellListLevel?
    @Magic("6")
    fun sixthLevel(): SpellListLevel?
    @Magic("7")
    fun seventhLevel(): SpellListLevel?
    @Magic("8")
    fun eighthLevel(): SpellListLevel?
    @Magic("9")
    fun ninthLevel(): SpellListLevel?
}

interface SpellListLevel {
    fun slots(): Int?
    fun spells(): List<String>
}

interface DailySpells {
    @Magic("1")
    fun onePerDay(): List<String>?
    @Magic("1e")
    fun onePerDayEach(): List<String>?
    @Magic("2")
    fun twoPerDay(): List<String>?
    @Magic("2e")
    fun twoPerDayEach(): List<String>?
    @Magic("3")
    fun threePerDay(): List<String>?
    @Magic("3e")
    fun threePerDayEach(): List<String>?
}

fun DailySpells.one() = onePerDay().orEmpty() + onePerDayEach().orEmpty()
fun DailySpells.two() = twoPerDay().orEmpty() + twoPerDayEach().orEmpty()
fun DailySpells.three() = threePerDay().orEmpty() + threePerDayEach().orEmpty()

fun SpellList.all() = listOfNotNull(cantrips(), firstLevel(), secondLevel(), thirdLevel(), fourthLevel(), fifthLevel(), sixthLevel(), seventhLevel(), eighthLevel(), ninthLevel())
    .flatMap { it.spells() }
