package org.carrat.webidl.build.compile.model.kotlinir.type

import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.TypeName

object KDynamic : KType() {
    override fun toPoet(): TypeName = Dynamic
}
