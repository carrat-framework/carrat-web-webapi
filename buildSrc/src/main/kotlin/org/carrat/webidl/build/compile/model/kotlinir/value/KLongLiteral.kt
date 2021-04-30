package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

data class KLongLiteral(
    val value: Long
) : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of(value.toString())
}
