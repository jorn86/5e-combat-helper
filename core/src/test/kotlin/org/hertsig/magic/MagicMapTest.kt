package org.hertsig.magic

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

private val simpleData = mapOf("a" to 42, "b" to "42", "c" to listOf(42), "d" to listOf("42"), "e" to mapOf("42" to "42"))

class MagicMapTest {
    @Test
    fun testSimple() {
        val magic = magicMap<SimpleTest>(simpleData)
        assertEquals(42, magic.a())
        assertEquals("42", magic.b())
        assertEquals(listOf(42), magic.c())
        assertEquals(listOf("42"), magic.d())
        assertEquals(mapOf("42" to "42"), magic.e())
    }

    @Test
    fun testKotlinDefault() {
        assertEquals("default", magicMap<DefaultTest>(emptyMap()).myDefault())
    }

    @Test
    fun testJavaDefault() {
        assertEquals("java default", magicMap<JavaDefaultTest>(emptyMap()).myDefault())
    }

    @Test
    fun testAnalyze() {
        magicMap<AnalyzableTest>(simpleData).analyze("AnalyzableTest")
    }

    @Test
    fun testToString() {
        assertEquals(simpleData.toString(), magicMap<SimpleTest>(simpleData).toString())
    }

    @Test
    fun testHashCode() {
        assertEquals(simpleData.hashCode(), magicMap<SimpleTest>(simpleData).hashCode())
    }

    @Test
    fun testEquals() {
        val magic = magicMap<SimpleTest>(simpleData)
        assertEquals(magicMap<SimpleTest>(simpleData), magic)
        assertEquals(magicMap<DefaultTest>(simpleData), magic) // can only check the map content, not the actual type
        assertNotEquals(magic, simpleData)
    }
}

private interface SimpleTest {
    fun a(): Int
    fun b(): String
    fun c(): List<Int>
    fun d(): List<String>
    fun e(): Map<String, String>
}

private interface DefaultTest {
    // This only works because of `freeCompilerArgs += "-Xjvm-default=all"` in build.gradle
    fun myDefault() = "default"
}

private interface AnalyzableTest: Analyzable
