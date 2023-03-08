package org.hertsig.magic

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Magic(
    val name: String = "",
    val elementType: KClass<out Any> = Any::class,
    val mapper: KClass<out Mapper> = DefaultMapper::class,
)
