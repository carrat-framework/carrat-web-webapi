package org.carrat.webidl.build.compile.model.ir.type

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KNativeTypes
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

abstract class NativeType : Type()

object IrString : NativeType() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression = KNativeTypes.string
}

object IrBoolean : NativeType() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression = KNativeTypes.boolean
}

object IrAny : NativeType() {
    override fun toK(`package`: KPackage, target: Target): KTypeExpression = KNativeTypes.any
}
