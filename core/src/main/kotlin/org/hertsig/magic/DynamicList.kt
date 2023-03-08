package org.hertsig.magic

class DynamicList
internal constructor(data: List<DynamicEntry>): List<DynamicEntry> by data {
    override fun toString() = joinToString("\n", postfix = "\n")
}

fun dynamicList(data: List<*>) = DynamicList(data.map { DynamicEntry(it) })

inline fun <reified T> DynamicList.getAll() = filter { it.test<T>() }.map { it.get<T>() }
fun DynamicList.analyze() = joinToString("\n", postfix = "\n") {
    it.data?.javaClass?.kotlin?.simpleName ?: "null"
}

class DynamicEntry
internal constructor(val data: Any?) {
    inline fun <reified T> test() = test(T::class.java)
    inline fun <reified T> get() = get(T::class.java)

    fun <T> test(type: Class<T>): Boolean {
        return if (type.isInterface) data is Map<*, *> else type.isInstance(data)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(type: Class<T>): T {
        return if (type.isInterface) magicMap(data as Map<String, Any>, type) else data as T
    }

    override fun equals(other: Any?) = other is DynamicEntry && data == other.data
    override fun hashCode() = data.hashCode()
    override fun toString() = data.toString()
}
