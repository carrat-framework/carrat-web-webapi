package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.kotlinir.KMemberDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KPackage

class MemberDeclaration(
    val identifier: String?,
    val static: Boolean,
    val overrides: Boolean,
    val memberType: MemberType
) {
    fun toK(`package`: KPackage, abstract: Boolean, target: Target): KMemberDeclaration {
        return KMemberDeclaration(
            identifier,
            target != Target.COMMON,
            false,
            target == Target.JS,
            memberType !is ConstructorType,
            overrides,
            false,
            memberType.toK(`package`, abstract, target)
        )
    }
}
