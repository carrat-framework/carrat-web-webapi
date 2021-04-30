package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KPropertyType(
    val type: KTypeExpression,
    val mutable: Boolean,
    val getter: KBody?,
    val setter: KBody?
) : KMemberType() {
    override fun toPoet(builder: TypeSpec.Builder, identifier: String?, modifiers: Collection<KModifier>) {
        val pType = type.toPoet()
        val mBuilder = PropertySpec.builder(identifier!!, pType)
        mBuilder.mutable(mutable)
        mBuilder.addModifiers(modifiers)
        if (getter != null) {
            mBuilder.getter(FunSpec.getterBuilder().addCode(getter.toPoet()).build())
        }
        if (setter != null) {
            mBuilder.setter(FunSpec.setterBuilder().addParameter("value", pType).addCode(setter.toPoet()).build())
        }
        builder.addProperty(mBuilder.build())
    }
}
