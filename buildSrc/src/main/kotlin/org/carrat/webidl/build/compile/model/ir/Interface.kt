package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.kotlinir.KDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KInterfaceDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeReference

data class Interface(
    override val identifier: String,
    val inherits: Collection<String>,
    override val members: Collection<MemberDeclaration>,
    val isFun: Boolean
) : ClassOrInterface() {
    override fun toK(`package`: KPackage, target: Target): KDeclaration {
        return KInterfaceDeclaration(
            KName(`package`, identifier),
            inherits.map { KTypeReference(KName(`package`, it)) },
            members.filter { !it.static }.map { it.toK(`package`, true, target) },
            members.filter { it.static }.map { it.toK(`package`, false, target) },
            target == Target.COMMON,
            target != Target.COMMON,
            target == Target.JS,
            isFun
        )
    }
}
