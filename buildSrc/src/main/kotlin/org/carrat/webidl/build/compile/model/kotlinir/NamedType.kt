package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

data class NamedType(
    val packageName: String,
    val name: String,
    val typeParameters: List<KType>
) : KType() {
    constructor(
        packageName: String,
        name: String,
        vararg typeParameters: KType
    ) : this(packageName, name, typeParameters.toList())

    override fun getPoetTypeName(dynamicSupported : Boolean): TypeName {
        val className = ClassName(packageName, name)
        return if(typeParameters.isNotEmpty()) {
            className.parameterizedBy(typeParameters.map{it.getPoetTypeName(dynamicSupported)})
        } else {
            className
        }
    }
}