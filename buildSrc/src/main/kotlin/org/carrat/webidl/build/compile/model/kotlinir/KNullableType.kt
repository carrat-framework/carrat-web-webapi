package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.TypeName
import org.carrat.webidl.build.compile.model.kotlinir.type.KType
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KNullableType(
    val memberType: KTypeExpression
) : KType() {
    override fun toPoet(): TypeName =
        memberType.toPoet().copy(true)
}
