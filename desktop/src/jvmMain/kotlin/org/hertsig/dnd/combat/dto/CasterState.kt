package org.hertsig.dnd.combat.dto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberCasterState(level: CasterLevel): CasterState {
    val first = remember { mutableStateOf(0) }
    val second = remember { mutableStateOf(0) }
    val third = remember { mutableStateOf(0) }
    val fourth = remember { mutableStateOf(0) }
    val fifth = remember { mutableStateOf(0) }
    val sixth = remember { mutableStateOf(0) }
    val seventh = remember { mutableStateOf(0) }
    val eighth = remember { mutableStateOf(0) }
    val ninth = remember { mutableStateOf(0) }
    return remember { CasterState(level, first, second, third, fourth, fifth, sixth, seventh, eighth, ninth) }
}

// tracks *used* slots not *available* slots
class CasterState(
    val casterLevel: CasterLevel,
    val first: MutableState<Int>,
    val second: MutableState<Int>,
    val third: MutableState<Int>,
    val fourth: MutableState<Int>,
    val fifth: MutableState<Int>,
    val sixth: MutableState<Int>,
    val seventh: MutableState<Int>,
    val eighth: MutableState<Int>,
    val ninth: MutableState<Int>
) {
    fun use(level: Int) {
        when (level) {
            1 -> first.use(casterLevel.first)
            2 -> second.use(casterLevel.second)
            3 -> third.use(casterLevel.third)
            4 -> fourth.use(casterLevel.fourth)
            5 -> fifth.use(casterLevel.fifth)
            6 -> sixth.use(casterLevel.sixth)
            7 -> seventh.use(casterLevel.seventh)
            8 -> eighth.use(casterLevel.eighth)
            9 -> ninth.use(casterLevel.ninth)
        }
    }

    fun reset(level: Int) {
        when (level) {
            1 -> first.unUse()
            2 -> second.unUse()
            3 -> third.unUse()
            4 -> fourth.unUse()
            5 -> fifth.unUse()
            6 -> sixth.unUse()
            7 -> seventh.unUse()
            8 -> eighth.unUse()
            9 -> ninth.unUse()
        }
    }

    fun resetAll() {
        first.value = 0
        second.value = 0
        third.value = 0
        fourth.value = 0
        fifth.value = 0
        sixth.value = 0
        seventh.value = 0
        eighth.value = 0
        ninth.value = 0
    }

    operator fun get(level: Int) = when (level) {
        1 -> first.value
        2 -> second.value
        3 -> third.value
        4 -> fourth.value
        5 -> fifth.value
        6 -> sixth.value
        7 -> seventh.value
        8 -> eighth.value
        9 -> ninth.value
        else -> error("Invalid spell level $level")
    }

    private fun MutableState<Int>.use(total: Int) {
        value = (value + 1).coerceAtMost(total)
    }

    private fun MutableState<Int>.unUse() {
        value = (value - 1).coerceAtLeast(0)
    }
}

enum class CasterLevel(
    val first: Int = 0,
    val second: Int = 0,
    val third: Int = 0,
    val fourth: Int = 0,
    val fifth: Int = 0,
    val sixth: Int = 0,
    val seventh: Int = 0,
    val eighth: Int = 0,
    val ninth: Int = 0,
    displayOverride: String? = null,
) {
    NONE(displayOverride = "No"),
    ONE(2),
    TWO(3),
    THREE(4, 2),
    FOUR(4, 3),
    FIVE(4, 3, 2),
    SIX(4, 3, 3),
    SEVEN(4, 3, 3, 1),
    EIGHT(4, 3, 3, 2),
    NINE(4, 3, 3, 3, 1),
    TEN(4, 3, 3, 3, 2),
    ELEVEN(4, 3, 3, 3, 2, 1),
    TWELVE(4, 3, 3, 3, 2, 1),
    THIRTEEN(4, 3, 3, 3, 2, 1, 1),
    FOURTEEN(4, 3, 3, 3, 2, 1, 1),
    FIFTEEN(4, 3, 3, 3, 2, 1, 1, 1),
    SIXTEEN(4, 3, 3, 3, 2, 1, 1, 1),
    SEVENTEEN(4, 3, 3, 3, 2, 1, 1, 1, 1),
    EIGHTEEN(4, 3, 3, 3, 3, 1, 1, 1, 1),
    NINETEEN(4, 3, 3, 3, 3, 2, 1, 1, 1),
    TWENTY(4, 3, 3, 3, 3, 2, 2, 1, 1),

    WARLOCK_01(1, displayOverride = "1"),
    WARLOCK_02(2, displayOverride = "2"),
    WARLOCK_03(0, 2, displayOverride = "3"),
    WARLOCK_05(0, 0, 2, displayOverride = "5"),
    WARLOCK_07(0, 0, 0, 2, displayOverride = "7"),
    WARLOCK_09(0, 0, 0, 0, 2, displayOverride = "9"),
    WARLOCK_11(0, 0, 0, 0, 3, displayOverride = "11"),
    WARLOCK_17(0, 0, 0, 0, 4, displayOverride = "17"),
    ;

    val display = displayOverride ?: ordinal.toString()

    operator fun get(level: Int) = when (level) {
        1 -> first
        2 -> second
        3 -> third
        4 -> fourth
        5 -> fifth
        6 -> sixth
        7 -> seventh
        8 -> eighth
        9 -> ninth
        else -> error("Invalid spell level $level")
    }

    companion object {
        val FULL = values().slice(1..20)
        val HALF = values().slice(1..10)
        val WARLOCK = values().slice(21..28)

        operator fun invoke(level: Int, warlock: Boolean = false): CasterLevel {
            require(level in (0..20)) { "No CasterLevel entry for $level" }
            if (warlock) {
                return when (level) {
                    1 -> WARLOCK_01
                    2 -> WARLOCK_02
                    in 3..4 -> WARLOCK_03
                    in 5..6 -> WARLOCK_05
                    in 7..8 -> WARLOCK_07
                    in 9..10 -> WARLOCK_09
                    in 11..16 -> WARLOCK_11
                    in 17..20 -> WARLOCK_17
                    else -> NONE
                }
            }
            return values()[level]
        }
    }
}
