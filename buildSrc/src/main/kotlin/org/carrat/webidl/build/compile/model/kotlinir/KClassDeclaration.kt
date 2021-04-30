package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KClassDeclaration(
    override val name: KName,
    override val inherits: KTypeExpression?,
    override val superInterfaces: Collection<KTypeExpression>,
    override val members: Collection<KMemberDeclaration>,
    override val companionMembers: Collection<KMemberDeclaration>,
    val expect: Boolean,
    val actual: Boolean,
    val abstract: Boolean,
    val external: Boolean
) : KClassOrObjectDeclaration() {
    override fun toPoet(fileBuilder: FileSpec.Builder) {
        val builder = TypeSpec.classBuilder(name.toPoet())
        if (expect) {
            builder.addModifiers(KModifier.EXPECT)
        }
        if (actual) {
            builder.addModifiers(KModifier.ACTUAL)
        }
        if (abstract) {
            builder.addModifiers(KModifier.ABSTRACT)
        } else {
            builder.addModifiers(KModifier.OPEN)
        }
        if (external) {
            builder.addModifiers(KModifier.EXTERNAL)
        }
        inherits?.let { builder.superclass(inherits.toPoet()) }
        builder.addSuperinterfaces(superInterfaces.map(KTypeExpression::toPoet))
        members.forEach {
            it.toPoet(builder)
        }
        if (!companionMembers.isEmpty()) {
            val companionObjectBuilder = TypeSpec.companionObjectBuilder()
            if (actual) {
                companionObjectBuilder.addModifiers(KModifier.ACTUAL)
            }
            companionMembers.forEach {
                it.toPoet(companionObjectBuilder)
            }
            builder.addType(companionObjectBuilder.build())
        }
        fileBuilder.addType(builder.build())
    }
}
