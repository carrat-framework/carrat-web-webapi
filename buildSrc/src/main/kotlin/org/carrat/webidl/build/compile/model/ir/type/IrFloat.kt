package org.carrat.webidl.build.compile.model.ir.type

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KNativeTypes
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

object IrFloat : NativeType() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression = KNativeTypes.float
}
