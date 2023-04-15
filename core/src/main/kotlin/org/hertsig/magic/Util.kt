package org.hertsig.magic

internal inline fun <reified T> safeCast(value: Any?, name: String = "MagicMap"): T = safeCast(value, T::class.java, name)

@Suppress("UNCHECKED_CAST")
internal fun <T> safeCast(value: Any?, type: Class<T>, name: String = "MagicMap"): T {
    return try {
        if (type.isPrimitive) value as T else type.cast(value)
    } catch (e: ClassCastException) {
        value.analyze("UnexpectedType")
        throw IllegalArgumentException("$name wants to return ${type.simpleName}, but is ${value?.javaClass?.simpleName}", e)
    }
}

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

internal fun typeOf(map: Map<*,*>): String {
    val type = singleType(map.values)
    return when {
        map.isEmpty() -> "Map<String, Any> // empty"
        singleType(map.keys) != String::class.java -> "Any // not all keys String"
        type == null -> "Map<String, Any>"
        else -> "Map<String, ${type.kotlin.simpleName}>"
    }
}

internal fun typeOf(list: List<*>): String {
    val type = singleType(list)
    return when {
        list.isEmpty() -> "List<Any> // empty"
        type == null -> "DynamicList"
        else -> "List<${type.kotlin.simpleName}>"
    }
}

private fun singleType(list: Iterable<*>) = list.mapNotNull { it?.javaClass }.distinct().singleOrNull()
