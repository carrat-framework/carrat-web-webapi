package org.carrat.webidl.build.compile.parser

import org.carrat.webidl.build.compile.model.webidlir.*
import org.carrat.webidl.build.compile.model.webidlir.types.Type

class IrBuilder {
    private val declarations : MutableMap<Identifier, DeclarationIrBuilder> = mutableMapOf()
    private var declaration : DeclarationIrBuilder? = null
    private val typeDefs : MutableSet<Typedef> = mutableSetOf()
    private val callbacks : MutableSet<Callback> = mutableSetOf()
    private val includes : MutableSet<Includes> = mutableSetOf()

    fun enterDeclaration(name : String, type : CitizenType, inherits : String? = null){
        val identifier = Identifier(name)
        declaration = declarations.computeIfAbsent(identifier) { DeclarationIrBuilder(identifier, type, inherits) }
        if(declaration!!.inherits == null && inherits != null) {
            declaration!!.inherits = inherits
        }
    }

    fun exitDeclaration(){
        declaration = null
    }

    fun addMember(member : Member){
        declaration!!.members.add(member)
    }

    fun addTypedef(type: Type, text: String) {
        typeDefs.add(Typedef(type, Identifier(text)))
    }

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    fun addIncludes(includes : Includes) {
        this.includes.add(includes)
    }

    fun build(): Set<Declaration> {
        includes.forEach {
            val left = declarations[it.left]
            if(left != null) {
                left.members.add(it)
            } else {
                System.err.println("Could not find declaration to include to " + it.left.name)
            }
        }
        val declarations = declarations.values.map {it.build()}.toMutableSet()
        declarations+=typeDefs
        declarations+=callbacks
        return declarations
    }
}
