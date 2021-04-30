package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

data class KStringLiteral(
    val value: String
) : KLiteral() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("\"" + value.replace(Regex("\\|\""), "\\\\\$0") + "\"")
    }
}
