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
    override fun addTo(builder: TypeSpec.Builder, dynamicSupported: Boolean, isObject: Boolean) {
        val mBuilder = FunSpec.builder(name)
        mBuilder.returns(type.returnType.getPoetTypeName(dynamicSupported))
        if (actual) {
            mBuilder.addModifiers(KModifier.ACTUAL)
        }
        if (abstract) {
            mBuilder.addModifiers(KModifier.ABSTRACT)
        } else {
            if (!isObject) {
                mBuilder.addModifiers(KModifier.OPEN)
            }
        }

        if (actual && !external && !abstract) {
            mBuilder.addCode("jsOnly()")
        }
        mBuilder.addParameters(getPoetParameters(type.parameters, dynamicSupported))
        builder.addFunction(mBuilder.build())
    }

    private fun getPoetParameters(arguments: List<Parameter>, dynamicSupported: Boolean) = arguments.map {
        val builder = ParameterSpec.builder(it.name ?: "", it.type.getPoetTypeName(dynamicSupported))
        if (it.defaultValue != null) {
            builder.defaultValue(it.defaultValue.toPoetCodeBlock())
        }
        builder.build()
    }

    fun unifyConflicts(others: MutableCollection<MemberDeclaration>): MethodDeclaration {
        val conflicts = others.filterIsInstance<MethodDeclaration>().filter { other ->
                    other.name == name &&
                    other.type.parameters.size == type.parameters.size &&
                    other.type.parameters.zip(type.parameters).all { (a, b) -> conflicts(a, b) }
        }
        if (conflicts.isEmpty()) {
            return this
        } else {
            others.removeAll(conflicts)
            val newParameters: MutableList<Parameter> = mutableListOf()
            for (i in type.parameters.indices) {
                var b = type.parameters[i]
                for(conflict in conflicts) {
                    val a = conflict.type.parameters[i]
                    if(conflicts(a, b)){
                        b = getUpper(a, b)
                    }
                }
                newParameters.add(b)
            }
            return MethodDeclaration(name, LambdaType(newParameters, type.returnType), actual, abstract, external)
        }
    }

    private fun conflicts(a: Parameter, b: Parameter): Boolean = conflicts(a.type, b.type) && a.vararg == b.vararg

    private fun getUpper(a: Parameter, b: Parameter): Parameter {
        return Parameter(b.name, getUpper(a.type, b.type), a.vararg || b.vararg, null)
    }

    private fun conflicts(a: KType, b: KType): Boolean {
        return (a == KDynamic || b == KDynamic) || a == b
    }

    private fun getUpper(a: KType, b: KType): KType {
        return when {
            (a == KDynamic || b == KDynamic) -> KDynamic
            a == b -> a
            else -> KDynamic
        }
    }
}
