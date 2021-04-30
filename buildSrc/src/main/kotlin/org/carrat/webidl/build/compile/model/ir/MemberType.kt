package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.ir.type.LambdaTypeExpression
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.*
import org.carrat.webidl.build.compile.model.kotlinir.value.KDefinedExternally
import org.carrat.webidl.build.compile.model.kotlinir.value.KJsOnly

sealed class MemberType {
    abstract fun toK(`package`: KPackage, abstract: Boolean, target: Target): KMemberType
}

class MethodType(
    val type: LambdaTypeExpression
) : MemberType() {
    override fun toK(`package`: KPackage, abstract: Boolean, target: Target): KMemberType {
        val kBody = if (!abstract) {
            when (target) {
                Target.COMMON -> null
                Target.JS -> KDefinedExternally
                Target.OTHER -> KJsOnly
            }
        } else {
            null
        }
        return KMethodType(
            type.toK(`package`, target),
            kBody
        )
    }
}

class PropertyType(
    val type: TypeExpression,
    val mutable: Boolean
) : MemberType() {
    override fun toK(`package`: KPackage, abstract: Boolean, target: Target): KMemberType {
        return KPropertyType(
            type.toK(`package`, target),
            mutable,
            null,
            null
        )
    }
}

class ConstructorType(
    val arguments: List<Argument>
) : MemberType() {
    override fun toK(`package`: KPackage, abstract: Boolean, target: Target): KMemberType {
        val kBody = when (target) {
            Target.COMMON -> null
            Target.JS -> KDefinedExternally
            Target.OTHER -> KJsOnly
        }
        return KConstructorType(arguments.map { it.toK(`package`, target) }, kBody)
    }
}
