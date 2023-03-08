package org.hertsig.dnd.combat.dto

enum class Recharge(val fromValue: Int?, val display: String) {
    NO(0, ""),
    FOUR(4, "4-6"),
    FIVE(5, "5-6"),
    SIX(6, "6"),
    ;

    companion object {
        fun forValue(value: Int) = values().single { it.fromValue == value }
    }
}
