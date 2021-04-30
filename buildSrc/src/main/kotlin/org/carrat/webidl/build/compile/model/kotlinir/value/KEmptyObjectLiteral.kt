package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

object KEmptyObjectLiteral : KLiteral() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("emptyObject()")
    }
}
