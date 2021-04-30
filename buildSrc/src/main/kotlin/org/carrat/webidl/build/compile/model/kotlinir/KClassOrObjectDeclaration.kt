package org.carrat.webidl.build.compile.model.kotlinir

import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

abstract class KClassOrObjectDeclaration : KTypeDeclaration() {
    abstract val inherits: KTypeExpression?
}
