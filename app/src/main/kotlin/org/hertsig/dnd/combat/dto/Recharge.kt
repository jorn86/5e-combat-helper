package org.hertsig.dnd.combat.dto

enum class Recharge(val fromValue: Int?, val display: String = "$fromValue-6") {
    NO(0, ""),
    FOUR(4),
    FIVE(5),
    SIX(6),
    ;
}
