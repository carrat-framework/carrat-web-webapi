//package org.carrat.webidl.build.compile.compiler
//
//import com.squareup.kotlinpoet.*
//import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
//import org.carrat.webidl.build.compile.model.kotlinir.*
//import org.carrat.webidl.build.compile.model.webidlir.*
//import org.carrat.webidl.build.compile.model.webidlir.Enum
//import org.carrat.webidl.build.compile.model.webidlir.Iterable
//import org.carrat.webidl.build.compile.model.webidlir.types.*
//import org.carrat.webidl.build.compile.model.webidlir.literals.*
//import java.nio.file.Path
//
//class Compiler(
//    private val commonOutputDirectory: Path,
//    private val jsOutputDirectory: Path,
//    private val otherOutputDirectories: Collection<Path>,
//    private val packageName: String,
//    private val ignoreDeclarations: Set<String>
//) {
//    fun compile(declarations: Collection<WDeclaration>) {
//        val declarationsMap = declarations.map { it.identifier to it }.toMap()
//        for (declaration in declarations) {
//            if (ignoreDeclarations.contains(declaration.identifier)) {
//                continue
//            }
//            when (declaration) {
//                is WInterface -> {
//                    commonInterface(declaration, declarationsMap)
//                    jsInterface(declaration, declarationsMap)
//                    otherInterface(declaration, declarationsMap)
//                }
//                is Dictionary -> {
//                    commonDictionary(declaration, declarationsMap)
//                    jsDictionary(declaration, declarationsMap)
//                    otherDictionary(declaration, declarationsMap)
//                }
//                is Namespace -> {
//                    commonNamespace(declaration, declarationsMap)
//                    jsNamespace(declaration, declarationsMap)
//                    otherNamespace(declaration, declarationsMap)
//                }
//                is Callback -> {
//                    val builder = TypeAliasSpec.builder(
//                        declaration.identifier, LambdaTypeName.get(
//                            null,
//                            parameters = getParameters(
//                                declaration.arguments, declarationsMap,
//                                dynamicSupported = false,
//                                allowDefaultValue = false
//                            ),
//                            returnType = getTypeName(declaration.type, declarationsMap, false, false, false)
//                        )
//                    )
//                    saveCommon(builder.build())
//                }
//                is Typedef -> {
//                    val type = getTypeName(declaration.type, declarationsMap, false, false, false)
//                    if (type != Dynamic) {
//                        val builder = TypeAliasSpec.builder(declaration.identifier, type)
//                        builder.modifiers += KModifier.PUBLIC
//                        saveCommon(builder.build())
//                    }
//                }
//                is CallbackInterface -> {
//                    commonInterface(declaration, declarationsMap, false)
//                    jsInterface(declaration, declarationsMap, false)
//                    otherInterface(declaration, declarationsMap, false)
//                }
//                is Enum -> {
//                    val fileSpec = FileSpec.builder(packageName, declaration.identifier)
//                    val typeSpecBuilder = TypeAliasSpec.builder(declaration.identifier, String::class)
//                    fileSpec.addTypeAlias(typeSpecBuilder.build())
////                    for(value in declaration.values) {
////                        val builder = PropertySpec.builder(normalizeName(value), String::class)
////                        builder.receiver(String.Companion::class)
////                        val fBuilder = FunSpec.getterBuilder()
////                        fBuilder.addCode("return \"${value}\"")
////                        builder.getter(fBuilder.build())
////                        fileSpec.addProperty(builder.build())
////                    }
//                    saveCommon(fileSpec.build())
//                }
//                is InterfaceMixin -> {
//                    commonInterface(declaration, declarationsMap, false)
//                    jsInterface(declaration, declarationsMap, false)
//                    otherInterface(declaration, declarationsMap, false)
//                }
//            }
//        }
//    }
//
//    private fun commonNamespace(
//        declaration: Namespace,
//        declarationsMap: Map<String, WDeclaration>
//    ) {
//        val builder = TypeSpec.classBuilder(ClassName(packageName, declaration.identifier))
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.EXPECT
//        builder.modifiers += KModifier.OPEN
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = false,
//            actual = false,
//            dynamicSupported = false,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveCommon(builder.build())
//    }
//
//    private fun jsNamespace(
//        declaration: Namespace,
//        declarationsMap: Map<String, WDeclaration>
//    ) {
//        val builder = TypeSpec.classBuilder(ClassName(packageName, declaration.identifier))
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.EXTERNAL
//        builder.modifiers += KModifier.OPEN
//        builder.modifiers += KModifier.ACTUAL
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = false,
//            actual = true,
//            dynamicSupported = true,
//            external = true,
//            declarations = declarationsMap
//        )
//        saveJs(builder.build())
//    }
//
//    private fun otherNamespace(
//        declaration: Namespace,
//        declarationsMap: Map<String, WDeclaration>
//    ) {
//        val builder = TypeSpec.classBuilder(ClassName(packageName, declaration.identifier))
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.OPEN
//        builder.modifiers += KModifier.ACTUAL
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = false,
//            actual = true,
//            dynamicSupported = false,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveOther(builder.build())
//    }
//
//    private fun commonDictionary(
//        declaration: Dictionary,
//        declarationsMap: Map<String, WDeclaration>
//    ) {
//        val builder = TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier))
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.EXPECT
//        val inherits = declaration.inherits
//        if (inherits != null) {
//            builder.superinterfaces[ClassName(packageName, inherits)] = null
//        }
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = false,
//            actual = false,
//            dynamicSupported = false,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveCommon(builder.build())
//    }
//
//    private fun jsDictionary(
//        declaration: Dictionary,
//        declarationsMap: Map<String, WDeclaration>
//    ) {
//        val builder = TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier))
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.EXTERNAL
//        builder.modifiers += KModifier.ACTUAL
//        val inherits = declaration.inherits
//        if (inherits != null) {
//            builder.superinterfaces[ClassName(packageName, inherits)] = null
//        }
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            false,
//            actual = true,
//            dynamicSupported = true,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveJs(builder.build())
//    }
//
//    private fun otherDictionary(
//        declaration: Dictionary,
//        declarationsMap: Map<String, WDeclaration>
//    ) {
//        val builder = TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier))
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.ACTUAL
//        val inherits = declaration.inherits
//        if (inherits != null) {
//            builder.superinterfaces[ClassName(packageName, inherits)] = null
//        }
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            false,
//            actual = true,
//            dynamicSupported = false,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveOther(builder.build())
//    }
//
//    private fun commonInterface(
//        declaration: AnyInterface,
//        declarationsMap: Map<String, WDeclaration>,
//        asClass: Boolean = true
//    ) {
//        val builder = if (asClass) {
//            TypeSpec.classBuilder(ClassName(packageName, declaration.identifier))
//        } else {
//            TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier))
//        }
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.EXPECT
//        builder.modifiers += KModifier.ABSTRACT
//        val inherits = declaration.inherits
//        if (inherits != null) {
//            builder.superclass(ClassName(packageName, inherits))
//        }
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = true,
//            actual = false,
//            dynamicSupported = false,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveCommon(builder.build())
//    }
//
//    private fun jsInterface(
//        declaration: AnyInterface,
//        declarationsMap: Map<String, WDeclaration>,
//        asClass: Boolean = true
//    ) {
//        val builder = if (asClass) {
//            TypeSpec.classBuilder(ClassName(packageName, declaration.identifier))
//        } else {
//            TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier))
//        }
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.EXTERNAL
//        builder.modifiers += KModifier.ABSTRACT
//        builder.modifiers += KModifier.ACTUAL
//        val inherits = declaration.inherits
//        if (inherits != null) {
//            builder.superclass(ClassName(packageName, inherits))
//        }
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = true,
//            actual = true,
//            dynamicSupported = true,
//            external = true,
//            declarations = declarationsMap
//        )
//        saveJs(builder.build())
//    }
//
//    private fun otherInterface(
//        declaration: AnyInterface,
//        declarationsMap: Map<String, WDeclaration>,
//        asClass: Boolean = true
//    ) {
//        val builder = if (asClass) {
//            TypeSpec.classBuilder(ClassName(packageName, declaration.identifier))
//        } else {
//            TypeSpec.interfaceBuilder(ClassName(packageName, declaration.identifier))
//        }
//        builder.modifiers += KModifier.PUBLIC
//        builder.modifiers += KModifier.ABSTRACT
//        builder.modifiers += KModifier.ACTUAL
//        val inherits = declaration.inherits
//        if (inherits != null) {
//            builder.superclass(ClassName(packageName, inherits))
//        }
//        addMembers(
//            builder,
//            declaration,
//            declaration.members,
//            abstract = true,
//            actual = true,
//            dynamicSupported = false,
//            external = false,
//            declarations = declarationsMap
//        )
//        saveOther(builder.build())
//    }
//
//    private fun normalizeName(name: String): String {
//        return name.replace(Regex("[^A-Za-z]"), "_")
//    }
//
//    private fun saveCommon(typeAliasSpec: TypeAliasSpec) {
//        val builder = FileSpec.builder(packageName, typeAliasSpec.name)
//        builder.addTypeAlias(typeAliasSpec)
//        saveCommon(builder.build())
//    }
//
//    private fun saveCommon(typeSpec: TypeSpec) {
//        val builder = FileSpec.builder(packageName, typeSpec.name!!)
//        builder.addType(typeSpec)
//        saveCommon(builder.build())
//    }
//
//    private fun saveCommon(fileSpec: FileSpec) {
//        try {
//            fileSpec.writeTo(commonOutputDirectory)
//        } catch (e: IllegalArgumentException) {
//            System.err.println("Failed to write " + fileSpec.name)
//        }
//    }
//
//    private fun saveJs(typeSpec: TypeSpec) {
//        val builder = FileSpec.builder(packageName, typeSpec.name!!)
//        builder.addType(typeSpec)
//        saveJs(builder.build())
//    }
//
//    private fun saveJs(fileSpec: FileSpec) {
//        try {
//            fileSpec.writeTo(jsOutputDirectory)
//        } catch (e: IllegalArgumentException) {
//            System.err.println("Failed to write " + fileSpec.name)
//        }
//    }
//
//    private fun saveOther(typeSpec: TypeSpec) {
//        val builder = FileSpec.builder(packageName, typeSpec.name!!)
//        builder.addType(typeSpec)
//        saveOther(builder.build())
//    }
//
//    private fun saveOther(fileSpec: FileSpec) {
//        otherOutputDirectories.forEach {
//            try {
//                fileSpec.writeTo(it)
//            } catch (e: IllegalArgumentException) {
//                System.err.println("Failed to write " + fileSpec.name)
//            }
//        }
//    }
//
//    private fun addMembers(
//        builder: TypeSpec.Builder,
//        declaration: WDeclaration,
//        members: List<WMember>,
//        abstract: Boolean,
//        actual: Boolean,
//        dynamicSupported: Boolean,
//        external: Boolean,
//        declarations: Map<String, WDeclaration>
//    ) {
//        val supertypes = getAllSupertypes(declaration, declarations)
//        val memberDeclarations: MutableSet<MemberDeclaration> = linkedSetOf()
//        for (member in members) {
//            when (member) {
//                is DictionaryEntry -> {
//                    //TODO
//                }
//                is AsyncIterable -> {
//                    //TODO
//                }
//                is Iterable -> {
//                    //TODO
//                }
//                is SetLike -> {
//                    //TODO
//                }
//                is MapLikeExpression -> {
//                    //TODO
//                }
//                is Stringifier -> {
//                    //TODO
//                }
//                is Includes -> {
//                    builder.addSuperinterface(ClassName(packageName, member.right))
//                }
//                is Constructor -> {
//                    val constructorBuilder = FunSpec.constructorBuilder()
//                    if (actual) {
//                        constructorBuilder.addModifiers(KModifier.ACTUAL)
//                    }
//                    if (!external) {
//                        constructorBuilder.addCode("jsOnly()")
//                    }
//                    constructorBuilder.addParameters(
//                        getParameters(
//                            member.arguments,
//                            declarations,
//                            dynamicSupported,
//                            !actual
//                        )
//                    )
//                }
//                is Operation -> {
//                    val operationName = member.name
//                    if (!supertypes.any { it.members.contains(member) }) {
//                        if (operationName is OperationName.Reference) {
//                            val memberDeclaration = MethodDeclaration(
//                                operationName.identifier,
//                                LambdaType(
//                                    getKParameters(member.arguments, declarations, !actual),
//                                    getKType(member.type, declarations, false)
//                                ),
//                                actual,
//                                abstract,
//                                external
//                            )
//                            memberDeclarations.add(memberDeclaration.unifyConflicts(memberDeclarations))
//                        } else {
//                            System.err.println("Encountered operation with name $operationName")
//                        }
//                    } else {
//                        System.err.println("Skipped inherited $operationName")
//                    }
//                }
//                is WAttribute -> {
//                    val name = member.name
//                    if (!supertypes.any { it.members.any { it is WAttribute && it.name == name }}) {
//                        if (name is AttributeName.Reference) {
//                            val memberDeclaration = PropertyDeclaration(
//                                name.identifier,
//                                getKType(member.type, declarations, false),
//                                member.inherited,
//                                !member.readOnly,
//                                actual,
//                                abstract,
//                                external
//                            )
//                            if (!memberDeclarations.add(memberDeclaration.unifyConflicts(memberDeclarations))) {
//                                System.err.println("Duplicated member $memberDeclaration")
//                            }
//                        } else {
//                            System.err.println("Encountered attribute with name $name")
//                        }
//                    } else {
//                        System.err.println("Skipped inherited $name")
//                    }
//                }
//            }
//        }
//        memberDeclarations.forEach { it.addTo(builder, dynamicSupported) }
//
//        val companionObjectBuilder = TypeSpec.companionObjectBuilder()
//        if (actual) {
//            companionObjectBuilder.addModifiers(KModifier.ACTUAL)
//        }
//        val coMemberDeclarations: MutableSet<MemberDeclaration> = linkedSetOf()
//        for (member in members) {
//            when (member) {
//                is Static -> {
//                    when (val actualMember = member.actual) {
//                        is Operation -> {
//                            val operationName = actualMember.name
//                            if (operationName is OperationName.Reference) {
//                                val memberDeclaration = MethodDeclaration(
//                                    operationName.identifier,
//                                    LambdaType(
//                                        getKParameters(actualMember.arguments, declarations, !actual),
//                                        getKType(actualMember.type, declarations, false)
//                                    ),
//                                    actual,
//                                    false,
//                                    external
//                                )
//                                coMemberDeclarations.add(memberDeclaration.unifyConflicts(coMemberDeclarations))
//                            } else {
//                                System.err.println("Encountered operation with name $operationName")
//                            }
//                        }
//                        is WAttribute -> {
//                            val memberDeclaration = PropertyDeclaration(
//                                (actualMember.name as AttributeName.Reference).identifier,
//                                getKType(actualMember.type, declarations, false),
//                                actualMember.inherited,
//                                !actualMember.readOnly,
//                                actual,
//                                false,
//                                external
//                            )
//                            if (!coMemberDeclarations.add(memberDeclaration.unifyConflicts(coMemberDeclarations))) {
//                                System.err.println("Duplicated member $memberDeclaration")
//                            }
//                        }
//                    }
//                }
//                is Constant -> {
//                    val memberDeclaration = PropertyDeclaration(
//                        member.identifier,
//                        getKType(member.type, declarations, false),
//                        false,
//                        true,
//                        actual,
//                        false,
//                        external
//                    )
//                    if (!coMemberDeclarations.add(memberDeclaration)) {
//                        System.err.println("Duplicated member $memberDeclaration")
//                    }
//                }
//            }
//        }
//        coMemberDeclarations.forEach { it.addTo(companionObjectBuilder, dynamicSupported, true) }
//        if (coMemberDeclarations.isNotEmpty()) {
//            builder.addType(companionObjectBuilder.build())
//            val annotationBuilder = AnnotationSpec.builder(ClassName("kotlin", "Suppress"))
//            annotationBuilder.addMember("\"NESTED_CLASS_IN_EXTERNAL_INTERFACE\"")
//            builder.addAnnotation(annotationBuilder.build())
//        }
//    }
//
//    private fun getAllSupertypes(
//        declaration: WDeclaration,
//        declarations: Map<String, WDeclaration>
//    ): Collection<WDeclaration> {
//        if (declaration is Typedef) {
//            val type = declaration.type
//            if (type is TypeReference) {
//                return getAllSupertypes(declarations.get(type.identifier)!!, declarations)
//            } else {
//                return emptySet()
//            }
//        } else {
//            val supertypes =
//                declaration.members.filterIsInstance<Includes>().map { declarations[it.right]!! }.toMutableSet()
//            val inherits = declaration.inherits
//            if (inherits != null) {
//                val inheritsDeclaration = declarations[inherits]
//                if(inheritsDeclaration != null) {
//                    supertypes.add(inheritsDeclaration)
//                } else {
//                    System.err.println("Missing declaration for inherited mixin ${inherits}.")
//                }
//            }
//            val inheritedSupertypes = supertypes.flatMap { getAllSupertypes(it, declarations) }
//            return supertypes + inheritedSupertypes
//        }
//    }
//
//    private fun getParameters(
//        arguments: List<WArgument>,
//        declarations: Map<String, WDeclaration>,
//        dynamicSupported: Boolean,
//        allowDefaultValue: Boolean = true
//    ) = arguments.map {
//        val argumentName = it.argumentName
//        val name = if (argumentName is ArgumentName.Reference) {
//            argumentName.identifier
//        } else {
//            System.err.println("Encountered argument with name " + it.argumentName)
//            "unknown"
//        }
//        var type = getTypeName(it.type, declarations, dynamicSupported, false, false)
//        if (it.optional && it.default == null && type != Dynamic) {
//            type = type.copy(true)
//        }
//        val parameterBuilder = ParameterSpec.builder(name, type)
//        if (it.vararg && allowDefaultValue) {
//            parameterBuilder.addModifiers(KModifier.VARARG)
//        }
//        if (it.default != null && allowDefaultValue) {
////            parameterBuilder.defaultValue("definedExternally")
//            parameterBuilder.defaultValue(renderValue(it.default, getKType(it.type, declarations, false), declarations))
//        }
//        parameterBuilder.build()
//    }
//
//    private fun renderValue(value: Literal): String {
//        return when (value) {
//            is BooleanLiteral -> if (value.value) "true" else "false"
//            is EmptyArrayLiteral -> "emptyArray()"
//            is EmptyObjectLiteral -> "emptyObject()"
//            is FloatLiteral -> "${value.value}f"
//            is IntegerLiteral -> value.value.toString()
//            is NullLiteral -> "null"
//            is StringLiteral -> "\"${escapeString(value.value)}\""
//            else -> throw NotImplementedError("Don't know how to render value $value")
//        }
//    }
//
//    @OptIn(ExperimentalUnsignedTypes::class)
//    private fun renderValue(value: Literal, type: KType, declarations: Map<String, WDeclaration>): String {
//        val eType = resolveAliases(type, declarations)
//        return when (value) {
//            is BooleanLiteral -> if (value.value) "true" else "false"
//            is EmptyArrayLiteral -> "emptyArray<${renderType(type, false)}>()"
//            is EmptyObjectLiteral -> "emptyObject()"
//            is FloatLiteral -> when (eType) {
//                NamedType("kotlin", "Double") -> {
//                    "%#f".format(value.value)
//                }
//                NamedType("kotlin", "Float") -> {
//                    "%#ff".format(value.value)
//                }
//                else -> throw NotImplementedError("Don't know how to convert value $value to $type")
//            }
//            is IntegerLiteral -> when (eType) {
//                NamedType("kotlin", "Byte") -> value.value.toByte().toString()
//                NamedType("kotlin", "Short") -> value.value.toShort().toString()
//                NamedType("kotlin", "Int") -> value.value.toInt().toString()
//                NamedType("kotlin", "Long") -> value.value.toString() + "L"
//                NamedType("kotlin", "UByte") -> value.value.toUByte().toString() + "u"
//                NamedType("kotlin", "UShort") -> value.value.toUShort().toString() + "u"
//                NamedType("kotlin", "UInt") -> value.value.toUInt().toString() + "u"
//                NamedType("kotlin", "ULong") -> value.value.toULong().toString() + "UL"
//                NamedType("kotlin", "Double") -> {
//                    "%#f".format(value.value)
//                }
//                NamedType("kotlin", "Float") -> {
//                    "%#ff".format(value.value)
//                }
//                else -> throw NotImplementedError("Don't know how to convert value $value to $type")
//            }
//            is NullLiteral -> "null"
//            is StringLiteral -> "\"${escapeString(value.value)}\""
//            else -> throw NotImplementedError("Don't know how to render value $value")
//        }
//    }
//
//    private fun resolveAliases(type: KType, declarations: Map<String, WDeclaration>) : KType {
//        if(type is NamedType && type.packageName == packageName) {
//            val name = type.name
//            val declaration = declarations[name]
//            if(declaration is Typedef) {
//                val aType = declaration.type
//                return resolveAliases(getKType(aType, declarations, false), declarations)
//            }
//        }
//        return type
//    }
//
//    private fun renderType(type: KType, dynamicSupported: Boolean): String {
//        return type.getPoetTypeName(dynamicSupported).toString()
//    }
//
//    private fun escapeString(value: String): String {
//        //TODO
//        return value
//    }
//
//    private fun getKParameters(
//        arguments: List<WArgument>,
//        declarations: Map<String, WDeclaration>,
//        allowDefaultValue: Boolean = true
//    ) = arguments.map {
//        val argumentName = it.argumentName
//        val name = when (argumentName) {
//            is ArgumentName.Reference -> argumentName.identifier
//            is ArgumentName.Keyword -> argumentName.keyword.keyword
//        }
//        var type = getKType(it.type, declarations, false)
//        if (it.optional && it.default == null && type is NamedType) {
//            type = KNullableType(type)
//        }
//        val defaultValue =
//            if (it.default != null && allowDefaultValue) CodeBlockKValue(renderValue(it.default, type, declarations)) else null
//        Parameter(name, type, it.vararg, defaultValue)
//    }
//
//    private fun getTypeName(
//        type: WType,
//        declarations: Map<String, WDeclaration>,
//        dynamicSupported: Boolean,
//        nullable: Boolean,
//        allowUnsigned: Boolean
//    ): TypeName {
//        if (isEffectivelyDynamic(type, declarations)) {
//            return if (dynamicSupported) {
//                Dynamic
//            } else {
//                ClassName("kotlin", "Any").copy(true)
//            }
//        }
//        return when (type) {
//            is PromiseType -> ClassName(packageName, "Promise").parameterizedBy(
//                getTypeName(
//                    type.memberType,
//                    declarations,
//                    dynamicSupported,
//                    false,
//                    allowUnsigned
//                )
//            ).copy(nullable)
//            is NullableTypeExpression -> getTypeName(type.baseType, declarations, dynamicSupported, true, false)
//            is StringType -> ClassName("kotlin", "String").copy(nullable)
//            is TypeReference -> ClassName(packageName, type.identifier).copy(nullable)
//            is BooleanType -> ClassName("kotlin", "Boolean").copy(nullable)
//            is ByteType -> ClassName("kotlin", "Byte").copy(nullable)
//            is FloatType -> when (type.baseType) {
//                FloatBaseType.FLOAT -> ClassName("kotlin", "Float").copy(nullable)
//                FloatBaseType.DOUBLE -> ClassName("kotlin", "Double").copy(nullable)
//            }
//            is IntegerType -> when (type.baseType) {
//                IntegerBaseType.SHORT -> (if (type.unsigned && allowUnsigned) ClassName(
//                    "kotlin",
//                    "UShort"
//                ) else ClassName("kotlin", "Short")).copy(nullable)
//                IntegerBaseType.LONG -> (if (type.unsigned && allowUnsigned) ClassName("kotlin", "UInt") else ClassName(
//                    "kotlin",
//                    "Int"
//                )).copy(nullable)
//                IntegerBaseType.LONG_LONG -> (if (type.unsigned && allowUnsigned) ClassName(
//                    "kotlin",
//                    "ULong"
//                ) else ClassName("kotlin", "Long")).copy(nullable)
////                IntegerBaseType.SHORT -> ClassName("kotlin", "Short").copy(nullable)
////                IntegerBaseType.LONG -> ClassName("kotlin", "Int").copy(nullable)
////                IntegerBaseType.LONG_LONG -> ClassName("kotlin", "Long").copy(nullable)
//            }
//            is VoidType -> ClassName("kotlin", "Unit")
//
//            else -> throw NotImplementedError("Unknown type: $type")
//        }
//    }
//
//    private fun getKType(type: WType, declarations: Map<String, WDeclaration>, allowUnsigned: Boolean): KType {
//        if (isEffectivelyDynamic(type, declarations)) {
//            return KDynamic
//        }
//        return when (type) {
//            is PromiseType -> NamedType(packageName, "Promise", getKType(type.memberType, declarations, false))
//            is NullableTypeExpression -> {
//                val kType = getKType(type.baseType, declarations, allowUnsigned)
//                if (kType is NamedType && !isEffectivelyNullable(type.baseType, declarations)) {
//                    KNullableType(kType)
//                } else {
//                    kType
//                }
//            }
//            is StringType -> NamedType("kotlin", "String")
//            is TypeReference -> NamedType(packageName, type.identifier)
//            is BooleanType -> NamedType("kotlin", "Boolean")
//            is ByteType -> NamedType("kotlin", "Byte")
//            is FloatType -> when (type.baseType) {
//                FloatBaseType.FLOAT -> NamedType("kotlin", "Float")
//                FloatBaseType.DOUBLE -> NamedType("kotlin", "Double")
//            }
//            is IntegerType -> when (type.baseType) {
//                IntegerBaseType.SHORT -> (if (type.unsigned && allowUnsigned) NamedType(
//                    "kotlin",
//                    "UShort"
//                ) else NamedType("kotlin", "Short"))
//                IntegerBaseType.LONG -> (if (type.unsigned && allowUnsigned) NamedType("kotlin", "UInt") else NamedType(
//                    "kotlin",
//                    "Int"
//                ))
//                IntegerBaseType.LONG_LONG -> (if (type.unsigned && allowUnsigned) NamedType(
//                    "kotlin",
//                    "ULong"
//                ) else NamedType("kotlin", "Long"))
////                IntegerBaseType.SHORT -> NamedType("kotlin", "Short")
////                IntegerBaseType.LONG -> NamedType("kotlin", "Int")
////                IntegerBaseType.LONG_LONG -> NamedType("kotlin", "Long")
//            }
//            is VoidType -> NamedType("kotlin", "Unit")
//
//            else -> throw NotImplementedError("Unknown type: $type")
//        }
//    }
//
//    private fun isEffectivelyDynamic(identifier: String, declarations: Map<String, WDeclaration>): Boolean {
//        return when (val declaration = declarations.get(identifier)) {
//            is Typedef -> isEffectivelyDynamic(declaration.type, declarations)
//            else -> false
//        }
//    }
//
//    private fun isEffectivelyDynamic(type: WType, declarations: Map<String, WDeclaration>): Boolean {
//        return when (type) {
//            is UnionTypeExpression -> true
//            is AnyType -> true
//            is PromiseType -> false
//            is ObservableArrayType -> true
//            is FrozenArrayType -> true
//            is SymbolType -> true
//            is ObjectType -> true
//            is SequenceType -> true
//            is NullableTypeExpression -> isEffectivelyDynamic(type.baseType, declarations)
//            is RecordType -> true
//            is BufferRelatedType -> true
//            is StringType -> false
//            is TypeReference -> isEffectivelyDynamic(type.identifier, declarations)
//            is UndefinedType -> true
//            is OctetType -> true
//            is BigIntType -> true
//            is PrimitiveType -> false
//            is VoidType -> false
//            else -> throw NotImplementedError("Unknown type: $type")
//        }
//    }
//
//    private fun isEffectivelyNullable(identifier: String, declarations: Map<String, WDeclaration>): Boolean {
//        return when (val declaration = declarations.get(identifier)) {
//            is Typedef -> isEffectivelyNullable(declaration.type, declarations)
//            else -> false
//        }
//    }
//
//    private fun isEffectivelyNullable(type: WType, declarations: Map<String, WDeclaration>): Boolean {
//        return when (type) {
//            is NullableTypeExpression -> true
//            is TypeReference -> isEffectivelyNullable(type.identifier, declarations)
//            else -> false
//        }
//    }
//}
