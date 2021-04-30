package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

object KUndefinedLiteral : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of("undefined")
}
