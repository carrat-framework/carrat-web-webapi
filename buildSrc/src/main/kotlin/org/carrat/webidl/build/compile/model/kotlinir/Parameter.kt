package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec

data class Parameter(
    val name : String?,
    val type : KType,
    val vararg : Boolean,
    val defaultValue : KValue?
) {
    fun getPoetParameterSpec(actual : Boolean, dynamicSupported : Boolean) : ParameterSpec {
        val builder = ParameterSpec.builder(name?:"", type.getPoetTypeName(dynamicSupported))
        if(vararg) {
            builder.addModifiers(KModifier.VARARG)
        }
        if(defaultValue != null && !actual) {
            builder.defaultValue(defaultValue.toPoetCodeBlock())
        }
        return builder.build()
    }
}