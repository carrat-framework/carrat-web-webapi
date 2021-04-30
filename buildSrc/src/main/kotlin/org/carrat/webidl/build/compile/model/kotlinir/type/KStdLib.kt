package org.carrat.webidl.build.compile.model.kotlinir.type

import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage

object KStdLib {
    public val kotlinPackage = KPackage(null, "kotlin")
    public val anyType = KTypeReference(KName(kotlinPackage, "Any"))
}
