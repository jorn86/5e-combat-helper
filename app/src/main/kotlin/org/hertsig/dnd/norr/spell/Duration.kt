package org.hertsig.dnd.norr.spell

interface Duration {
    fun display(): String {
        if (type() == "instant") return "instantaneous"
        var text = duration()!!.display()
        if (concentration() == true) text += " (concentration)"
        return text
    }

    fun type(): String
    fun duration(): Amount?
    fun concentration(): Boolean?
}
