package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

@OptIn(ExperimentalUnsignedTypes::class)
data class KULongLiteral(
    val value: ULong
) : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of("${value}UL")
}
