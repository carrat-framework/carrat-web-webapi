package org.carrat.webidl.build.compile.model.ir.type

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KNullableType
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KDynamic
import org.carrat.webidl.build.compile.model.kotlinir.type.KNativeTypes
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

object IrDynamic : NativeType() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression {
        return when (target) {
            Target.JS -> KDynamic
            else -> KNullableType(KNativeTypes.any)
        }
    }
}
