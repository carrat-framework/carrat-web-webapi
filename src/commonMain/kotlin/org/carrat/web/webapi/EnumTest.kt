package org.carrat.web.webapi

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public expect interface EnumTest {
    public companion object
}

public expect val EnumTest.Companion.TEST_VALUE: EnumTest
