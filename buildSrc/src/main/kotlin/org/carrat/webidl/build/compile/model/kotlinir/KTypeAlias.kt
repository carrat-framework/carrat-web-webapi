package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeAliasSpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression

class KTypeAlias(
    override val name: KName,
    val type: KTypeExpression
) : KDeclaration() {
    override fun toPoet(fileBuilder: FileSpec.Builder) {
        val b = TypeAliasSpec.builder(
            name.name, type.toPoet()
        )
        fileBuilder.addTypeAlias(b.build())
    }
}
