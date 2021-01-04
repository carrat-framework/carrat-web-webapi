package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.CodeBlock

class CodeBlockKValue(
    val code : String
) : KValue() {
    override fun toPoetCodeBlock(): CodeBlock {
        return CodeBlock.of(code)
    }
}
