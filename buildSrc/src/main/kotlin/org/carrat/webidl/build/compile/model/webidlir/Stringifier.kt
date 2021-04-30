package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.MemberDeclaration

sealed class Stringifier : WMember() {
//    data class AttributeStringifier(
//        val wAttribute: WAttribute
//    ) : Stringifier() {
//        override fun WidlContext.toIr(declaration: String, static : Boolean): MemberDeclaration? = with(wAttribute) { toIr(declaration, static) }
//    }
//
//    data class OperationStringifier(
//        val operation: WOperation
//    ) : Stringifier() {
//        override fun WidlContext.toIr(declaration: String, static : Boolean): MemberDeclaration? = with(operation) { toIr(declaration, static) }
//    }

    object EmptyStringifier : Stringifier() {
        override fun WidlContext.toIr(declaration: String): MemberDeclaration? = null
    }
}
