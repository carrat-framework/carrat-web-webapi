package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.kotlinir.KDeclaration
import org.carrat.webidl.build.compile.model.kotlinir.KPackage

abstract class Declaration {
    abstract val identifier: String
    abstract fun toK(`package`: KPackage, target: Target): KDeclaration?
}


