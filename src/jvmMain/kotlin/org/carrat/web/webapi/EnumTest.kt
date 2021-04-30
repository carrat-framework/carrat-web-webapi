package org.carrat.web.webapi

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public actual interface EnumTest {
    public actual companion object
}

private val aTestValue = object : EnumTest {

}
public actual val EnumTest.Companion.TEST_VALUE: EnumTest get() = aTestValue
