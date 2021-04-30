package org.carrat.webidl.build.compile.model.kotlinir

import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

abstract class KTypeDeclaration : KDeclaration() {
    abstract val members: Collection<KMemberDeclaration>
    abstract val companionMembers: Collection<KMemberDeclaration>
    abstract val superInterfaces: Collection<KTypeExpression>
}
