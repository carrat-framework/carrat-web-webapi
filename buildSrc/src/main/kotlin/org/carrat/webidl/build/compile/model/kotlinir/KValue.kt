package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.CodeBlock

abstract class KValue {
    abstract fun toPoetCodeBlock(): CodeBlock
}