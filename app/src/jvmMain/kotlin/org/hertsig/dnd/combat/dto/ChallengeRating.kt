package org.hertsig.dnd.combat.dto

enum class ChallengeRating(val value: Float, val xp: Int, val display: String) {
    NONE(0f, 0, "—"),
    ZERO(0, 10),
    EIGHTH(1/8f, 25, "⅛"),
    QUARTER(1/4f, 50, "¼"),
    HALF(1/2f, 100, "½"),
    ONE(1, 200),
    TWO(2, 450),
    THREE(3, 700),
    FOUR(4, 1100),
    FIVE(5, 1800),
    SIX(6, 2300),
    SEVEN(7, 2900),
    EIGHT(8, 3900),
    NINE(9, 5000),
    TEN(10, 5900),
    ELEVEN(11, 7200),
    TWELVE(12, 8400),
    THIRTEEN(13, 10_000),
    FOURTEEN(14, 11_500),
    FIFTEEN(15, 13_000),
    SIXTEEN(16, 15_000),
    SEVENTEEN(17, 18_000),
    EIGHTEEN(18, 20_000),
    NINETEEN(19, 22_000),
    TWENTY(20, 25_000),
    TWENTY_ONE(21, 33_000),
    TWENTY_TWO(22, 41_000),
    TWENTY_THREE(23, 50_000),
    TWENTY_FOUR(24, 62_000),
    TWENTY_FIVE(25, 75_000),
    TWENTY_SIX(26, 90_000),
    TWENTY_SEVEN(27, 105_000),
    TWENTY_EIGHT(28, 120_000),
    THIRTY(30, 155_000),
    ;

    constructor(value: Int, xp: Int): this(value.toFloat(), xp, value.toString())

    companion object {
        operator fun invoke(value: Int) = values().last { it.value == value.toFloat() }
        operator fun invoke(value: String) = when (value) {
            "1/8" -> EIGHTH
            "1/4" -> QUARTER
            "1/2" -> HALF
            else -> invoke(value.toInt())
        }
    }
}

val ChallengeRating.proficiencyBonus get() = (value.toInt() - 1) / 4 + 2
val StatBlock.xp get() = challengeRating.xp
