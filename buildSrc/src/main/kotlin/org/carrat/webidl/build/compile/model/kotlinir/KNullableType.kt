package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.TypeName

class KNullableType(
    val memberType : NamedType
) : KType() {
    override fun getPoetTypeName(dynamicSupported : Boolean): TypeName = memberType.getPoetTypeName(dynamicSupported).copy(true)
}