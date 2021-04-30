package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

@OptIn(ExperimentalUnsignedTypes::class)
data class KUByteLiteral(
    val value: UByte
) : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of("${value}u")
}
