@file:Suppress("UNCHECKED_CAST")

package org.hertsig.magic

import com.github.benmanes.caffeine.cache.Caffeine
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

inline fun <reified T> magicMap(data: Map<String, Any>) = magicMap(data, T::class.java)
inline fun <reified T> magicList(data: List<Map<String, Any>>) = magicList(data, T::class.java)

fun <T> magicList(data: List<Map<String, Any>>, type: Class<T>) = data.map { magicMap(it, type) }
fun <T> magicMap(data: Map<String, Any>, type: Class<T>): T {
    return Proxy.newProxyInstance(MagicHandler::class.java.classLoader, arrayOf(type), MagicHandler(data)) as T
}

private class MagicHandler(private val data: Map<String, Any>): InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?) = when {
        method.isDefault -> InvocationHandler.invokeDefault(proxy, method, *(args.orEmpty()))
        method.name == "analyze" -> analyze(data, single(args))
        else -> { none(args); cache.get(method)(data) }
    }

    private fun none(args: Array<*>?) = require(args == null) { "Method requires no arguments" }

    private inline fun <reified T> single(args: Array<out Any>?): T {
        val arg = args?.singleOrNull() ?: error("Method requires a single argument")
        return arg as? T ?: error("Method requires argument of type ${T::class.simpleName}")
    }

    companion object {
        private fun methodHandler(method: Method): Handler {
            if (method.name == "toString") return ToStringHandler
            val annotation = method.getAnnotation(Magic::class.java) ?: Magic()
            val name = annotation.name.ifEmpty { method.name }
            val returnType = method.returnType
            val elementType = annotation.elementType.java
            val elementMapper = elementType.getAnnotation(Magic::class.java)?.mapper?.instance() ?: DefaultMapper
            val mapper = annotation.mapper.instance()
            return when {
                returnType.isDynamicList() -> DynamicListHandler(name, mapper, elementType)
                returnType.isList() -> if (elementType.isInterface) ListHandler(name, mapper, elementType, elementMapper) else IdentityHandler(name, mapper)
                returnType.isMap() -> if (elementType.isInterface) MapHandler(name, mapper, elementType, elementMapper) else IdentityHandler(name, mapper)
                returnType.isInterface -> SingleHandler(name, returnType, mapper)
                else -> IdentityHandler(name, mapper) // TODO check return type
            }
        }

        private val cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .build(::methodHandler)
    }
}

private typealias Handler = (data: Map<String, Any>) -> Any?

private class DynamicListHandler(val key: String, val mapper: Mapper, val elementType: Class<out Any>): Handler {
    override fun invoke(data: Map<String, Any>): Any {
        val value = mapper(data[key]) ?: emptyList<Any>()
        return dynamicList(value as List<*>)
    }
}

private class ListHandler(val key: String, val mapper: Mapper, val elementType: Class<out Any>, val elementMapper: Mapper): Handler {
    override fun invoke(data: Map<String, Any>): Any {
        val value = mapper(data[key]) ?: emptyList<Any>()
        return (value as List<Any>).map { magicMap(elementMapper(it) as Map<String, Any>, elementType) }
    }
}

private class MapHandler(val key: String, val mapper: Mapper, val elementType: Class<out Any>, val elementMapper: Mapper): Handler {
    override fun invoke(data: Map<String, Any>): Any {
        val value = mapper(data[key]) ?: emptyMap<String, Any>()
        return (value as Map<String, Any>).mapValues { magicMap(elementMapper(it.value) as Map<String, Any>, elementType) }
    }
}

private class SingleHandler(val key: String, val type: Class<out Any>, val mapper: Mapper): Handler {
    override fun invoke(data: Map<String, Any>): Any? {
        val value = mapper(data[key]) ?: return null
        return magicMap(value as Map<String, Any>, type)
    }
}

private object ToStringHandler: Handler {
    override fun invoke(data: Map<String, Any>) = data.toString()
}

private class IdentityHandler(val key: String, val mapper: Mapper): Handler {
    override fun invoke(data: Map<String, Any>) = mapper(data[key])
}

fun analyze(data: Map<String, Any>, name: String) {
    println("interface $name {")
    data.forEach { (key, value) ->
        val type = value.javaClass
        val typeName = when {
            type.isList() -> "List<Any>"
            type.isMap() -> "Map<String, Any>"
            else -> type.kotlin.simpleName
        }
        println("\tfun $key(): $typeName")
    }
    println("}")
}

private fun Class<*>.isDynamicList() = DynamicList::class.java.isAssignableFrom(this)
private fun Class<*>.isList() = List::class.java.isAssignableFrom(this)
private fun Class<*>.isMap() = Map::class.java.isAssignableFrom(this)
private fun <T: Any> KClass<T>.instance() = objectInstance ?: createInstance()
