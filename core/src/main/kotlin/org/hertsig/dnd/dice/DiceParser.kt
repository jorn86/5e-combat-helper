package org.hertsig.dnd.dice

fun parseOptional(string: String): MultiDice? {
    if (string.isBlank()) return null
    return parse(string)
}

private val tokenSeparator = Regex("(?<=[+-])|(?=[\\s+-])\\s*")
fun parse(string: String): MultiDice {
    val tokens = string.split(tokenSeparator).mapNotNull { parseToken(it.trim()) }
    val iterator = tokens.iterator()
    var current = Dice.NONE
    var total = MultiDice(current)
    var sign: DieToken? = DieToken.Plus

    while (iterator.hasNext()) {
        val token = iterator.next()
        if (sign == null) {
            when (token) {
                DieToken.Plus -> sign = token
                DieToken.Minus -> sign = token
                is DieToken.Type -> {
                    total += current(token.text)
                    current = Dice.NONE
                }
                else -> error("Expected sign or type token, but got $token")
            }
        } else {
            when (token) {
                is DieToken.Dice -> {
                    check(sign is DieToken.Plus)
                    current += (token.amount d token.size)
                    sign = null
                }
                is DieToken.Modifier -> {
                    if (sign is DieToken.Plus) current += token.modifier else current -= token.modifier
                    sign = null
                }
                else -> error("Expected dice or modifier token, but got $token")
            }
        }
    }
    if (current.isRelevant()) total += current
    return total
}

private val dice = Regex("(\\d+)?d(\\d+)")
private val modifier = Regex("(\\d+)")
private fun parseToken(text: String): DieToken? {
    if (text.isBlank()) return null
    else if (text == "+") return DieToken.Plus
    else if (text == "-") return DieToken.Minus

    val diceMatch = dice.matchEntire(text)
    if (diceMatch != null) return DieToken.Dice(diceMatch.groupValues[1].toIntOrNull() ?: 1, diceMatch.groupValues[2].toInt())

    val modifierMatch = modifier.matchEntire(text)
    if (modifierMatch != null) return DieToken.Modifier(modifierMatch.groupValues[1].toInt())

    return  DieToken.Type(text)
}

private sealed interface DieToken {
    object Plus: DieToken { override fun toString() = "Plus" }
    object Minus: DieToken { override fun toString() = "Minus" }
    data class Dice(val amount: Int, val size: Int): DieToken
    data class Modifier(val modifier: Int): DieToken
    data class Type(val text: String): DieToken
}
