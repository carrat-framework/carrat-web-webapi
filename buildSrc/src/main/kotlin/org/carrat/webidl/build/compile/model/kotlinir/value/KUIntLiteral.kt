package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

@OptIn(ExperimentalUnsignedTypes::class)
data class KUIntLiteral(
    val value: UInt
) : KLiteral() {
    override fun toPoet(): CodeBlock = CodeBlock.of("${value}u")
}
