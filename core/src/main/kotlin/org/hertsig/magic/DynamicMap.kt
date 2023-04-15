package org.hertsig.magic

class DynamicMap(val data: Map<String, Any>): Analyzable {
    fun string(key: String) = safeCast<String>(data[key])
    fun int(key: String) = safeCast<Int>(data[key])
    fun <T> list(key: String) = safeCast<List<T>>(data[key])
    fun wrapList(key: String) = list<Map<String, Any>>(key).map { DynamicMap(it) }
    fun dynamicList(key: String) = dynamicList(list<Any>(key))
    fun <T> map(key: String) = safeCast<Map<String, T>>(data[key])
    fun wrapMap(key: String) = DynamicMap(map(key))

    override fun analyze(name: String) = analyze(data, name)
}
