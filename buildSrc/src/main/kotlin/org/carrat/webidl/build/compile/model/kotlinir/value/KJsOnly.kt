package org.carrat.webidl.build.compile.model.kotlinir.value

import com.squareup.kotlinpoet.CodeBlock
import org.carrat.webidl.build.compile.model.kotlinir.KBody

object KJsOnly : KBody() {
    override fun toPoet(): CodeBlock {
        return CodeBlock.of("jsOnly()")
    }
}
