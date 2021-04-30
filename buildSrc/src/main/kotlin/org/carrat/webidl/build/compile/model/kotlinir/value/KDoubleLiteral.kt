package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock

class KDoubleLiteral(
    val value: Double
) : KLiteral() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("%#f".format(value))
    }
}
