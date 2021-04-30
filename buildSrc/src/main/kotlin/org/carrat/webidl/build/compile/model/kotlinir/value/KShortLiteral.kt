package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

data class KShortLiteral(
    val value: Short
) : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of(value.toString())
}
