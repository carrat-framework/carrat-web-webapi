package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KObjectDeclaration(
    override val name: KName,
    override val inherits: KTypeExpression?,
    override val superInterfaces: Collection<KTypeExpression>,
    override val members: Collection<KMemberDeclaration>,
    val expect: Boolean,
    val actual: Boolean,
    val external: Boolean
) : KClassOrObjectDeclaration() {
    override val companionMembers: Collection<KMemberDeclaration>
        get() = emptyList()

    override fun toPoet(fileBuilder: FileSpec.Builder) {
        val builder = TypeSpec.objectBuilder(name.toPoet())
        if (expect) {
            builder.addModifiers(KModifier.EXPECT)
        }
        if (actual) {
            builder.addModifiers(KModifier.ACTUAL)
        }
        if (external) {
            builder.addModifiers(KModifier.EXTERNAL)
        }
        inherits?.let { builder.superclass(inherits.toPoet()) }
        builder.addSuperinterfaces(superInterfaces.map(KTypeExpression::toPoet))
        members.forEach {
            it.toPoet(builder)
        }
        fileBuilder.addType(builder.build())
    }
}
