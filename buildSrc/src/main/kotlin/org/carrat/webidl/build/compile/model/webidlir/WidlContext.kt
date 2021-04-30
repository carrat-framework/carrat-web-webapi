package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Argument
import org.carrat.webidl.build.compile.model.ir.PropertyType
import org.carrat.webidl.build.compile.model.ir.type.*
import org.carrat.webidl.build.compile.model.ir.value.UndefinedLiteral
import org.carrat.webidl.build.compile.model.webidlir.types.WNullableTypeExpression
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeReference
import java.util.*

class WidlContext(
    private val wIndex: WIndex
) {
    private val functionGroups: MutableMap<FunctionGroupDiscriminator, FunctionGroup> = mutableMapOf()
    private val propertyGroups: MutableMap<PropertyGroupDiscriminator, PropertyGroup> = mutableMapOf()

    init {
        wIndex.values.forEach { declaration ->
            declaration.members.filterIsInstance<WOperation>().forEach {
                val discriminator = it.discriminator(declaration.identifier)
                if (discriminator != null) {
                    val functionGroup = functionGroups.computeIfAbsent(discriminator) { FunctionGroup() }
                    functionGroup.members += it
                }
            }
        }
        functionGroups.values.forEach(FunctionGroup::compile)

        wIndex.values.forEach { declaration ->
            declaration.members.filterIsInstance<WAttribute>().forEach {
                val discriminator = it.discriminator(declaration.identifier)
                val propertyGroup = propertyGroups.computeIfAbsent(discriminator) { PropertyGroup() }
                propertyGroup.members += it
            }
        }

        propertyGroups.forEach { discriminator, group ->
            var inherits = wIndex[discriminator.declaration]?.inherits
            while (inherits != null) {
                val declaration = wIndex[inherits]
                inherits = if (declaration is WInterface) {
                    if (declaration.members.filterIsInstance<WAttribute>().any {
                            it.name == discriminator.name && it.static == discriminator.static
                        }) {
                        group.overrides = true
                        null
                    } else {
                        declaration.inherits
                    }
                } else {
                    null
                }
            }
        }
    }

    tailrec fun WTypeExpression.resolve(): WTypeExpression {
        return when (this) {
            is WTypeReference -> {
                val wDeclaration = wIndex[identifier]
                if (wDeclaration is WTypedef) {
                    wDeclaration.type.resolve()
                } else {
                    this
                }
            }
            is WNullableTypeExpression -> {
                @Suppress("NON_TAIL_RECURSIVE_CALL")
                WNullableTypeExpression(this.baseType.resolve())
            }
            else ->
                this
        }
    }

    fun getOperationType(operation: WOperation, declaration: String): LambdaTypeExpression? {
        val discriminator = operation.discriminator(declaration)
        return if (discriminator != null) {
            val functionGroup = functionGroups[discriminator]!!
            val findUnion = functionGroup.unions[operation]!!.parent()
            if (findUnion.element == operation) {
                findUnion.item
            } else {
                null
            }
        } else {
            null
        }
    }

    fun getPropertyType(attribute: WAttribute, declaration: String): PropertyType? {
        val discriminator = attribute.discriminator(declaration)
        val propertyGroup = propertyGroups[discriminator]!!
        return if (propertyGroup.members.first() == attribute) {
            propertyGroup.compile()
        } else {
            null
        }
    }

    fun overrides(attribute: WAttribute, declaration: String): Boolean {
        val discriminator = attribute.discriminator(declaration)
        val propertyGroup = propertyGroups[discriminator]!!
        return propertyGroup.overrides
    }

    fun overrides(operation: WOperation, declaration: String): Boolean {
        val type = functionGroups[operation.discriminator(declaration)]!!.unions[operation]!!.item
        return findAllInherited(declaration).any {
            val discriminator =
                FunctionGroupDiscriminator(it, operation.name!!, operation.arguments.size, operation.static)
            val functionGroup = functionGroups[discriminator]
            functionGroup?.unions?.values?.any { conflicts(it.item, type) is TypeConflict.Conflict } ?: false
        }
    }

    data class PropertyGroupDiscriminator(
        val declaration: String,
        val name: String,
        val static: Boolean
    )

    data class FunctionGroupDiscriminator(
        val declaration: String,
        val name: String,
        val parametersCount: Int,
        val static: Boolean
    )

    private inner class PropertyGroup {

        fun compile(): PropertyType {
            return PropertyType(
                members.map { it.type.toIr() }.reduce { a, b -> union(a, b) },
                members.any { !it.readOnly })
        }

        val members = mutableListOf<WAttribute>()
        var overrides: Boolean = false
    }

    private inner class FunctionGroup {
        val members = mutableSetOf<WOperation>()

        lateinit var unions: Map<WOperation, FindUnion<WOperation, LambdaTypeExpression>>

        fun compile() {
            val unions = members.map { FindUnion(it, irOperationType(it)) }
            unions.flatMap { a -> unions.map { b -> Pair(a, b) } }.filter { (a, b) -> a != b }.forEach { (a, b) ->
                val conflicts = conflicts(a.item, b.item)
                if (conflicts is TypeConflict.Conflict) {
                    a.union(b, conflicts.item)
                }
            }
            this.unions = unions.associateBy { it.element }
        }
    }

    private fun conflicts(
        a: LambdaTypeExpression,
        b: LambdaTypeExpression
    ): TypeConflict<LambdaTypeExpression> {
        val conflict = conflicts(a.arguments, b.arguments)
        return if (conflict is TypeConflict.Conflict) {
            TypeConflict.Conflict(LambdaTypeExpression(conflict.item, union(a.returnType, b.returnType)))
        } else {
            TypeConflict.NoConflict
        }
    }

    private fun conflicts(a: List<Argument>, b: List<Argument>): TypeConflict<List<Argument>> {
        val conflicts = a.zip(b).mapIndexed { i, (a, b) -> conflicts(a, b, i + 1) }
        return if (conflicts.any { it == TypeConflict.NoConflict }) {
            TypeConflict.NoConflict
        } else {
            TypeConflict.Conflict(conflicts.map { (it as TypeConflict.Conflict<Argument>).item })
        }
    }

    private fun conflicts(a: Argument, b: Argument, argNumber: Int): TypeConflict<Argument> {
        val type = conflicts(a.type, b.type)
        return if (type is TypeConflict.Conflict) {
            val name = if (a.identifier == b.identifier) a.identifier else "arg$argNumber"
            val defaultValue = if (a.defaultValue != null || b.defaultValue != null) UndefinedLiteral else null
            TypeConflict.Conflict(
                Argument(name, type.item, a.vararg || b.vararg, defaultValue)
            )
        } else {
            TypeConflict.NoConflict
        }
    }

    private fun union(a: TypeExpression, b: TypeExpression): TypeExpression {
        return findSuper(a, b)
    }

    private fun findSuper(a: TypeExpression, b: TypeExpression): TypeExpression {
        //TODO: Lambda type expressions
        val aSupers = generateSequence(a) { it.findSuper() }.toList()
        val bSupers = generateSequence(b) { it.findSuper() }.toList()
        val last = aSupers.reversed().zip(bSupers.reversed()).findLast { (a, b) -> a == b }
        return last?.first ?: IrDynamic
    }

    private fun conflicts(a: TypeExpression, b: TypeExpression): TypeConflict<TypeExpression> {
        return when {
            a == b -> TypeConflict.Conflict(a)
            a.isSuper(b) -> TypeConflict.Conflict(a)
            b.isSuper(a) -> TypeConflict.Conflict(b)
            else -> TypeConflict.NoConflict
        }
    }

    private fun TypeExpression.isSuper(b: TypeExpression): Boolean {
        return when {
            this == b -> true
            this is Nullable && b is Nullable -> this.element.isSuper(b.element)
            this is Nullable -> this.element.isSuper(b)
            this is IrDynamic || this is IrAny -> true
            this is PromiseTypeExpression && b is PromiseTypeExpression -> this.member.isSuper(b.member)
            this is TypeReference && b is TypeReference -> findSuper()?.isSuper(b) ?: false
            else -> false
        }
    }

    private fun TypeExpression.findSuper(): TypeExpression? {
        return when (this) {
            is Nullable -> this.element.findSuper()?.let { Nullable(it) }
            is TypeReference -> this.findSuper()
            else -> null
        }
    }

    private fun TypeReference.findSuper(): TypeReference? {
        return findSuper(this.identifier)?.let { TypeReference(it) }
    }

    private fun findSuper(declaration: String): String? {
        return wIndex[declaration]?.inherits
    }

    private fun findAllInherited(identifier: String): Set<String> {
        val queue = LinkedList<String>()
        val result = mutableSetOf<String>()
        val wDeclaration = wIndex[identifier]!!
        wDeclaration.inherits?.let { queue.add(it) }
        if (wDeclaration is WAnyInterface) {
            queue.addAll(wDeclaration.includes)
        }
        while (queue.isNotEmpty()) {
            val element = queue.poll()
            result += element
            val declaration = wIndex[element]!!
            declaration.inherits?.let { queue.add(it) }
            if (declaration is WAnyInterface) {
                queue.addAll(declaration.includes)
            }
        }
        return result
    }

    private fun findAllSuper(declaration: String): List<String> {
        return generateSequence(findSuper(declaration)) { findSuper(it) }.toList()
    }

    private fun irOperationType(operation: WOperation): LambdaTypeExpression {
        return LambdaTypeExpression(operation.arguments.map { with(it) { toIr(true) } }, operation.type.toIr())
    }

    private inner class FindUnion<Element, Item>(val element: Element, var item: Item) {
        private var parent = this

        fun parent(): FindUnion<Element, Item> {
            if (parent != this) {
                parent = parent.parent()
            }
            return parent
        }

        fun union(other: FindUnion<Element, Item>, arguments: Item) {
            parent().parent = other.parent()
            parent.item = arguments
        }
    }
}

private fun WOperation.discriminator(declaration: String): WidlContext.FunctionGroupDiscriminator? {
    return if (name != null) {
        WidlContext.FunctionGroupDiscriminator(
            declaration,
            name,
            parametersCount = this.arguments.size,
            static
        )
    } else {
        null
    }
}

private fun WAttribute.discriminator(declaration: String): WidlContext.PropertyGroupDiscriminator {
    return WidlContext.PropertyGroupDiscriminator(
        declaration,
        name,
        static
    )
}

private sealed class TypeConflict<out T> {
    object NoConflict : TypeConflict<Nothing>()

    data class Conflict<out T>(
        val item: T
    ) : TypeConflict<T>()
}
