package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.TypeSpec

abstract class MemberDeclaration {
    abstract fun addTo(builder : TypeSpec.Builder, dynamicSupported : Boolean, isObject : Boolean = false)
}