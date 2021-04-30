package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.carrat.webidl.build.compile.model.kotlinir.type.KLambdaTypeExpression

data class KMethodType(
    val type: KLambdaTypeExpression,
    val kBody: KBody?
) : KMemberType() {
    override fun toPoet(builder: TypeSpec.Builder, identifier: String?, modifiers: Collection<KModifier>) {
        val mBuilder = FunSpec.builder(identifier!!)
        mBuilder.returns(type.returnType.toPoet())
        mBuilder.addModifiers(modifiers)
        if (kBody != null) {
            mBuilder.addCode(kBody.toPoet())
        }
        mBuilder.addParameters(type.parameters.map(KParameter::toPoet))
        builder.addFunction(mBuilder.build())
    }
//    override fun addTo(builder: TypeSpec.Builder, dynamicSupported: Boolean, isObject: Boolean) {
//        val mBuilder = FunSpec.builder(name)
//        mBuilder.returns(type.returnType.getPoetTypeName(dynamicSupported))
//        if (actual) {
//            mBuilder.addModifiers(KModifier.ACTUAL)
//        }
//        if (abstract) {
//            mBuilder.addModifiers(KModifier.ABSTRACT)
//        } else {
//            if (!isObject) {
//                mBuilder.addModifiers(KModifier.OPEN)
//            }
//        }
//
//        if (actual && !external && !abstract) {
//            mBuilder.addCode("jsOnly()")
//        }
//        mBuilder.addParameters(getPoetParameters(type.parameters, dynamicSupported))
//        builder.addFunction(mBuilder.build())
//    }

//    private fun getPoetParameters(arguments: List<Parameter>, dynamicSupported: Boolean) = arguments.map {
//        it.getPoetParameterSpec(actual, dynamicSupported)
//    }
//
//    fun unifyConflicts(others: MutableCollection<KMemberDeclaration>): KMethodType {
//        val conflicts = others.filterIsInstance<KMethodType>().filter { other ->
//                    other.name == name &&
//                    other.type.parameters.size == type.parameters.size &&
//                    other.type.parameters.zip(type.parameters).all { (a, b) -> conflicts(a, b) }
//        }
//        if (conflicts.isEmpty()) {
//            return this
//        } else {
//            others.removeAll(conflicts)
//            val newParameters: MutableList<Parameter> = mutableListOf()
//            for (i in type.parameters.indices) {
//                var b = type.parameters[i]
//                for(conflict in conflicts) {
//                    val a = conflict.type.parameters[i]
//                    if(conflicts(a, b)){
//                        b = getUpper(a, b)
//                    }
//                }
//                newParameters.add(b)
//            }
//            return KMethodType(name, LambdaType(newParameters, type.returnType), actual, abstract, external)
//        }
//    }
//
//    private fun conflicts(a: Parameter, b: Parameter): Boolean = conflicts(a.type, b.type) && a.vararg == b.vararg
//
//    private fun getUpper(a: Parameter, b: Parameter): Parameter {
//        return Parameter(b.name, getUpper(a.type, b.type), a.vararg || b.vararg, null)
//    }
//
//    private fun conflicts(a: KType, b: KType): Boolean {
//        return (a == KDynamic || b == KDynamic) || a == b
//    }
//
//    private fun getUpper(a: KType, b: KType): KType {
//        return when {
//            (a == KDynamic || b == KDynamic) -> KDynamic
//            a == b -> a
//            else -> KDynamic
//        }
//    }
}
