package org.carrat.webidl.build.compile.compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.carrat.webidl.build.compile.model.kotlinir.*
import org.carrat.webidl.build.compile.model.webidlir.*
import org.carrat.webidl.build.compile.model.webidlir.Enum
import org.carrat.webidl.build.compile.model.webidlir.Iterable
import org.carrat.webidl.build.compile.model.webidlir.types.*
import java.nio.file.Path

class Compiler(
    private val commonOutputDirectory: Path,
    private val jsOutputDirectory: Path,
    private val otherOutputDirectories: Collection<Path>,
    private val packageName: String,
    private val ignoreDeclarations: Set<String>
) {
    fun compile(declarations: Collection<Declaration>) {
        val declarationsMap = declarations.map { it.identifier.name to it }.toMap()
        for (declaration in declarations) {
            if (ignoreDeclarations.contains(declaration.identifier.name)) {
                continue
            }
            when (declaration) {
                is Interface -> {
                    commonInterface(declaration, declarationsMap)
                    jsInterface(declaration, declarationsMap)
                    otherInterface(declaration, declarationsMap)
                }
                is Dictionary -> {
                    commonDictionary(declaration, declarationsMap)
                    jsDictionary(declaration, declarationsMap)
                    otherDictionary(declaration, declarationsMap)
                }
                is Namespace -> {
                    commonNamespace(declaration, declarationsMap)
                    jsNamespace(declaration, declarationsMap)
                    otherNamespace(declaration, declarationsMap)
                }
                is Callback -> {
                    val builder = TypeAliasSpec.builder(
                        declaration.identifier.name, LambdaTypeName.get(
                            null,
                            parameters = getParameters(
                                declaration.arguments, declarationsMap,
                                dynamicSupported = false,
                                allowDefaultValue = false
                            ),
                            returnType = getTypeName(declaration.type, declarationsMap, false)
                        )
                    )
                    saveCommon(builder.build())
                }
                is Typedef -> {
                    val type = getTypeName(declaration.type, declarationsMap, false)
                    if (type != Dynamic) {
                        val builder = TypeAliasSpec.builder(declaration.identifier.name, type)
                        builder.modifiers += KModifier.PUBLIC
                        saveCommon(builder.build())
                    }
                }
                is CallbackInterface -> {
                    commonInterface(declaration, declarationsMap)
                    jsInterface(declaration, declarationsMap)
                    otherInterface(declaration, declarationsMap)
                }
                is Enum -> {
                    val fileSpec = FileSpec.builder(packageName, declaration.identifier.name)
                    val typeSpecBuilder = TypeAliasSpec.builder(declaration.identifier.name, String::class)
                    fileSpec.addTypeAlias(typeSpecBuilder.build())
//                    for(value in declaration.values) {
//                        val builder = PropertySpec.builder(normalizeName(value), String::class)
//                        builder.receiver(String.Companion::class)
//                        val fBuilder = FunSpec.getterBuilder()
//                        fBuilder.addCode("return \"${value}\"")
//                        builder.getter(fBuilder.build())
//                        fileSpec.addProperty(builder.build())
//                    }
                    saveCommon(fileSpec.build())
                }
                is InterfaceMixin -> {
                    commonInterface(declaration, declarationsMap, false)
                    jsInterface(declaration, declarationsMap, false)
                    otherInterface(declaration, declarationsMap, false)
                }
            }
        }
    }

    private fun commonNamespace(
        declaration: Namespace,
        declarationsMap: Map<String, Declaration>
    ) {
        val builder = TypeSpec.classBuilder(ClassName(packageName, declaration.identifier.name))
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.EXPECT
        builder.modifiers += KModifier.OPEN
        addMembers(
            builder,
            declaration.members,
            abstract = false,
            actual = false,
            dynamicSupported = false,
            external = false,
            declarations = declarationsMap
        )
        saveCommon(builder.build())
    }

    private fun jsNamespace(
        declaration: Namespace,
        declarationsMap: Map<String, Declaration>
    ) {
        val builder = TypeSpec.classBuilder(ClassName(packageName, declaration.identifier.name))
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.EXTERNAL
        builder.modifiers += KModifier.OPEN
        builder.modifiers += KModifier.ACTUAL
        addMembers(
            builder,
            declaration.members,
            abstract = false,
            actual = true,
            dynamicSupported = true,
            external = true,
            declarations = declarationsMap
        )
        saveJs(builder.build())
    }

    private fun otherNamespace(
        declaration: Namespace,
        declarationsMap: Map<String, Declaration>
    ) {
        val builder = TypeSpec.classBuilder(ClassName(packageName, declaration.identifier.name))
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.OPEN
        builder.modifiers += KModifier.ACTUAL
        addMembers(
            builder,
            declaration.members,
            abstract = false,
            actual = true,
            dynamicSupported = false,
            external = false,
            declarations = declarationsMap
        )
        saveOther(builder.build())
    }

    private fun commonDictionary(
        declaration: Dictionary,
        declarationsMap: Map<String, Declaration>
    ) {
        val builder = TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier.name))
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.EXPECT
        val inherits = declaration.inherits
        if (inherits != null) {
            builder.superinterfaces[ClassName(packageName, inherits.name)] = null
        }
        addMembers(
            builder, declaration.members,
            abstract = false,
            actual = false,
            dynamicSupported = false,
            external = false,
            declarations = declarationsMap
        )
        saveCommon(builder.build())
    }

    private fun jsDictionary(
        declaration: Dictionary,
        declarationsMap: Map<String, Declaration>
    ) {
        val builder = TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier.name))
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.EXTERNAL
        builder.modifiers += KModifier.ACTUAL
        val inherits = declaration.inherits
        if (inherits != null) {
            builder.superinterfaces[ClassName(packageName, inherits.name)] = null
        }
        addMembers(
            builder, declaration.members, false,
            actual = true,
            dynamicSupported = true,
            external = false,
            declarations = declarationsMap
        )
        saveJs(builder.build())
    }

    private fun otherDictionary(
        declaration: Dictionary,
        declarationsMap: Map<String, Declaration>
    ) {
        val builder = TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier.name))
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.ACTUAL
        val inherits = declaration.inherits
        if (inherits != null) {
            builder.superinterfaces[ClassName(packageName, inherits.name)] = null
        }
        addMembers(
            builder, declaration.members, false,
            actual = true,
            dynamicSupported = false,
            external = false,
            declarations = declarationsMap
        )
        saveOther(builder.build())
    }

    private fun commonInterface(
        declaration: AnyInterface,
        declarationsMap: Map<String, Declaration>,
        asClass: Boolean = true
    ) {
        val builder = if (asClass) {
            TypeSpec.classBuilder(ClassName(packageName, declaration.identifier.name))
        } else {
            TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier.name))
        }
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.EXPECT
        builder.modifiers += KModifier.ABSTRACT
        val inherits = declaration.inherits
        if (inherits != null) {
            builder.superclass(ClassName(packageName, inherits.name))
        }
        addMembers(
            builder, declaration.members,
            abstract = true,
            actual = false,
            dynamicSupported = false,
            external = false,
            declarations = declarationsMap
        )
        saveCommon(builder.build())
    }

    private fun jsInterface(
        declaration: AnyInterface,
        declarationsMap: Map<String, Declaration>,
        asClass: Boolean = true
    ) {
        val builder = if (asClass) {
            TypeSpec.classBuilder(ClassName(packageName, declaration.identifier.name))
        } else {
            TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier.name))
        }
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.EXTERNAL
        builder.modifiers += KModifier.ABSTRACT
        builder.modifiers += KModifier.ACTUAL
        val inherits = declaration.inherits
        if (inherits != null) {
            builder.superclass(ClassName(packageName, inherits.name))
        }
        addMembers(
            builder, declaration.members,
            abstract = true,
            actual = true,
            dynamicSupported = true,
            external = true,
            declarations = declarationsMap
        )
        saveJs(builder.build())
    }

    private fun otherInterface(
        declaration: AnyInterface,
        declarationsMap: Map<String, Declaration>,
        asClass: Boolean = true
    ) {
        val builder = if (asClass) {
            TypeSpec.classBuilder(ClassName(packageName, declaration.identifier.name))
        } else {
            TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier.name))
        }
        builder.modifiers += KModifier.PUBLIC
        builder.modifiers += KModifier.ABSTRACT
        builder.modifiers += KModifier.ACTUAL
        val inherits = declaration.inherits
        if (inherits != null) {
            builder.superclass(ClassName(packageName, inherits.name))
        }
        addMembers(
            builder, declaration.members,
            abstract = true,
            actual = true,
            dynamicSupported = false,
            external = false,
            declarations = declarationsMap
        )
        saveOther(builder.build())
    }

    private fun normalizeName(name: String): String {
        return name.replace(Regex("[^A-Za-z]"), "_")
    }

    private fun saveCommon(typeAliasSpec: TypeAliasSpec) {
        val builder = FileSpec.builder(packageName, typeAliasSpec.name)
        builder.addTypeAlias(typeAliasSpec)
        saveCommon(builder.build())
    }

    private fun saveCommon(typeSpec: TypeSpec) {
        val builder = FileSpec.builder(packageName, typeSpec.name!!)
        builder.addType(typeSpec)
        saveCommon(builder.build())
    }

    private fun saveCommon(fileSpec: FileSpec) {
        try {
            fileSpec.writeTo(commonOutputDirectory)
        } catch (e: IllegalArgumentException) {
            System.err.println("Failed to write " + fileSpec.name)
        }
    }

    private fun saveJs(typeSpec: TypeSpec) {
        val builder = FileSpec.builder(packageName, typeSpec.name!!)
        builder.addType(typeSpec)
        saveJs(builder.build())
    }

    private fun saveJs(fileSpec: FileSpec) {
        try {
            fileSpec.writeTo(jsOutputDirectory)
        } catch (e: IllegalArgumentException) {
            System.err.println("Failed to write " + fileSpec.name)
        }
    }

    private fun saveOther(typeSpec: TypeSpec) {
        val builder = FileSpec.builder(packageName, typeSpec.name!!)
        builder.addType(typeSpec)
        saveOther(builder.build())
    }

    private fun saveOther(fileSpec: FileSpec) {
        otherOutputDirectories.forEach {
            try {
                fileSpec.writeTo(it)
            } catch (e: IllegalArgumentException) {
                System.err.println("Failed to write " + fileSpec.name)
            }
        }
    }

    private fun addMembers(
        builder: TypeSpec.Builder,
        members: List<Member>,
        abstract: Boolean,
        actual: Boolean,
        dynamicSupported: Boolean,
        external: Boolean,
        declarations: Map<String, Declaration>
    ) {
        val memberDeclarations: MutableSet<MemberDeclaration> = linkedSetOf()
        for (member in members) {
            when (member) {
                is DictionaryEntry -> {
                    //TODO
                }
                is AsyncIterable -> {
                    //TODO
                }
                is Iterable -> {
                    //TODO
                }
                is SetLike -> {
                    //TODO
                }
                is MapLike -> {
                    //TODO
                }
                is Stringifier -> {
                    //TODO
                }
                is Includes -> {
                    builder.addSuperinterface(ClassName(packageName, member.right.name))
                }
                is Constructor -> {
                    val constructorBuilder = FunSpec.constructorBuilder()
                    if (actual) {
                        constructorBuilder.addModifiers(KModifier.ACTUAL)
                    }
                    if (!external) {
                        constructorBuilder.addCode("jsOnly()")
                    }
                    constructorBuilder.addParameters(getParameters(member.arguments, declarations, dynamicSupported))
                }
                is Operation -> {
                    val operationName = member.name
                    if (operationName is OperationName.Reference) {
                        val memberDeclaration = MethodDeclaration(
                            operationName.identifier.name,
                            LambdaType(
                                getKParameters(member.arguments, declarations),
                                getKType(member.type, declarations)
                            ),
                            actual,
                            abstract,
                            external
                        )
                        memberDeclarations.add(memberDeclaration.unifyConflicts(memberDeclarations))
                    } else {
                        System.err.println("Encountered operation with name $operationName")
                    }
                }
                is Attribute -> {
                    val memberDeclaration = PropertyDeclaration(
                        (member.name as AttributeName.Reference).identifier.name,
                        getKType(member.type, declarations),
                        member.inherited,
                        !member.readOnly,
                        actual,
                        abstract,
                        external
                    )
                    if (!memberDeclarations.add(memberDeclaration)) {
                        System.err.println("Duplicated member $memberDeclaration")
                    }
                }
            }
        }
        memberDeclarations.forEach { it.addTo(builder, dynamicSupported) }

        val companionObjectBuilder = TypeSpec.companionObjectBuilder()
        if (actual) {
            companionObjectBuilder.addModifiers(KModifier.ACTUAL)
        }
        val coMemberDeclarations: MutableSet<MemberDeclaration> = linkedSetOf()
        for (member in members) {
            when (member) {
                is Static -> {
                    when (val actualMember = member.actual) {
                        is Operation -> {
                            val operationName = actualMember.name
                            if (operationName is OperationName.Reference) {
                                val memberDeclaration = MethodDeclaration(
                                    operationName.identifier.name,
                                    LambdaType(
                                        getKParameters(actualMember.arguments, declarations),
                                        getKType(actualMember.type, declarations)
                                    ),
                                    actual,
                                    false,
                                    external
                                )
                                coMemberDeclarations.add(memberDeclaration.unifyConflicts(coMemberDeclarations))
                            } else {
                                System.err.println("Encountered operation with name $operationName")
                            }
                        }
                        is Attribute -> {
                            val memberDeclaration = PropertyDeclaration(
                                (actualMember.name as AttributeName.Reference).identifier.name,
                                getKType(actualMember.type, declarations),
                                actualMember.inherited,
                                !actualMember.readOnly,
                                actual,
                                false,
                                external
                            )
                            if (!coMemberDeclarations.add(memberDeclaration)) {
                                System.err.println("Duplicated member $memberDeclaration")
                            }
                        }
                    }
                }
            }
        }
        coMemberDeclarations.forEach { it.addTo(companionObjectBuilder, dynamicSupported, true) }
        if (coMemberDeclarations.isNotEmpty()) {
            builder.addType(companionObjectBuilder.build())
        }
    }

    private fun getParameters(
        arguments: List<Argument>,
        declarations: Map<String, Declaration>,
        dynamicSupported: Boolean,
        allowDefaultValue: Boolean = true
    ) = arguments.map {
        val argumentName = it.argumentName
        val name = if (argumentName is ArgumentName.Reference) {
            argumentName.identifier.name
        } else {
            System.err.println("Encountered argument with name " + it.argumentName)
            "unknown"
        }
        var type = getTypeName(it.type, declarations, dynamicSupported)
        if (it.optional && it.default == null && type != Dynamic) {
            type = type.copy(true)
        }
        val parameterBuilder = ParameterSpec.builder(name, type)
        if (it.vararg && allowDefaultValue) {
            parameterBuilder.addModifiers(KModifier.VARARG)
        }
        if (it.default != null && allowDefaultValue) {
            parameterBuilder.defaultValue("definedExternally")
        }
        parameterBuilder.build()
    }

    private fun getKParameters(
        arguments: List<Argument>,
        declarations: Map<String, Declaration>,
        allowDefaultValue: Boolean = true
    ) = arguments.map {
        val argumentName = it.argumentName
        val name = if (argumentName is ArgumentName.Reference) {
            argumentName.identifier.name
        } else {
            System.err.println("Encountered argument with name " + it.argumentName)
            null
        }
        var type = getKType(it.type, declarations)
        if (it.optional && it.default == null && type is NamedType) {
            type = KNullableType(type)
        }
        val defaultValue = if (it.default != null && allowDefaultValue) DefinedExternally else null
        Parameter(name, type, it.vararg, defaultValue)
    }

    private fun getTypeName(
        type: Type,
        declarations: Map<String, Declaration>,
        dynamicSupported: Boolean,
        nullable: Boolean = false
    ): TypeName {
        if (isEffectivelyDynamic(type, declarations)) {
            return if (dynamicSupported) {
                Dynamic
            } else {
                ClassName("kotlin", "Any").copy(true)
            }
        }
        return when (type) {
            is PromiseType -> ClassName(packageName, "Promise").parameterizedBy(
                getTypeName(
                    type.memberType,
                    declarations,
                    dynamicSupported
                )
            ).copy(nullable)
            is NullableType -> getTypeName(type.baseType, declarations, dynamicSupported, true)
            is StringType -> ClassName("kotlin", "String").copy(nullable)
            is TypeReference -> ClassName(packageName, type.identifier.name).copy(nullable)
            is BooleanType -> ClassName("kotlin", "Boolean").copy(nullable)
            is ByteType -> ClassName("kotlin", "Byte").copy(nullable)
            is FloatType -> when (type.baseType) {
                FloatBaseType.FLOAT -> ClassName("kotlin", "Float").copy(nullable)
                FloatBaseType.DOUBLE -> ClassName("kotlin", "Double").copy(nullable)
            }
            is IntegerType -> when (type.baseType) {
//                IntegerBaseType.SHORT -> (if(type.unsigned) ClassName("kotlin","UShort") else ClassName("kotlin","Short")).copy(nullable)
//                IntegerBaseType.LONG -> (if(type.unsigned) ClassName("kotlin","UInt") else ClassName("kotlin","Int")).copy(nullable)
//                IntegerBaseType.LONG_LONG -> (if(type.unsigned) ClassName("kotlin","ULong") else ClassName("kotlin","Long")).copy(nullable)
                IntegerBaseType.SHORT -> ClassName("kotlin", "Short").copy(nullable)
                IntegerBaseType.LONG -> ClassName("kotlin", "Int").copy(nullable)
                IntegerBaseType.LONG_LONG -> ClassName("kotlin", "Long").copy(nullable)
            }
            is VoidType -> ClassName("kotlin", "Unit")

            else -> throw NotImplementedError("Unknown type: $type")
        }
    }

    private fun getKType(type: Type, declarations: Map<String, Declaration>): KType {
        if (isEffectivelyDynamic(type, declarations)) {
            return KDynamic
        }
        return when (type) {
            is PromiseType -> NamedType(packageName, "Promise", getKType(type.memberType, declarations))
            is NullableType -> {
                val kType = getKType(type.baseType, declarations)
                if (kType is NamedType && !isEffectivelyNullable(type.baseType, declarations)) {
                    KNullableType(kType)
                } else {
                    kType
                }
            }
            is StringType -> NamedType("kotlin", "String")
            is TypeReference -> NamedType(packageName, type.identifier.name)
            is BooleanType -> NamedType("kotlin", "Boolean")
            is ByteType -> NamedType("kotlin", "Byte")
            is FloatType -> when (type.baseType) {
                FloatBaseType.FLOAT -> NamedType("kotlin", "Float")
                FloatBaseType.DOUBLE -> NamedType("kotlin", "Double")
            }
            is IntegerType -> when (type.baseType) {
//                IntegerBaseType.SHORT -> (if(type.unsigned) NamedType("kotlin","UShort") else NamedType("kotlin","Short"))
//                IntegerBaseType.LONG -> (if(type.unsigned) NamedType("kotlin","UInt") else NamedType("kotlin","Int"))
//                IntegerBaseType.LONG_LONG -> (if(type.unsigned) NamedType("kotlin","ULong") else NamedType("kotlin","Long"))
                IntegerBaseType.SHORT -> NamedType("kotlin", "Short")
                IntegerBaseType.LONG -> NamedType("kotlin", "Int")
                IntegerBaseType.LONG_LONG -> NamedType("kotlin", "Long")
            }
            is VoidType -> NamedType("kotlin", "Unit")

            else -> throw NotImplementedError("Unknown type: $type")
        }
    }

    private fun isEffectivelyDynamic(identifier: String, declarations: Map<String, Declaration>): Boolean {
        return when (val declaration = declarations.get(identifier)) {
            is Typedef -> isEffectivelyDynamic(declaration.type, declarations)
            else -> false
        }
    }

    private fun isEffectivelyDynamic(type: Type, declarations: Map<String, Declaration>): Boolean {
        return when (type) {
            is UnionType -> true
            is AnyType -> true
            is PromiseType -> false
            is ObservableArrayType -> true
            is FrozenArrayType -> true
            is SymbolType -> true
            is ObjectType -> true
            is SequenceType -> true
            is NullableType -> isEffectivelyDynamic(type.baseType, declarations)
            is RecordType -> true
            is BufferRelatedType -> true
            is StringType -> false
            is TypeReference -> isEffectivelyDynamic(type.identifier.name, declarations)
            is UndefinedType -> true
            is OctetType -> true
            is BigIntType -> true
            is PrimitiveType -> false
            is VoidType -> false
            else -> throw NotImplementedError("Unknown type: $type")
        }
    }

    private fun isEffectivelyNullable(identifier: String, declarations: Map<String, Declaration>): Boolean {
        return when (val declaration = declarations.get(identifier)) {
            is Typedef -> isEffectivelyNullable(declaration.type, declarations)
            else -> false
        }
    }

    private fun isEffectivelyNullable(type: Type, declarations: Map<String, Declaration>): Boolean {
        return when (type) {
            is NullableType -> true
            is TypeReference -> isEffectivelyNullable(type.identifier.name, declarations)
            else -> false
        }
    }
}