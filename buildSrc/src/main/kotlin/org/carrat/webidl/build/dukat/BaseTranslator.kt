package org.carrat.webidl.build.dukat

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.*
import org.carrat.webidl.build.compile.model.kotlinir.type.KLambdaTypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeExpression
import org.carrat.webidl.build.compile.model.kotlinir.type.KTypeReference
import org.carrat.webidl.build.compile.model.kotlinir.value.KJsOnly
import org.carrat.webidl.build.compile.model.kotlinir.value.KValueExpression
import org.carrat.webidl.build.compile.model.webidlir.WIndex
import org.carrat.webidl.build.compile.model.webidlir.WInterface
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel

abstract class BaseTranslator(
    val `package`: KPackage,
    val target: Target,
    val actual: Boolean,
    val external: Boolean,
    val externalValue: KValueExpression
) {
    fun translate(wIndex: WIndex, sourceSet: SourceSetModel): Collection<KDeclaration> {
        return sourceSet.sources.flatMap { translateSourceFile(wIndex, it) }
    }

    private fun translateSourceFile(wIndex: WIndex, sourceFile: SourceFileModel): Collection<KDeclaration> {
        return translateModule(wIndex, sourceFile.root)
    }

    private fun translateModule(wIndex: WIndex, module: ModuleModel): Collection<KDeclaration> {
        return module.declarations.mapNotNull {
            translateTopLevel(
                wIndex,
                it
            )
        } + module.submodules.flatMap { translateModule(wIndex, it) }
    }

    private fun translateTopLevel(wIndex: WIndex, declaration: TopLevelModel): KDeclaration? {
        return when (declaration) {
            is TypeAliasModel -> translateTypeAlias(declaration)
            is ClassModel -> translateClass(wIndex, declaration)
            is InterfaceModel -> translateInterface(declaration)
            is ObjectModel -> translateTopObject(declaration)
            is VariableModel -> translateTopVariable(declaration)
            is FunctionModel -> translateTopFunction(declaration)
            is EnumModel -> translateEnum(declaration)
            is ImportModel -> translateImport(declaration)
            else -> TODO()
        }
    }

    protected abstract fun translateTypeAlias(typeAlias: TypeAliasModel): KTypeAlias?

    private val internalConstructor = KMemberDeclaration(
        null,
        false,
        false,
        false,
        false,
        false,
        true,
        KConstructorType(
            emptyList(),
            KJsOnly
        )
    )

    private fun nonStatic(member: MemberModel): Boolean {
        return when (member) {
            is MethodModel -> !member.static
            is PropertyModel -> !member.static
            else -> true
        }
    }

    protected fun translateClass(wIndex: WIndex, `class`: ClassModel): KClassDeclaration? {
        val inherits = `class`.parentEntities.singleOrNull { wIndex[translateSimpleName(it.value.value)] is WInterface }
        var members = `class`.members.filter(::nonStatic).mapNotNull { translateMember(it, true, true) }
        `class`.primaryConstructor?.let {
            members = members + translateConstructor(it)
        }
        if (target == Target.OTHER && members.map { it.memberType }.filterIsInstance<KConstructorType>()
                .none { it.parameters.isEmpty() }
        ) {
            members = members + internalConstructor
        }
        return KClassDeclaration(
            KName(`package`, translateSimpleName(`class`.name)),
            inherits?.let { translateHeritage(it) },
            `class`.parentEntities.filter { it != inherits }.map { translateHeritage(it) },
            members,
            `class`.companionObject?.members?.mapNotNull { translateMember(it, false, true) } ?: emptyList(),
            !actual,
            actual,
            false,
            external
        )
    }

    private fun translateInterface(`interface`: InterfaceModel): KInterfaceDeclaration? {
        val name = translateSimpleName(`interface`.name)
        return KInterfaceDeclaration(
            KName(`package`, name),
            `interface`.parentEntities.map { translateHeritage(it) },
            `interface`.members.mapNotNull { translateMember(it, false, false) },
            `interface`.companionObject?.members?.mapNotNull { translateMember(it, false, true) } ?: emptyList(),
            !actual,
            actual,
            external,
            false,//TODO
            if (!actual) "I$name" else null
        )
    }

    private fun translateTopObject(`object`: ObjectModel): KDeclaration? {
        return KObjectDeclaration(
            KName(`package`, translateSimpleName(`object`.name)),
            null,//TODO?
            `object`.parentEntities.map { translateHeritage(it) },
            `object`.members.mapNotNull { translateMember(it, false, true) },
            !actual,
            actual,
            external
        )
    }

    private fun translateTopVariable(variable: VariableModel): KVariableDeclaration {
        return KVariableDeclaration(
            KName(`package`, translateSimpleName(variable.name)),
            translateType(variable.type),
            !variable.immutable,
            !actual,
            actual
        )
    }

    private fun translateTopFunction(function: FunctionModel): KFunctionDeclaration {
        return KFunctionDeclaration(
            KName(`package`, translateSimpleName(function.name)), !actual, actual,
            KLambdaTypeExpression(function.parameters.map { translateParameter(it) }, translateType(function.type)),
            if (actual && !external) externalValue else null,
            external
        )
    }

    private fun translateEnum(function: EnumModel): KDeclaration? = TODO()
    private fun translateImport(`import`: ImportModel): KDeclaration? = null

    protected fun translateHeritage(heritage: HeritageModel): KTypeReference {
        return KTypeReference(
            KName(`package`, translateSimpleName(heritage.value.value)),
            heritage.value.params.map { translateTypeParameter(it) }
        )
    }

    protected fun translateType(type: TypeModel): KTypeExpression {
        return when (type) {
            is TypeValueModel -> translateTypeValue(type)
            is FunctionTypeModel -> translateFunctionType(type)
            is TypeParameterReferenceModel -> TODO()
            is TypeParameterModel -> translateTypeParameter(type)
            else -> TODO()
        }
    }

    val dynamicQualifier = QualifierEntity(IdentifierEntity("tsstdlib"), IdentifierEntity("dynamic"))

    protected fun translateTypeValue(typeValue: TypeValueModel): KTypeExpression {
        return if (typeValue.fqName == dynamicQualifier || translateSimpleName(typeValue.value) == "dynamic") {
            target.dynamicType
        } else {
            nullable(
                KTypeReference(
                    KName(`package`, translateSimpleName(typeValue.value)),
                    typeValue.params.map { translateTypeParameter(it) }
                ),
                typeValue.nullable
            )
        }
    }

    private fun nullable(type: KTypeExpression, nullable: Boolean): KTypeExpression {
        return if (!nullable) {
            type
        } else {
            KNullableType(type)
        }
    }

    protected fun translateTypeParameter(typeParameter: TypeParameterModel): KTypeExpression {
        return nullable(translateType(typeParameter.type), typeParameter.nullable) //TODO?
    }

    protected fun translateFunctionType(functionType: FunctionTypeModel): KTypeExpression {
        return nullable(
            KLambdaTypeExpression(
                functionType.parameters.map { translateLambdaParameter(it) },
                translateType(functionType.type)
            ),
            functionType.nullable
        )
    }

    protected fun translateLambdaParameter(lambdaParameter: LambdaParameterModel): KParameter {
        return KParameter(lambdaParameter.name, translateType(lambdaParameter.type), false, null)
    }

    protected fun translateTypeParameterReference(typeParameterReference: TypeParameterReferenceModel): KTypeReference {
        return KTypeReference(KName(`package`, translateSimpleName(typeParameterReference.name)))
    }

    protected fun translateMember(
        member: MemberModel,
        allowOpen: Boolean,
        requiresImplementation: Boolean
    ): KMemberDeclaration? {
        return when (member) {
            is InitBlockModel -> translateInitBlock(member)
            is ClassModel -> translateMemberClass(member)
            is InterfaceModel -> translateMemberInterface(member)
            is ConstructorModel -> translateConstructor(member)
            is MethodModel -> translateMethod(member, allowOpen, requiresImplementation)
            is PropertyModel -> translateProperty(member, allowOpen, requiresImplementation)
            else -> TODO()
        }
    }

    protected fun translateInitBlock(initBlock: InitBlockModel): KMemberDeclaration? = null

    protected fun translateMemberClass(`class`: ClassModel): KMemberDeclaration? = TODO()
    protected fun translateMemberInterface(`interface`: InterfaceModel): KMemberDeclaration? = TODO()

    protected fun translateConstructor(constructor: ConstructorModel): KMemberDeclaration {
        return KMemberDeclaration(
            null,
            actual,
            false,
            external,
            false,
            false,
            false,
            KConstructorType(
                constructor.parameters.map { translateParameter(it) },
                null
            )
        )
    }

    protected fun translateMethod(
        method: MethodModel,
        allowOpen: Boolean,
        requiresImplementation: Boolean
    ): KMemberDeclaration? {
        val overrides = !method.override.isNullOrEmpty()
        val shouldInclude = (!overrides || (requiresImplementation && target == Target.OTHER))
                || overrides
        if (shouldInclude) {
            return KMemberDeclaration(
                translateSimpleName(method.name),
                actual,
                actual && !external && !requiresImplementation,
                false,
                allowOpen,//method.open
                overrides,
                false,
                KMethodType(
                    KLambdaTypeExpression(
                        method.parameters.map { translateParameter(it) },
                        translateType(method.type)
                    ),
//                method.body?.let { translateBlockStatement(it) }
                    if (actual && !external && requiresImplementation) externalValue else null
                )
            )
        } else {
            return null
        }
    }

    protected fun translateProperty(
        property: PropertyModel,
        allowOpen: Boolean,
        requiresImplementation: Boolean
    ): KMemberDeclaration? {
        val overrides = !property.override.isNullOrEmpty()
        val shouldInclude = (!overrides || (requiresImplementation && target == Target.OTHER))
                || overrides
        if (shouldInclude) {
            return KMemberDeclaration(
                translateSimpleName(property.name),
                actual,
                false,
                false,
                property.open,
                overrides,
                false,
                KPropertyType(
                    translateType(property.type),
                    !property.immutable,
                    if (actual && !external && requiresImplementation) externalValue else null,
                    if (actual && !external && !property.immutable && requiresImplementation) externalValue else null
                )
            )
        } else {
            return null
        }
    }

    protected fun translateSimpleName(nameEntity: NameEntity): String {
        var name = when (nameEntity) {
            is IdentifierEntity -> nameEntity.value
            is QualifierEntity -> nameEntity.right.value
        }
        if (name.startsWith('`')) {
            name = name.substring(1, name.length - 1)
        }
        return name
    }

    private fun translateParameter(parameter: ParameterModel): KParameter {
        return KParameter(
            parameter.name,
            translateType(parameter.type),
            parameter.vararg,
            if (!actual) parameter.initializer?.let { translateStatement(it) } else null
        )
    }

    private fun translateStatement(statement: StatementModel): KValueExpression = externalValue

    private fun translateBlockStatement(blockStatement: BlockStatementModel): KBody = externalValue


    //TODO: Nullable
}
