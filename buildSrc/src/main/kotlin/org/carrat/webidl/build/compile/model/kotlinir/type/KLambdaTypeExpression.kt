package org.carrat.webidl.build.compile.model.kotlinir.type

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeName
import org.carrat.webidl.build.compile.model.kotlinir.KParameter

class KLambdaTypeExpression(
    val parameters: List<KParameter>,
    val returnType: KTypeExpression
) : KTypeExpression() {
    override fun toPoet(): TypeName {
        return LambdaTypeName.get(null, parameters.map { it.toPoet() }, returnType.toPoet())
    }
}
