package org.hertsig.magic

import kotlin.reflect.KFunction

/**
 * Is added to every Magic proxy, allowing you to analyze the actual data structure
 */
interface Analyzable {
    fun analyze(name: String)
}

fun KFunction<*>.analyze() = call().analyze(name)

fun Any?.analyze(name: String) {
    when {
        this == null -> println("$name: null")
        this is Analyzable -> analyze(name)
        this is Map<*, *> -> analyze(this, name)
        this is List<*> -> println("$name: ${typeOf(this)}")
        else -> println("$name: ${javaClass.simpleName}")
    }
}

fun analyze(data: Map<*, *>, name: String) {
    println("interface $name {")
    data.forEach { (key, value) ->
        val typeName = value.displayTypeName()
        println("\tfun $key(): $typeName")
    }
    println("}")
}
