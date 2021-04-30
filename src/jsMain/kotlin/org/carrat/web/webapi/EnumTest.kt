package org.carrat.web.webapi

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public actual external interface EnumTest {
    public actual companion object
}

public actual inline val EnumTest.Companion.TEST_VALUE: EnumTest get() = "testValue".asDynamic().unsafeCast<EnumTest>()
