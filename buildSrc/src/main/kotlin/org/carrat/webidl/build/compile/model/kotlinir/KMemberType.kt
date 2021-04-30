package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

abstract class KMemberType {
    abstract fun toPoet(builder: TypeSpec.Builder, identifier: String?, modifiers: Collection<KModifier>)
}
