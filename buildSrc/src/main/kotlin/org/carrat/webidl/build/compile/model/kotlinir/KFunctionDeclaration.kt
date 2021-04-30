package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import org.carrat.webidl.build.compile.model.kotlinir.type.KLambdaTypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KFunctionDeclaration(
    override val name: KName,
    val expect: Boolean,
    val actual: Boolean,
    val type: KLambdaTypeExpression,
    val body: KBody?,
    val external: Boolean
) : KTypeDeclaration() {
    override val members: Collection<KMemberDeclaration>
        get() = emptyList()
    override val companionMembers: Collection<KMemberDeclaration>
        get() = emptyList()
    override val superInterfaces: Collection<KTypeExpression>
        get() = emptyList()

    override fun toPoet(fileBuilder: FileSpec.Builder) {
        val builder = FunSpec.builder(name.name)
        if (expect) {
            builder.addModifiers(KModifier.EXPECT)
        }
        if (actual) {
            builder.addModifiers(KModifier.ACTUAL)
        }
        if (external) {
            builder.addModifiers(KModifier.EXTERNAL)
        }
        builder.returns(type.returnType.toPoet())
        body?.let { builder.addCode(it.toPoet()) }
        fileBuilder.addFunction(builder.build())
    }
}
