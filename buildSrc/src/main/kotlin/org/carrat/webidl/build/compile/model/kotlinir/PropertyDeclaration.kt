package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.*

data class PropertyDeclaration(
    val name: String,
    val type: KType,
    val overrides: Boolean,
    val mutable: Boolean,
    val actual: Boolean,
    val abstract: Boolean,
    val external: Boolean
) : MemberDeclaration() {
    override fun addTo(builder: TypeSpec.Builder, dynamicSupported: Boolean, isObject: Boolean) {
        val typeName = type.getPoetTypeName(dynamicSupported)
        val pBuilder = PropertySpec.builder(name, typeName)
        pBuilder.mutable(mutable)
        if (overrides) {
            pBuilder.addModifiers(KModifier.OVERRIDE)
        }
        if (actual) {
            pBuilder.addModifiers(KModifier.ACTUAL)
        }
        if (abstract) {
            pBuilder.addModifiers(KModifier.ABSTRACT)
        } else {
            if (!isObject) {
                pBuilder.addModifiers(KModifier.OPEN)
            }
        }
        if (actual && !external && !abstract) {
            val gBuilder = FunSpec.getterBuilder()
            gBuilder.addCode("jsOnly()")
            pBuilder.getter(gBuilder.build())
            if (mutable) {
                val sBuilder = FunSpec.setterBuilder()
                sBuilder.addParameter(ParameterSpec("value", typeName))
                sBuilder.addCode("jsOnly()")
                pBuilder.setter(sBuilder.build())
            }
        }
        builder.addProperty(pBuilder.build())
    }

    fun unifyConflicts(others: MutableCollection<MemberDeclaration>): PropertyDeclaration {
        val conflicts = others.filterIsInstance<PropertyDeclaration>().filter { other ->
            other.name == name
        }
        if (conflicts.isEmpty()) {
            return this
        } else {
            others.removeAll(conflicts)
            var newType = type
            for (conflict in conflicts) {
                newType = getUpper(newType, conflict.type)
            }
            return PropertyDeclaration(name, newType, false, mutable, actual, abstract, external)
        }
    }

    private fun getUpper(a: KType, b: KType): KType {
        return when {
            (a == KDynamic || b == KDynamic) -> KDynamic
            a == b -> a
            else -> KDynamic
        }
    }
}
