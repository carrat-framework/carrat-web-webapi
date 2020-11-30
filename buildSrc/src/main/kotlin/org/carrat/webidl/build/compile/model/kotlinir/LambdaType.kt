package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.LambdaTypeName

class LambdaType(
    val parameters: List<Parameter>,
    val returnType: KType
) : KType() {
    override fun getPoetTypeName(dynamicSupported: Boolean): LambdaTypeName {
        return LambdaTypeName.get(
            null,
            parameters.map { it.getPoetParameterSpec(false, dynamicSupported) },
            returnType.getPoetTypeName(dynamicSupported)
        )
    }
}