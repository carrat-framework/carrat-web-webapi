package org.carrat.webidl.build.compile.parser

import org.carrat.webidl.build.compile.model.webidlir.*
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

internal class IrBuilderContext {
    private val declarations: MutableMap<String, IrDeclarationBuilder> = mutableMapOf()
    private val typeDefs: MutableSet<WTypedef> = mutableSetOf()
    private val callbacks: MutableSet<WCallback> = mutableSetOf()
    private val includes: MutableSet<WIncludes> = mutableSetOf()

    fun registerDeclaration(
        identifier: String,
        type: CitizenType
    ): IrDeclarationBuilder {
        val declarationBuilder = declarations.computeIfAbsent(identifier) { IrDeclarationBuilder(identifier, type) }
        if (declarationBuilder.type != type) {
            throw IllegalStateException(
                "There is already registered declaration with identifier $identifier and type " +
                        "${declarationBuilder.type}. Tried to register declaration with same identifier, but " +
                        "different type $type."
            )
        }
        return declarationBuilder
    }

    fun addTypedef(type: WTypeExpression, text: String) {
        typeDefs.add(WTypedef("", type, text))
    }

    fun addCallback(callback: WCallback) {
        callbacks.add(callback)
    }

    fun addIncludes(includes: WIncludes) {
        this.includes.add(includes)
    }

    fun build(): Set<WDeclaration> {
        includes.forEach {
            val left = declarations[it.identifier]
            if (left != null) {
                left.includes.add(it.inherits)
            } else {
                System.err.println("Could not find declaration to include to " + it.identifier)
            }
        }
        val declarations = declarations.values.map { it.build() }.toMutableSet()
        declarations += typeDefs
        declarations += callbacks
        return declarations
    }
}
