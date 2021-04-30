package org.carrat.webidl.build.compile.parser

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.carrat.webidl.build.compile.model.webidlir.*
import org.carrat.webidl.build.compile.model.webidlir.Iterable
import org.carrat.webidl.build.compile.model.webidlir.types.*
import org.carrat.webidl.build.compile.model.webidlir.value.*
import org.carrat.webidl.build.grammar.WebIdlParser

internal class IrParser(
    private val irBuilderContext: IrBuilderContext
) {
    fun processDocument(ctx: WebIdlParser.DocumentContext) {
        var definitions = ctx.definitions()
        while (definitions.definition() != null) {
            processDefinition(definitions.definition())
            definitions = definitions.definitions()
        }
    }

    private fun processDefinition(ctx: WebIdlParser.DefinitionContext) {
        when {
            ctx.callbackOrInterfaceOrMixin() != null -> processCallbackOrInterfaceOrMixin(ctx.callbackOrInterfaceOrMixin())
            ctx.namespace() != null -> processNamespace(ctx.namespace())
            ctx.partial() != null -> processPartial(ctx.partial())
            ctx.dictionary() != null -> processDictionary(ctx.dictionary())
            ctx.widlEnum() != null -> processWidlEnum(ctx.widlEnum())
            ctx.typedef() != null -> processTypedef(ctx.typedef())
            ctx.includesStatement() != null -> processIncludesStatement(ctx.includesStatement())
            else -> {
                throw IllegalArgumentException("Don't know how to process definition \"${ctx.text}\".")
            }
        }
    }

    private fun processCallbackOrInterfaceOrMixin(ctx: WebIdlParser.CallbackOrInterfaceOrMixinContext) {
        when {
            ctx.callbackRestOrInterface() != null -> processCallbackRestOrInterface(ctx.callbackRestOrInterface())
            ctx.interfaceOrMixin() != null -> processInterfaceOrMixin(ctx.interfaceOrMixin())
            else -> throw IllegalArgumentException("Don't know how to process callbackOrInterfaceOrMixin \"${ctx.text}\".")
        }
    }

    private fun processCallbackRestOrInterface(ctx: WebIdlParser.CallbackRestOrInterfaceContext) {
        when {
            ctx.callbackRest() != null -> processCallback(ctx.callbackRest())
            ctx.callbackInterfaceMembers() != null -> {
                val identifier = ctx.identifier().text
                val members = getCallbackInterfaceMembers(ctx.callbackInterfaceMembers())
                val declarationBuilder =
                    irBuilderContext.registerDeclaration(identifier, CitizenType.CALLBACK_INTERFACE)
                declarationBuilder.members.addAll(members)
            }
        }
    }

    private fun processInterfaceOrMixin(ctx: WebIdlParser.InterfaceOrMixinContext) {
        when {
            ctx.interfaceRest() != null -> processInterface(ctx.interfaceRest())
            ctx.mixinRest() != null -> processMixin(ctx.mixinRest())
            else -> {
                throw IllegalArgumentException("Don't know how to process interfaceOrMixin \"${ctx.text}\".")
            }
        }
    }

    private fun processInterface(ctx: WebIdlParser.InterfaceRestContext) {
        val identifier = ctx.identifier().text
        val inheritance = getInheritance(ctx.inheritance())
        val members = getInterfaceMembers(ctx.interfaceMembers())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.INTERFACE)
        if (inheritance != null) {
            declarationBuilder.inherits = inheritance //TODO: Check existing inheritance
        }
        declarationBuilder.members.addAll(members)
    }

    private fun processMixin(ctx: WebIdlParser.MixinRestContext) {
        val identifier = ctx.identifier().text
        val members = getMixinMembers(ctx.mixinMembers())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.MIXIN)
        declarationBuilder.members.addAll(members)
    }

    private fun processNamespace(ctx: WebIdlParser.NamespaceContext) {
        val identifier = ctx.identifier().text
        val members = getNamespaceMembers(ctx.namespaceMembers())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.NAMESPACE)
        declarationBuilder.members.addAll(members)
    }

    private fun getInterfaceMembers(ctx: WebIdlParser.InterfaceMembersContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        var interfaceMembers = ctx
        while (interfaceMembers.interfaceMember() != null) {
            members += getInterfaceMember(interfaceMembers.interfaceMember())
            interfaceMembers = interfaceMembers.interfaceMembers()
        }
        return members
    }

    private fun getMixinMembers(ctx: WebIdlParser.MixinMembersContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        var interfaceMembers = ctx
        while (interfaceMembers.mixinMember() != null) {
            members += getMixinMember(interfaceMembers.mixinMember())
            interfaceMembers = interfaceMembers.mixinMembers()
        }
        return members
    }

    private fun getNamespaceMembers(ctx: WebIdlParser.NamespaceMembersContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        var interfaceMembers = ctx
        while (interfaceMembers.namespaceMember() != null) {
            members += getNamespaceMember(interfaceMembers.namespaceMember())
            interfaceMembers = interfaceMembers.namespaceMembers()
        }
        return members
    }

    private fun getCallbackInterfaceMembers(ctx: WebIdlParser.CallbackInterfaceMembersContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        var interfaceMembers = ctx
        while (interfaceMembers.callbackInterfaceMember() != null) {
            members += getCallbackInterfaceMember(interfaceMembers.callbackInterfaceMember())
            interfaceMembers = interfaceMembers.callbackInterfaceMembers()
        }
        return members
    }

    private fun getPartialInterfaceMembers(ctx: WebIdlParser.PartialInterfaceMembersContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        var interfaceMembers = ctx
        while (interfaceMembers.partialInterfaceMember() != null) {
            members += getPartialInterfaceMember(interfaceMembers.partialInterfaceMember())
            interfaceMembers = interfaceMembers.partialInterfaceMembers()
        }
        return members
    }

    private fun getDictionaryMembers(ctx: WebIdlParser.DictionaryMembersContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        var interfaceMembers = ctx
        while (interfaceMembers.dictionaryMember() != null) {
            members += getDictionaryMember(interfaceMembers.dictionaryMember())
            interfaceMembers = interfaceMembers.dictionaryMembers()
        }
        return members
    }

    private fun getEnumValueList(ctx: WebIdlParser.EnumValueListContext): Collection<WMember> {
        val members = mutableListOf<WMember>()
        members += EnumEntry(parseStringLiteral(ctx.STRING()))
        var enumValueListComma: WebIdlParser.EnumValueListCommaContext? = ctx.enumValueListComma()
        while (enumValueListComma?.enumValueListString()?.STRING() != null) {
            members += EnumEntry(parseStringLiteral(enumValueListComma.enumValueListString().STRING()))
            enumValueListComma = enumValueListComma.enumValueListString().enumValueListComma()
        }
        return members
    }

    private fun parseStringLiteral(node: TerminalNode): WStringLiteral {
        val text = node.text
        return WStringLiteral(text.substring(1, text.length - 1))
    }

    private fun getInterfaceMember(ctx: WebIdlParser.InterfaceMemberContext): WMember {
        return when {
            ctx.partialInterfaceMember() != null -> getPartialInterfaceMember(ctx.partialInterfaceMember())
            ctx.constructor() != null -> getConstructor(ctx.constructor())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as interfaceMember.")
        }
    }

    private fun getMixinMember(ctx: WebIdlParser.MixinMemberContext): WMember {
        return when {
            ctx.widlConst() != null -> getWidlConst(ctx.widlConst())
            ctx.regularOperation() != null -> getRegularOperation(ctx.regularOperation())
            ctx.stringifier() != null -> getStringifier(ctx.stringifier())
            ctx.attributeRest() != null -> getAttribute(
                ctx.attributeRest(),
                ctx.optionalReadOnly().text.isNotEmpty(),
                false
            )
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as mixinMember.")
        }
    }

    private fun getCallbackInterfaceMember(ctx: WebIdlParser.CallbackInterfaceMemberContext): WMember {
        return when {
            ctx.widlConst() != null -> getWidlConst(ctx.widlConst())
            ctx.regularOperation() != null -> getRegularOperation(ctx.regularOperation())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as callbackInterfaceMember.")
        }
    }

    private fun getPartialInterfaceMember(ctx: WebIdlParser.PartialInterfaceMemberContext): WMember {
        return when {
            ctx.widlConst() != null -> getWidlConst(ctx.widlConst())
            ctx.operation() != null -> getOperation(ctx.operation())
            ctx.stringifier() != null -> getStringifier(ctx.stringifier())
            ctx.staticMember() != null -> getStaticMember(ctx.staticMember())
            ctx.iterable() != null -> getIterable(ctx.iterable())
            ctx.asyncIterable() != null -> getAsyncIterable(ctx.asyncIterable())
            ctx.readOnlyMember() != null -> getReadOnlyMember(ctx.readOnlyMember())
            ctx.readWriteAttribute() != null -> getReadWriteAttribute(ctx.readWriteAttribute())
            ctx.readWriteMaplike() != null -> getReadWriteMaplike(ctx.readWriteMaplike())
            ctx.readWriteSetlike() != null -> getReadWriteSetlike(ctx.readWriteSetlike())
            ctx.inheritAttribute() != null -> getInheritAttribute(ctx.inheritAttribute())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as partialInterfaceMember.")
        }
    }

    private fun processPartial(ctx: WebIdlParser.PartialContext) {
        val def = ctx.partialDefinition()
        when {
            def.partialInterfaceOrPartialMixin() != null -> processPartialInterfaceOrPartialMixin(def.partialInterfaceOrPartialMixin())
            def.partialDictionary() != null -> processPartialDictionary(def.partialDictionary())
            def.namespace() != null -> processNamespace(def.namespace())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${def.text}\" as partialDefinition.")
        }
    }

    private fun processPartialInterfaceOrPartialMixin(ctx: WebIdlParser.PartialInterfaceOrPartialMixinContext) {
        when {
            ctx.partialInterfaceRest() != null -> processPartialInterface(ctx.partialInterfaceRest())
            ctx.mixinRest() != null -> processMixin(ctx.mixinRest())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as partialInterfaceOrPartialMixin.")
        }
    }

    private fun processPartialInterface(ctx: WebIdlParser.PartialInterfaceRestContext) {
        val identifier = ctx.identifier().text
        val members = getPartialInterfaceMembers(ctx.partialInterfaceMembers())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.INTERFACE)
        declarationBuilder.members.addAll(members)
    }

    private fun processDictionary(ctx: WebIdlParser.DictionaryContext) {
        val identifier = ctx.identifier().text
        val inheritance = getInheritance(ctx.inheritance())
        val members = getDictionaryMembers(ctx.dictionaryMembers())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.DICTIONARY)
        if (inheritance != null) {
            declarationBuilder.inherits = inheritance //TODO: Check existing inheritance
        }
        declarationBuilder.members.addAll(members)
    }

    private fun processPartialDictionary(ctx: WebIdlParser.PartialDictionaryContext) {
        val identifier = ctx.identifier().text
        val members = getDictionaryMembers(ctx.dictionaryMembers())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.DICTIONARY)
        declarationBuilder.members.addAll(members)
    }

    private fun processWidlEnum(ctx: WebIdlParser.WidlEnumContext) {
        val identifier = ctx.identifier().text
        val members = getEnumValueList(ctx.enumValueList())
        val declarationBuilder = irBuilderContext.registerDeclaration(identifier, CitizenType.ENUM)
        declarationBuilder.members.addAll(members)
    }

    private fun processTypedef(ctx: WebIdlParser.TypedefContext) {
        irBuilderContext.addTypedef(getType(ctx.typeWithExtendedAttributes().type()), ctx.identifier().text)
    }

    private fun processIncludesStatement(ctx: WebIdlParser.IncludesStatementContext) {
        irBuilderContext.addIncludes(WIncludes("", ctx.identifier(0).text, ctx.identifier(1).text))
    }

    private fun getInheritance(ctx: WebIdlParser.InheritanceContext): String? {
        return ctx.identifier()?.text
    }

    private fun getWidlConst(ctx: WebIdlParser.WidlConstContext): WConstant {
        return WConstant(
            getConstType(ctx.constType()),
            ctx.identifier().text,
            getConstValue(ctx.constValue())
        )
    }

    private fun getConstType(ctx: WebIdlParser.ConstTypeContext): WTypeExpression {
        return when {
            ctx.primitiveType() != null -> getPrimitiveType(ctx.primitiveType())
            ctx.identifier() != null -> getTypeReference(ctx.identifier())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as const type.")
        }
    }

    private fun getPrimitiveType(ctx: WebIdlParser.PrimitiveTypeContext): WPrimitiveType {
        return when {
            ctx.unsignedIntegerType() != null -> getIntegerType(ctx.unsignedIntegerType())
            ctx.unrestrictedFloatType() != null -> getFloatType(ctx.unrestrictedFloatType())
            else -> getSimplePrimitiveType(ctx)
        }
    }

    private fun getIntegerType(ctx: WebIdlParser.UnsignedIntegerTypeContext): WIntegerType {
        val unsigned = ctx.start.text == "unsigned"
        val ictx = ctx.integerType()
        val baseType = when {
            ictx.text == "short" -> IntegerBaseType.SHORT
            ictx.optionalLong().text == "long" -> IntegerBaseType.LONG_LONG
            else -> IntegerBaseType.LONG
        }
        return WIntegerType(unsigned, baseType)
    }

    private fun getFloatType(ctx: WebIdlParser.UnrestrictedFloatTypeContext): WFloatType {
        val unrestricted = ctx.start.text == "unrestricted"
        val ictx = ctx.floatType()
        val baseType = when {
            ictx.text == "float" -> FloatBaseType.FLOAT
            ictx.text == "double" -> FloatBaseType.DOUBLE
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ictx.text}\" as float type.")
        }
        return WFloatType(unrestricted, baseType)
    }

    private fun getSimplePrimitiveType(ctx: WebIdlParser.PrimitiveTypeContext): WPrimitiveType {
        return when (ctx.text) {
            "undefined" -> WUndefinedType
            "boolean" -> WBooleanType
            "byte" -> WByteType
            "octet" -> WOctetType
            "bigint" -> WBigIntType
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as simple primitive type.")
        }
    }

    private fun getTypeReference(ctx: ParseTree): WTypeExpression {
        val name = ctx.text
        return if (name == "void") WVoidType else WTypeReference(name)
    }

    private fun getConstValue(ctx: WebIdlParser.ConstValueContext): WLiteral {
        return when {
            ctx.booleanLiteral() != null -> getBooleanValue(ctx.booleanLiteral())
            ctx.floatLiteral() != null -> getFloatValue(ctx.floatLiteral())
            ctx.INTEGER() != null -> getIntegerValue(ctx.INTEGER())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as const value.")
        }
    }

    private fun getBooleanValue(ctx: WebIdlParser.BooleanLiteralContext): WBooleanLiteral {
        return WBooleanLiteral(ctx.text == "true")
    }

    private fun getFloatValue(ctx: WebIdlParser.FloatLiteralContext): WFloatLiteral {
        return WFloatLiteral(ctx.text.toDouble())
    }

    private fun getIntegerValue(ctx: TerminalNode): WIntegerLiteral {
        val text = ctx.text
        return WIntegerLiteral(
            when {
                text.startsWith("0x") -> text.substring(2).toLong(16)
                else -> text.toLong()
            }
        )
    }

    private fun getReadOnlyMember(ctx: WebIdlParser.ReadOnlyMemberContext): WMember {
        val rCtx = ctx.readOnlyMemberRest()
        return when {
            rCtx.attributeRest() != null -> getAttribute(rCtx.attributeRest(), true, false)
            rCtx.maplikeRest() != null -> getMapLike(rCtx.maplikeRest(), true)
            rCtx.setlikeRest() != null -> getSetLike(rCtx.setlikeRest(), true)
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as read only member.")
        }
    }

    private fun getReadWriteAttribute(ctx: WebIdlParser.ReadWriteAttributeContext): WAttribute {
        return getAttribute(ctx.attributeRest(), false, false)
    }

    private fun getMapLike(ctx: WebIdlParser.MaplikeRestContext, readOnly: Boolean): MapLikeExpression {
        return MapLikeExpression(
            getType(ctx.typeWithExtendedAttributes(0).type()),
            getType(ctx.typeWithExtendedAttributes(1).type()),
            readOnly
        )
    }

    private fun getSetLike(ctx: WebIdlParser.SetlikeRestContext, readOnly: Boolean): SetLike {
        return SetLike(getType(ctx.typeWithExtendedAttributes().type()), readOnly)
    }

    private fun getInheritAttribute(ctx: WebIdlParser.InheritAttributeContext): WAttribute {
        return getAttribute(ctx.attributeRest(), false, true)
    }

    private fun getAttribute(ctx: WebIdlParser.AttributeRestContext, readOnly: Boolean, inherit: Boolean): WAttribute {
        val type = getType(ctx.typeWithExtendedAttributes().type())
        val name = getAttributeName(ctx.attributeName())
        return WAttribute(readOnly, inherit, type, name)
    }

    private fun getAttributeName(ctx: WebIdlParser.AttributeNameContext): String {
        return ctx.text
    }


    private fun getType(ctx: WebIdlParser.TypeContext): WTypeExpression {
        return when {
            ctx.singleType() != null -> getSingleType(ctx.singleType())
            ctx.unionType() != null -> getNullableType(ctx.widlNull().text.isNotEmpty(), getUnionType(ctx.unionType()))
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as type.")
        }
    }

    private fun getSingleType(ctx: WebIdlParser.SingleTypeContext): WTypeExpression {
        return when {
            ctx.distinguishableType() != null -> getDistinguishableType(ctx.distinguishableType())
            ctx.text == "any" -> WAnyType
            ctx.promiseType() != null -> getPromiseType(ctx.promiseType())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as single type.")
        }
    }

    private fun getUnionType(ctx: WebIdlParser.UnionTypeContext): WUnionTypeExpression {
        val types =
            mutableListOf(getUnionMemberType(ctx.unionMemberType(0)), getUnionMemberType(ctx.unionMemberType(1)))
        var unionMemberTypes = ctx.unionMemberTypes()
        while (unionMemberTypes != null) {
            if (unionMemberTypes.unionMemberType() != null) {
                types += getUnionMemberType(unionMemberTypes.unionMemberType())
            }
            unionMemberTypes = unionMemberTypes.unionMemberTypes()
        }
        return WUnionTypeExpression(types)
    }

    private fun getUnionMemberType(ctx: WebIdlParser.UnionMemberTypeContext): WTypeExpression {
        return when {
            ctx.distinguishableType() != null -> getDistinguishableType(ctx.distinguishableType())
            ctx.unionType() != null -> getNullableType(ctx.widlNull().text.isNotEmpty(), getUnionType(ctx.unionType()))
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as union type member.")
        }
    }

    private fun getDistinguishableType(ctx: WebIdlParser.DistinguishableTypeContext): WTypeExpression {
        val nullable = ctx.widlNull().text.isNotEmpty()
        val type = when {
            ctx.primitiveType() != null -> getPrimitiveType(ctx.primitiveType())
            ctx.stringType() != null -> getStringType(ctx.stringType())
            ctx.identifier() != null -> getTypeReference(ctx.identifier())
            ctx.bufferRelatedType() != null -> getBufferRelatedType(ctx.bufferRelatedType())
            ctx.recordType() != null -> getRecordType(ctx.recordType())
            else -> when (ctx.start.text) {
                "sequence" -> WSequenceTypeExpression(getType(ctx.typeWithExtendedAttributes().type()))
                "object" -> WObjectType
                "symbol" -> WSymbolType
                "FrozenArray" -> WFrozenArrayTypeExpression(getType(ctx.typeWithExtendedAttributes().type()))
                "ObservableArray" -> WObservableArrayTypeExpression(getType(ctx.typeWithExtendedAttributes().type()))
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as distinguishable type.")
            }
        }
        return getNullableType(nullable, type)
    }

    private fun getPromiseType(ctx: WebIdlParser.PromiseTypeContext): WPromiseTypeExpression {
        return WPromiseTypeExpression(getType(ctx.type()))
    }

    private fun getNullableType(nullable: Boolean, type: WTypeExpression): WTypeExpression {
        return when (nullable) {
            true -> WNullableTypeExpression(type)
            false -> type
        }
    }

    private fun getStringType(ctx: WebIdlParser.StringTypeContext): WStringType {
        return when (ctx.text) {
            "ByteString" -> WStringType.ByteString
            "DOMString" -> WStringType.DOMString
            "USVString" -> WStringType.USVString
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as string type.")
        }
    }

    private fun getBufferRelatedType(ctx: WebIdlParser.BufferRelatedTypeContext): WBufferRelatedType {
        return when (ctx.text) {
            "ArrayBuffer" -> WBufferRelatedType.ArrayBuffer
            "DataView" -> WBufferRelatedType.DataView
            "Int8Array" -> WBufferRelatedType.Int8Array
            "Int16Array" -> WBufferRelatedType.Int16Array
            "Int32Array" -> WBufferRelatedType.Int32Array
            "Uint8Array" -> WBufferRelatedType.Uint8Array
            "Uint16Array" -> WBufferRelatedType.Uint16Array
            "Uint32Array" -> WBufferRelatedType.Uint32Array
            "Uint8ClampedArray" -> WBufferRelatedType.Uint8ClampedArray
            "Float32Array" -> WBufferRelatedType.Float32Array
            "Float64Array" -> WBufferRelatedType.Float64Array
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as buffer related type.")
        }
    }

    private fun getRecordType(ctx: WebIdlParser.RecordTypeContext): WRecordTypeExpression {
        return WRecordTypeExpression(getStringType(ctx.stringType()), getType(ctx.typeWithExtendedAttributes().type()))
    }

    private fun getOperation(ctx: WebIdlParser.OperationContext): WOperation {
        return when {
            ctx.regularOperation() != null -> getRegularOperation(ctx.regularOperation())
            ctx.specialOperation() != null -> getSpecialOperation(ctx.specialOperation())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as operation.")
        }
    }

    private fun getRegularOperation(ctx: WebIdlParser.RegularOperationContext): WOperation {
        return WOperation(
            null,
            getType(ctx.type()),
            getOptionalOperationName(ctx.operationRest().optionalOperationName()),
            getArgumentList(ctx.operationRest().argumentList())
        )
    }

    private fun getSpecialOperation(ctx: WebIdlParser.SpecialOperationContext): WOperation {
        val regularOperation = getRegularOperation(ctx.regularOperation())
        return WOperation(
            getSpecial(ctx.special()),
            regularOperation.type,
            regularOperation.name,
            regularOperation.arguments
        )
    }

    private fun getSpecial(ctx: WebIdlParser.SpecialContext): WOperation.Special {
        val special = WOperation.Special.byKeyword[ctx.text]
        if (special != null) {
            return special
        } else {
            throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as special.")
        }
    }

    private fun getOptionalOperationName(ctx: WebIdlParser.OptionalOperationNameContext): String? {
        return if (ctx.operationName() != null) {
            getOperationName(ctx.operationName())
        } else {
            null
        }
    }

    private fun getOperationName(ctx: WebIdlParser.OperationNameContext): String {
        return ctx.text
    }

    private fun getOperationNameKeyword(ctx: WebIdlParser.OperationNameKeywordContext): String {
        return ctx.text
    }

    private fun getArgumentList(ctx: WebIdlParser.ArgumentListContext): List<WArgument> {
        val arguments = mutableListOf<WArgument>()
        if (ctx.argument() != null) {
            arguments.add(getArgument(ctx.argument()))
            var argumentsCtx: WebIdlParser.ArgumentsContext? = ctx.arguments()
            while (argumentsCtx != null) {
                if (argumentsCtx.argument() != null) {
                    arguments.add(getArgument(argumentsCtx.argument()))
                }
                argumentsCtx = argumentsCtx.arguments()
            }
        }
        return arguments
    }

    private fun getArgument(ctx: WebIdlParser.ArgumentContext): WArgument {
        return getArgument(ctx.argumentRest())
    }

    private fun getArgument(ctx: WebIdlParser.ArgumentRestContext): WArgument {
        return when {
            ctx.typeWithExtendedAttributes() != null -> {
                val type = getNullableType(true, getType(ctx.typeWithExtendedAttributes().type()))
                WArgument(
                    true,
                    type,
                    false,
                    getArgumentName(ctx.argumentName()),
                    getDefaultValue(ctx.widlDefault())
                )
            }
            else -> WArgument(
                false,
                getType(ctx.type()),
                ctx.ellipsis().text.isNotEmpty(),
                getArgumentName(ctx.argumentName()),
                null
            )
        }
    }

    private fun getDefaultValue(ctx: WebIdlParser.WidlDefaultContext): WLiteral? {
        return if (ctx.defaultValue() != null) {
            getDefaultValue(ctx.defaultValue())
        } else {
            null
        }
    }

    private fun getDefaultValue(ctx: WebIdlParser.DefaultValueContext): WLiteral {
        return when {
            ctx.constValue() != null -> getConstValue(ctx.constValue())
            ctx.STRING() != null -> getStringValue(ctx.STRING())
            else -> when (ctx.start.text) {
                "[" -> WEmptyArrayLiteral
                "{" -> WEmptyObjectLiteral
                "null" -> WNullLiteral
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as default value.")
            }
        }
    }

    private fun getStringValue(ctx: TerminalNode): WStringLiteral {
        val value = ctx.text
        return WStringLiteral(value.substring(1, value.length - 1))
    }

    private fun getArgumentName(ctx: WebIdlParser.ArgumentNameContext): String {
        return ctx.text
    }

    private fun getConstructor(ctx: WebIdlParser.ConstructorContext): WConstructor {
        return WConstructor(
            getArgumentList(ctx.argumentList())
        )
    }

    private fun getStringifier(ctx: WebIdlParser.StringifierContext): WMember {
        val rest = ctx.stringifierRest()
        return when {
            rest.attributeRest() != null -> //TODO: Add stringifier attribute?
                getAttribute(rest.attributeRest(), rest.optionalReadOnly().text.isNotEmpty(), false)
            rest.regularOperation() != null -> //TODO: Add stringifier attribute?
                getRegularOperation(rest.regularOperation())
            else -> Stringifier.EmptyStringifier
        }
    }

    private fun getStaticMember(ctx: WebIdlParser.StaticMemberContext): WMember {
        val rCtx = ctx.staticMemberRest()
        return when {
            rCtx.attributeRest() != null ->
                getAttribute(rCtx.attributeRest(), rCtx.optionalReadOnly().text.isNotEmpty(), false).asStatic()
            rCtx.regularOperation() != null ->
                getRegularOperation(rCtx.regularOperation()).asStatic()
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as static member.")
        }.asStatic()
//        return WStatic(member)
    }

    private fun getIterable(ctx: WebIdlParser.IterableContext): Iterable {
        val left = getType(ctx.typeWithExtendedAttributes().type())
        val right = getOptionalType(ctx.optionalType())
        val iterable = if (right != null) {
            Iterable.MapIterable(left, right)
        } else {
            Iterable.CollectionIterable(left)
        }
        return iterable
    }

    private fun getOptionalType(ctx: WebIdlParser.OptionalTypeContext): WTypeExpression? {
        return if (ctx.typeWithExtendedAttributes() != null) {
            getType(ctx.typeWithExtendedAttributes().type())
        } else {
            null
        }
    }

    private fun getAsyncIterable(ctx: WebIdlParser.AsyncIterableContext): AsyncIterable {
        val left = getType(ctx.typeWithExtendedAttributes().type())
        val right = getOptionalType(ctx.optionalType())
        val arguments = if (ctx.optionalArgumentList().argumentList() != null) {
            getArgumentList(ctx.optionalArgumentList().argumentList())
        } else {
            null
        }
        val iterable = if (right != null) {
            AsyncIterable.MapIterable(left, right, arguments)
        } else {
            AsyncIterable.CollectionIterable(left, arguments)
        }
        return iterable
    }

    private fun getReadWriteMaplike(ctx: WebIdlParser.ReadWriteMaplikeContext): MapLikeExpression {
        return getMapLike(ctx.maplikeRest(), false)
    }

    private fun getReadWriteSetlike(ctx: WebIdlParser.ReadWriteSetlikeContext): SetLike {
        return getSetLike(ctx.setlikeRest(), false)
    }

    private fun getNamespaceMember(ctx: WebIdlParser.NamespaceMemberContext): WMember {
        return when {
            ctx.regularOperation() != null -> getRegularOperation(ctx.regularOperation())
            ctx.attributeRest() != null -> getAttribute(ctx.attributeRest(), true, false)
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as namespaceMember.")
        }
    }

    private fun getDictionaryMember(ctx: WebIdlParser.DictionaryMemberContext): WDictionaryEntry {
        val rest = ctx.dictionaryMemberRest()
        return when {
            rest.typeWithExtendedAttributes() != null -> WDictionaryEntry(
                true,
                getType(rest.typeWithExtendedAttributes().type()),
                rest.identifier().text,
                null
            )
            rest.type() != null -> WDictionaryEntry(
                true,
                getType(rest.type()),
                rest.identifier().text,
                getDefaultValue(rest.widlDefault())
            )
            else -> throw IllegalArgumentException("Don't know how to interpret \"${rest.text}\" as dictionaryMemberRest.")
        }
    }

    private fun getCallback(ctx: WebIdlParser.CallbackRestContext): WCallback {
        return WCallback("", ctx.identifier().text, getType(ctx.type()), getArgumentList(ctx.argumentList()))
    }

    private fun processCallback(ctx: WebIdlParser.CallbackRestContext) {
        irBuilderContext.addCallback(
            WCallback(
                "",
                ctx.identifier().text,
                getType(ctx.type()),
                getArgumentList(ctx.argumentList())
            )
        )
    }
}
