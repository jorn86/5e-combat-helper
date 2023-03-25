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

private fun typeOf(list: List<*>): String {
    val type = singleType(list)
    return when {
        list.isEmpty() -> "List<Any> // empty"
        type == null -> "DynamicList"
        else -> "List<${type.kotlin.simpleName}>"
    }
}

private fun typeOf(map: Map<*,*>): String {
    val type = singleType(map.values)
    return when {
        map.isEmpty() -> "Map<String, Any> // empty"
        singleType(map.keys) != String::class.java -> "Any // not all keys String"
        type == null -> "Map<String, Any>"
        else -> "Map<String, ${type.kotlin.simpleName}>"
    }
}

private fun singleType(list: Iterable<*>) = list.mapNotNull { it?.javaClass }.distinct().singleOrNull()

internal fun Class<*>.isDynamicList() = DynamicList::class.java.isAssignableFrom(this)
internal fun Class<*>.isList() = List::class.java.isAssignableFrom(this)
internal fun Class<*>.isMap() = Map::class.java.isAssignableFrom(this)

internal fun Any?.displayTypeName(): String {
    val type = this?.javaClass
    return when {
        type == null -> "null"
        type.isList() -> typeOf(this as List<*>)
        type.isMap() -> typeOf(this as Map<*, *>)
        else -> type.kotlin.simpleName!!
    }
}
