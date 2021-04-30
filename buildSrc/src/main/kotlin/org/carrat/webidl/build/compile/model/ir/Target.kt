package org.carrat.webidl.build.compile.model.ir

import org.carrat.webidl.build.compile.model.kotlinir.KNullableType
import org.carrat.webidl.build.compile.model.kotlinir.type.KDynamic
import org.carrat.webidl.build.compile.model.kotlinir.type.KStdLib
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

private val anyNullable = KNullableType(KStdLib.anyType)

enum class Target {
    COMMON {
        override val dynamicType = anyNullable
    },
    JS {
        override val dynamicType = KDynamic
    },
    OTHER {
        override val dynamicType = anyNullable
    };

    abstract val dynamicType: KTypeExpression
}
