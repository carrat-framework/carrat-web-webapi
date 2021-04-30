package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

data class KBooleanLiteral(
    val value: Boolean
) : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of(if (value) "true" else "false")
}
