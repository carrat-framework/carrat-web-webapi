package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FileSpec

abstract class KDeclaration {
    abstract fun toPoet(fileBuilder: FileSpec.Builder)

    abstract val name: KName
}
