package org.carrat.webidl.build.compile.model.kotlinir.type

import com.squareup.kotlinpoet.TypeName

abstract class KTypeExpression {
    abstract fun toPoet(): TypeName
}
