package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.CodeBlock

object DefinedExternally : KValue() {
    override fun toPoetCodeBlock(): CodeBlock = CodeBlock.of("definedExternally")
}