package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.*
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KInterfaceDeclaration(
    override val name: KName,
    override val superInterfaces: Collection<KTypeExpression>,
    override val members: Collection<KMemberDeclaration>,
    override val companionMembers: Collection<KMemberDeclaration>,
    val expect: Boolean,
    val actual: Boolean,
    val external: Boolean,
    val functional: Boolean,
    val jsName: String? = null
) : KTypeDeclaration() {
    override fun toPoet(fileBuilder: FileSpec.Builder) {
        val builder = TypeSpec.interfaceBuilder(name.toPoet())
        if (expect) {
            builder.addModifiers(KModifier.EXPECT)
        }
        if (actual) {
            builder.addModifiers(KModifier.ACTUAL)
        }
        if (external) {
            builder.addModifiers(KModifier.EXTERNAL)
        }
        if (functional) {
            builder.addModifiers(KModifier.FUN)
        }
        if (jsName != null) {
            builder.addAnnotation(
                AnnotationSpec.builder(ClassName("kotlin.js", "JsName")).addMember("\"$jsName\"").build()
            )
        }
        builder.addSuperinterfaces(superInterfaces.map(KTypeExpression::toPoet))
        members.forEach {
            it.toPoet(builder)
        }
        if (!companionMembers.isEmpty()) {
            val companionObjectBuilder = TypeSpec.companionObjectBuilder()
            builder.addAnnotation(
                AnnotationSpec.builder(Suppress::class).addMember("\"NESTED_CLASS_IN_EXTERNAL_INTERFACE\"").build()
            )
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
