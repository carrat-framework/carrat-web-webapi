package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.TypeName

abstract class KType {
    abstract fun getPoetTypeName(dynamicSupported : Boolean) : TypeName
}