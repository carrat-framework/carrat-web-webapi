package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression

data class KParameter(
    val identifier: String?,
    val type: KTypeExpression,
    val vararg: Boolean,
    val defaultValue: KValueExpression?
) {
    fun toPoet(): ParameterSpec {
        val builder = ParameterSpec.builder(identifier ?: "", type.toPoet())
        if (vararg) {
            builder.addModifiers(KModifier.VARARG)
        }
        if (defaultValue != null) {
            builder.defaultValue(defaultValue.toPoet())
        }
        return builder.build()
    }
}
