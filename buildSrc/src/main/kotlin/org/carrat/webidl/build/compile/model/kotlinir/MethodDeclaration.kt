package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec

data class MethodDeclaration(
    val name: String,
    val type: LambdaType,
    val actual: Boolean,
    val abstract: Boolean,
    val external: Boolean
) : MemberDeclaration() {
    override fun addTo(builder: TypeSpec.Builder, dynamicSupported : Boolean, isObject : Boolean) {
        val mBuilder = FunSpec.builder(name)
        mBuilder.returns(type.returnType.getPoetTypeName(dynamicSupported))
        if (actual) {
            mBuilder.addModifiers(KModifier.ACTUAL)
        }
        if (abstract) {
            mBuilder.addModifiers(KModifier.ABSTRACT)
        } else {
            if(!isObject) {
                mBuilder.addModifiers(KModifier.OPEN)
            }
        }

        if(actual && !external && !abstract) {
            mBuilder.addCode("jsOnly()")
        }
        mBuilder.addParameters(getPoetParameters(type.parameters, dynamicSupported))
    }

    private fun getPoetParameters(arguments: List<Parameter>, dynamicSupported : Boolean) = arguments.map {
        val builder = ParameterSpec.builder(it.name ?: "", it.type.getPoetTypeName(dynamicSupported))
        if (it.defaultValue != null) {
            builder.defaultValue(it.defaultValue.toPoetCodeBlock())
        }
        builder.build()
    }

    fun unifyConflicts(others: MutableCollection<MemberDeclaration>) : MethodDeclaration {
        val conflicts = others.filter { other ->
            other is MethodDeclaration &&
                    other.type.parameters.size == type.parameters.size &&
                    other.type.parameters.zip(type.parameters).all { (a, b) -> conflicts(a, b) }
        }
        if(conflicts.isEmpty()) {
            return this
        } else {
            others.removeAll(conflicts)
            val newParameters : MutableList<Parameter> = mutableListOf()
            for(i in type.parameters.indices) {
                val b = type.parameters[i]
                if(conflicts.any { conflicts((it as MethodDeclaration).type.parameters[i], b) }) {
                    newParameters.add(Parameter(b.name, KDynamic, b.vararg, null))
                } else {
                    newParameters.add(b)
                }
            }
            return MethodDeclaration(name, LambdaType(newParameters, type.returnType), actual, abstract, external)
        }
    }

    private fun conflicts(a: Parameter, b: Parameter): Boolean = conflicts(a.type, b.type) && a.vararg == b.vararg

    private fun conflicts(a: KType, b: KType): Boolean {
        return (a == KDynamic || b == KDynamic)
    }


}