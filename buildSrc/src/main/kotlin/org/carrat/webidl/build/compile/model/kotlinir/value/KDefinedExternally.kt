package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

object KDefinedExternally : KValueExpression() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("definedExternally")
    }
}
