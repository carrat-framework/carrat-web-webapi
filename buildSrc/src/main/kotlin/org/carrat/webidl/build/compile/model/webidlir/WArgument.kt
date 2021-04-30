package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Argument
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression
import org.carrat.webidl.build.compile.model.webidlir.value.WLiteral

data class WArgument(
    val optional: Boolean,
    val type: WTypeExpression,
    val vararg: Boolean,
    val argumentName: String,
    val defaultValue: WLiteral?
) {
    fun WidlContext.toIr(defaultValueAllowed: Boolean, irType: TypeExpression? = null): Argument {
        val irType = irType ?: type.toIr()
        val defaultValue = if (defaultValue != null) {
            if (defaultValueAllowed) {
                with(this@WArgument.defaultValue) {
                    toIr(type)
                }
            } else {
                System.err.println("Skipping default value $defaultValue")
                null
            }
        } else {
            null
        }
        return Argument(argumentName, irType, vararg, defaultValue)
    }
}
