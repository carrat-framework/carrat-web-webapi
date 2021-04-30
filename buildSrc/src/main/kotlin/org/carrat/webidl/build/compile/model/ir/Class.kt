package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.kotlinir.KClassDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeReference

class Class(
    override val identifier: String,
    val inherits: String?,
    val implements: Collection<String>,
    override val members: Collection<MemberDeclaration>
) : ClassOrInterface() {
    override fun toK(`package`: KPackage, target: Target): KDeclaration {
        return KClassDeclaration(
            KName(`package`, identifier),
            inherits?.let { KTypeReference(KName(`package`, it)) },
            implements.map { KTypeReference(KName(`package`, it)) },
            members.filter { !it.static }.map { it.toK(`package`, true, target) },
            members.filter { it.static }.map { it.toK(`package`, false, target) },
            target == Target.COMMON,
            target != Target.COMMON,
            false,
            target == Target.JS
        )
    }
}
