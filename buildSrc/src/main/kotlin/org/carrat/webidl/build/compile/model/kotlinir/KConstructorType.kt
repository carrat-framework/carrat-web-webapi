package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

data class KConstructorType(
    val parameters: List<KParameter>,
    val kBody: KBody?
) : KMemberType() {
    override fun toPoet(builder: TypeSpec.Builder, identifier: String?, modifiers: Collection<KModifier>) {
        assert(identifier == null)
        val mBuilder = FunSpec.constructorBuilder()
        mBuilder.addModifiers(modifiers)
        if (kBody != null) {
            mBuilder.addCode(kBody.toPoet())
        }
        mBuilder.addParameters(parameters.map(KParameter::toPoet))
        builder.addFunction(mBuilder.build())
    }
}
