package org.hertsig.dnd.combat.dto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlin.math.max
import kotlin.math.min

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
            1 -> first.value = min(casterLevel.first, first.value + 1)
            2 -> second.value = min(casterLevel.second, second.value + 1)
            3 -> third.value = min(casterLevel.third, third.value + 1)
            4 -> fourth.value = min(casterLevel.fourth, fourth.value + 1)
            5 -> fifth.value = min(casterLevel.fifth, fifth.value + 1)
            6 -> sixth.value = min(casterLevel.sixth, sixth.value + 1)
            7 -> seventh.value = min(casterLevel.seventh, seventh.value + 1)
            8 -> eighth.value = min(casterLevel.eighth, eighth.value + 1)
            9 -> ninth.value = min(casterLevel.ninth, ninth.value + 1)
        }
    }

    fun reset(level: Int) {
        when (level) {
            1 -> first.value = max(0, first.value - 1)
            2 -> second.value = max(0, second.value - 1)
            3 -> third.value = max(0, third.value - 1)
            4 -> fourth.value = max(0, fourth.value - 1)
            5 -> fifth.value = max(0, fifth.value - 1)
            6 -> sixth.value = max(0, sixth.value - 1)
            7 -> seventh.value = max(0, seventh.value - 1)
            8 -> eighth.value = max(0, eighth.value - 1)
            9 -> ninth.value = max(0, ninth.value - 1)
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
    ;

    val display = displayOverride ?: ordinal.toString()
}
