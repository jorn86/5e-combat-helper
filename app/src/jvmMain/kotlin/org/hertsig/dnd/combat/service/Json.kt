package org.hertsig.dnd.combat.service

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.hertsig.dnd.combat.dto.Use
import org.hertsig.dnd.dice.MultiDice
import org.hertsig.dnd.dice.parse

internal val mapper = ObjectMapper()
    .registerModule(KotlinModule.Builder().enable(KotlinFeature.StrictNullChecks).build())
    .registerModule(SimpleModule().apply {
        customSerialization(DiceSerializer, DiceDeserializer)
        customSerialization(UseSerializer, UseDeserializer)
    })
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

private object DiceSerializer : JsonSerializer<MultiDice>() {
    override fun serialize(value: MultiDice, generator: JsonGenerator, serializers: SerializerProvider) =
        generator.writeString(value.asString(false))
}

private object DiceDeserializer : JsonDeserializer<MultiDice>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext?) = parse(parser.valueAsString)
}

private object UseSerializer : JsonSerializer<Use>() {
    override fun serialize(value: Use, generator: JsonGenerator, serializers: SerializerProvider) =
        generator.writeString(value.toString())
}

private object UseDeserializer : JsonDeserializer<Use>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext?) = Use.parse(parser.valueAsString)
}

private inline fun <reified T> SimpleModule.customSerialization(serializer: JsonSerializer<T>, deserializer: JsonDeserializer<T>) {
    addSerializer(T::class.java, serializer)
    addDeserializer(T::class.java, deserializer)
}
