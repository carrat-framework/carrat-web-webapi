package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

object KEmptyArrayLiteral : KLiteral() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("emptyArray()")
    }
}
