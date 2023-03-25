package org.hertsig.magic

class DynamicList
internal constructor(data: List<DynamicEntry>): List<DynamicEntry> by data, Analyzable {
    override fun toString() = joinToString("\n", postfix = "\n")

    override fun analyze(name: String) = println(joinToString(", ", "$name: ") {
        it.data.displayTypeName()
    })

    operator fun plus(other: DynamicList): DynamicList = DynamicList(this as List<DynamicEntry> + other)
}

fun dynamicList(data: List<*>) = DynamicList(data.map { DynamicEntry(it) })

inline fun <reified T> DynamicList.getAll() = filter { it.test<T>() }.map { it.get<T>() }
inline fun <reified T> DynamicList.getAll(filter: (T) -> Boolean) =
    filter { it.test<T>() }.map { it.get<T>() }.filter(filter)

class DynamicEntry
internal constructor(val data: Any?): Analyzable {
    inline fun <reified T> test() = test(T::class.java)
    inline fun <reified T> get() = get(T::class.java)

    fun <T> test(type: Class<T>) = when {
        type == DynamicList::class.java -> data is List<*>
        type.isInterface -> data is Map<*, *>
        else -> type.isInstance(data)
    }

    fun isMap() = data is Map<*,*>
    @Suppress("UNCHECKED_CAST")
    fun <T> getMapValue(key: String) = (data as Map<String, *>)[key] as? T

    @Suppress("UNCHECKED_CAST")
    fun <T> get(type: Class<T>) = when {
        type == DynamicList::class.java -> dynamicList(data as List<*>)
        type.isInterface -> magicMap(data as Map<String, Any>, type)
        else -> data
    } as T

    override fun analyze(name: String) = data.analyze(name)

    override fun equals(other: Any?) = other is DynamicEntry && data == other.data
    override fun hashCode() = data.hashCode()
    override fun toString() = data.toString()
}
