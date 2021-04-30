package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

class KFloatLiteral(
    val value: Float
) : KLiteral() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("%#ff".format(value))
    }
}
