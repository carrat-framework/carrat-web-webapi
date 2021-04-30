package org.carrat.webidl.build.compile.model.ir

abstract class ClassOrInterface : Declaration() {
    abstract val members: Collection<MemberDeclaration>
}
