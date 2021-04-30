package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KVariableDeclaration(
    override val name: KName,
    val type: KTypeExpression,
    val mutable: Boolean,
    val expect: Boolean,
    val actual: Boolean
) : KDeclaration() {
    override fun toPoet(fileBuilder: FileSpec.Builder) {
        val builder = PropertySpec.builder(name.name, type.toPoet())
        if (expect) {
            builder.addModifiers(KModifier.EXPECT)
        }
        if (actual) {
            builder.addModifiers(KModifier.ACTUAL)
        }
        fileBuilder.addProperty(builder.build())
    }
}
