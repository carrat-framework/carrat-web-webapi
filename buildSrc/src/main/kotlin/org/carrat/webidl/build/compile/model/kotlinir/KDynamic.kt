package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ClassName

object KDynamic : KType() {
    override fun getPoetTypeName(dynamicSupported : Boolean): TypeName = if(dynamicSupported) Dynamic else ClassName("kotlin", "Any").copy(true)
}