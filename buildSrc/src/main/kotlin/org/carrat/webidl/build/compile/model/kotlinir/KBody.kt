package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.CodeBlock

abstract class KBody {
    abstract fun toPoet(): CodeBlock
}
