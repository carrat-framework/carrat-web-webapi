package org.carrat.webidl.build.compile.model.kotlinir.type

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.carrat.webidl.build.compile.model.kotlinir.KName

class KTypeReference(
    val name: KName,
    val parameters: List<KTypeExpression> = listOf()
) : KTypeExpression() {
    override fun toPoet(): TypeName {
        val className = name.toPoet()
        return if (parameters.isEmpty()) {
            className
        } else {
            className.parameterizedBy(parameters.map { it.toPoet() })
        }
    }
}
