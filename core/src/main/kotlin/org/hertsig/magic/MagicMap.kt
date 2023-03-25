@file:Suppress("UNCHECKED_CAST")

package org.hertsig.magic

import com.github.benmanes.caffeine.cache.Caffeine
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.javaMethod

inline fun <reified T> magicList(data: List<Map<String, Any>>) = magicList(data, T::class.java)
inline fun <reified T> magicMap(data: Map<String, Any>) = magicMap(data, T::class.java)

fun <T> magicList(data: List<Map<String, Any>>, type: Class<T>) = data.map { magicMap(it, type) }
fun <T> magicMap(data: Map<String, Any>, type: Class<T>): T {
    return Proxy.newProxyInstance(MagicHandler::class.java.classLoader, arrayOf(type, Analyzable::class.java), MagicHandler(data)) as T
}

private class MagicHandler(val data: Map<String, Any>): InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?) = when {
        method.isDefault -> InvocationHandler.invokeDefault(proxy, method, *(args.orEmpty()))
        else -> cache[method](data, args.orEmpty())
    }

    companion object {
        private fun methodHandler(method: Method): Handler<*> = when {
            method.match(Analyzable::analyze) -> AnalyzeHandler
            method.match(Any::toString) -> ToStringHandler
            method.match(Any::equals) -> EqualsHandler
            method.match(Any::hashCode) -> HashCodeHandler
            else -> parseMagicAnnotation(method)
        }

        private fun parseMagicAnnotation(method: Method): Handler<*> {
            val annotation = method.getAnnotation(Magic::class.java) ?: Magic()
            val name = annotation.name.ifEmpty { method.name }
            val returnType = method.returnType
            val mapper = annotation.mapper.instance()
            val elementType = annotation.elementType.java
            val elementMapper = elementType.getAnnotation(Magic::class.java)?.mapper?.instance() ?: DefaultMapper
            return when {
                returnType.isDynamicList() -> DynamicListHandler(name, mapper)
                returnType.isList() -> if (elementType.isInterface) ListHandler(name, mapper, elementType, elementMapper) else IdentityHandler(name, mapper, returnType)
                returnType.isMap()  -> if (elementType.isInterface)  MapHandler(name, mapper, elementType, elementMapper) else IdentityHandler(name, mapper, returnType)
                returnType.isInterface -> SingleHandler(name, returnType, mapper)
                else -> IdentityHandler(name, mapper, returnType)
            }
        }

        private val cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .build(::methodHandler)
    }
}

private typealias Handler<T> = (data: Map<String, Any>, args: Array<out Any>) -> T

private abstract class NoArgsHandler<T>: Handler<T> {
    abstract fun handle(data: Map<String, Any>): T

    override fun invoke(data: Map<String, Any>, args: Array<out Any>): T {
        checkArgs(args, 0)
        return handle(data)
    }
}

private class DynamicListHandler(val key: String, val mapper: Mapper): NoArgsHandler<DynamicList>() {
    override fun handle(data: Map<String, Any>): DynamicList {
        val value = mapper(data[key]) ?: emptyList<Any>()
        return dynamicList(safeCast(value))
    }
    override fun toString() = "${javaClass.simpleName}($key)"
}

private class ListHandler<T>(val key: String, val mapper: Mapper, val elementType: Class<T>, val elementMapper: Mapper): NoArgsHandler<List<T>>() {
    override fun handle(data: Map<String, Any>): List<T> {
        val value = mapper(data[key]) ?: return emptyList()
        return safeCast<List<Any>>(value).map { magicMap(safeCast(elementMapper(it)), elementType) }
    }
    override fun toString() = "${javaClass.simpleName}($key)"
}

private class MapHandler(val key: String, val mapper: Mapper, val elementType: Class<out Any>, val elementMapper: Mapper): NoArgsHandler<Map<String, Any>>() {
    override fun handle(data: Map<String, Any>): Map<String, Any> {
        val value = mapper(data[key]) ?: return emptyMap()
        return safeCast<Map<String, Any>>(value).mapValues {
            magicMap(safeCast(elementMapper(it.value)), elementType)
        }
    }
    override fun toString() = "${javaClass.simpleName}($key)"
}

private class SingleHandler(val key: String, val type: Class<out Any>, val mapper: Mapper): NoArgsHandler<Any?>() {
    override fun handle(data: Map<String, Any>): Any? {
        val value = mapper(data[key]) ?: return null
        return magicMap(safeCast(value), type)
    }
    override fun toString() = "${javaClass.simpleName}($key)"
}

private class IdentityHandler<T>(val key: String, val mapper: Mapper, val returnType: Class<T>): NoArgsHandler<Any?>() {
    override fun handle(data: Map<String, Any>) = safeCast(mapper(data[key]), returnType)
    override fun toString() = "${javaClass.simpleName}($key)"
}

private object ToStringHandler: NoArgsHandler<String>() {
    override fun handle(data: Map<String, Any>) = data.toString()
}

private object HashCodeHandler: NoArgsHandler<Int>() {
    override fun handle(data: Map<String, Any>) = data.hashCode()
}

private object EqualsHandler: Handler<Boolean> {
    override fun invoke(data: Map<String, Any>, args: Array<out Any>): Boolean {
        checkArgs(args, 1)
        val otherData = dataFromProxy(args.single()) ?: return false
        return data == otherData
    }
}

private object AnalyzeHandler: Handler<Unit> {
    override fun invoke(data: Map<String, Any>, args: Array<out Any>) {
        checkArgs(args, 1)
        val name = args.single() as? String ?: error("Analyze method requires a single String argument, but got ${args.single()}")
        analyze(data, name)
    }
}

private fun <T: Any> KClass<T>.instance() = objectInstance ?: createInstance()

private fun Handler<*>.checkArgs(args: Array<out Any>, size: Int) = require(args.size == size) {
    "${javaClass.simpleName} requires $size argument(s), but got ${args.contentToString()}"
}

private fun dataFromProxy(proxy: Any?): Map<String, Any>? {
    if (proxy == null) return null
    if (!Proxy.isProxyClass(proxy.javaClass)) return null
    val handler = Proxy.getInvocationHandler(proxy) as? MagicHandler ?: return null
    return handler.data
}

private fun Method.match(ref: KFunction<*>) = this == ref.javaMethod

private inline fun <reified T> Handler<*>.safeCast(value: Any?): T = safeCast(value, T::class.java)
private fun <T> Handler<*>.safeCast(value: Any?, type: Class<T>): T {
    return try {
        if (type.isPrimitive) value as T else type.cast(value)
    } catch (e: ClassCastException) {
        value.analyze("UnexpectedType")
        throw IllegalArgumentException("$this wants to return ${type.simpleName}, but is ${value?.javaClass?.simpleName}", e)
    }
}
