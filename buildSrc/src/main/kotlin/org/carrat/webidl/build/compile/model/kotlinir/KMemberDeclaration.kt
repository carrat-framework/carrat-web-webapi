package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

class KMemberDeclaration(
    val identifier: String?,
    val actual: Boolean,
    val abstract: Boolean,
    val external: Boolean,
    val open: Boolean,
    val overrides: Boolean,
    val internal: Boolean,
    val memberType: KMemberType
) {
    fun toPoet(builder: TypeSpec.Builder) {
        val modifiers = mutableListOf<KModifier>()
        if (actual) {
            modifiers += KModifier.ACTUAL
        }
        if (abstract) {
            modifiers += KModifier.ABSTRACT
        }
        if (external) {
            modifiers += KModifier.EXTERNAL
        }
        if (open) {
            modifiers += KModifier.OPEN
        }
        if (overrides) {
            modifiers += KModifier.OVERRIDE
        }
        if (internal) {
            modifiers += KModifier.INTERNAL
        }
        memberType.toPoet(builder, identifier, modifiers)
    }
}
