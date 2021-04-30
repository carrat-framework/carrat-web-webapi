package org.carrat.webidl.build.compile.model.kotlinir.type

import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage

private val kotlin = KPackage(null, "kotlin")

private fun nativeType(name: String): KTypeReference = KTypeReference(KName(kotlin, name))

object KNativeTypes {
    val string = nativeType("String")
    val boolean = nativeType("Boolean")
    val any = nativeType("Any")
    val unit = nativeType("Unit")
    val float = nativeType("Float")
    val double = nativeType("Double")
    val short = nativeType("Short")
    val uShort = nativeType("UShort")
    val int = nativeType("Int")
    val uInt = nativeType("UInt")
    val long = nativeType("Long")
    val uLong = nativeType("ULong")
    val byte = nativeType("Byte")
}
