package org.carrat.webidl.build.compile.parser

import org.antlr.v4.runtime.tree.TerminalNode
import org.carrat.webidl.build.compile.model.webidlir.*
import org.carrat.webidl.build.compile.model.webidlir.types.*
import org.carrat.webidl.build.compile.model.webidlir.values.*
import org.carrat.webidl.build.compile.model.webidlir.values.FloatValue
import org.carrat.webidl.build.grammar.WebIdlBaseListener
import org.carrat.webidl.build.grammar.WebIdlParser
import org.carrat.webidl.build.compile.model.webidlir.Iterable

class WebIdlIrBuildingListener(
    private val irBuilder: IrBuilder
) : WebIdlBaseListener() {
    override fun enterInterfaceRest(ctx: WebIdlParser.InterfaceRestContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.INTERFACE, getInheritance(ctx.inheritance()))
    }

    override fun exitInterfaceRest(ctx: WebIdlParser.InterfaceRestContext) {
        irBuilder.exitDeclaration()
    }

    override fun enterPartialInterfaceRest(ctx: WebIdlParser.PartialInterfaceRestContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.INTERFACE)
    }

    override fun exitPartialInterfaceRest(ctx: WebIdlParser.PartialInterfaceRestContext) {
        irBuilder.exitDeclaration()
    }

    private fun getInheritance(ctx: WebIdlParser.InheritanceContext): String? {
        return if (ctx.IDENTIFIER() != null) {
            ctx.IDENTIFIER().text
        } else {
            null
        }
    }

    override fun enterMixinRest(ctx: WebIdlParser.MixinRestContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.MIXIN)
    }

    override fun exitMixinRest(ctx: WebIdlParser.MixinRestContext) {
        irBuilder.exitDeclaration()
    }

    override fun exitIncludesStatement(ctx: WebIdlParser.IncludesStatementContext) {
        irBuilder.addIncludes(Includes(Identifier(ctx.IDENTIFIER(0).text), Identifier(ctx.IDENTIFIER(1).text)))
    }

    override fun enterCallbackRestOrInterface(ctx: WebIdlParser.CallbackRestOrInterfaceContext) {
        when {
            ctx.callbackRest() != null -> {
                //TODO
            }
            else -> irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.CALLBACK_INTERFACE)
        }
    }

    override fun exitCallbackRestOrInterface(ctx: WebIdlParser.CallbackRestOrInterfaceContext) {
        when {
            ctx.callbackRest() != null -> irBuilder.addCallback(getCallback(ctx.callbackRest()))
            else -> {
                var callbackInterfaceMembers = ctx.callbackInterfaceMembers()
                while (callbackInterfaceMembers.callbackInterfaceMember() != null) {
                    val callbackInterfaceMember = callbackInterfaceMembers.callbackInterfaceMember()
                    when {
                        callbackInterfaceMember.regularOperation() != null -> irBuilder.addMember(
                            getRegularOperation(
                                callbackInterfaceMember.regularOperation()
                            )
                        )
                        callbackInterfaceMember.widlConst() != null -> {
                        }
                        else -> throw IllegalArgumentException("Don't know how to interpret \"${callbackInterfaceMember.text}\" as callback interface member.")
                    }
                    callbackInterfaceMembers = callbackInterfaceMembers.callbackInterfaceMembers()
                }
                irBuilder.exitDeclaration()
            }
        }
    }

    override fun exitWidlConst(ctx: WebIdlParser.WidlConstContext) {
        irBuilder.addMember(
            Constant(
                getConstType(ctx.constType()),
                Identifier(ctx.IDENTIFIER().text),
                getConstValue(ctx.constValue())
            )
        )
    }

    private fun getConstType(ctx: WebIdlParser.ConstTypeContext): Type {
        return when {
            ctx.primitiveType() != null -> getPrimitiveType(ctx.primitiveType())
            ctx.IDENTIFIER() != null -> getTypeReference(ctx.IDENTIFIER())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as const type.")
        }
    }

    private fun getPrimitiveType(ctx: WebIdlParser.PrimitiveTypeContext): PrimitiveType {
        return when {
            ctx.unsignedIntegerType() != null -> getIntegerType(ctx.unsignedIntegerType())
            ctx.unrestrictedFloatType() != null -> getFloatType(ctx.unrestrictedFloatType())
            else -> getSimplePrimitiveType(ctx)
        }
    }

    private fun getIntegerType(ctx: WebIdlParser.UnsignedIntegerTypeContext): IntegerType {
        val unsigned = ctx.start.text == "unsigned"
        val ictx = ctx.integerType()
        val baseType = when {
            ictx.text == "short" -> IntegerBaseType.SHORT
            ictx.optionalLong().text == "long" -> IntegerBaseType.LONG_LONG
            else -> IntegerBaseType.LONG
        }
        return IntegerType(unsigned, baseType)
    }

    private fun getFloatType(ctx: WebIdlParser.UnrestrictedFloatTypeContext): FloatType {
        val unrestricted = ctx.start.text == "unrestricted"
        val ictx = ctx.floatType()
        val baseType = when {
            ictx.text == "float" -> FloatBaseType.FLOAT
            ictx.text == "double" -> FloatBaseType.DOUBLE
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ictx.text}\" as float type.")
        }
        return FloatType(unrestricted, baseType)
    }

    private fun getSimplePrimitiveType(ctx: WebIdlParser.PrimitiveTypeContext): PrimitiveType {
        return when (ctx.text) {
            "undefined" -> UndefinedType
            "boolean" -> BooleanType
            "byte" -> ByteType
            "octet" -> OctetType
            "bigint" -> BigIntType
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as simple primitive type.")
        }
    }

    private fun getTypeReference(ctx: TerminalNode): Type {
        val name = ctx.text
        return if (name == "void") VoidType else TypeReference(Identifier(name))
    }

    private fun getConstValue(ctx: WebIdlParser.ConstValueContext): Value {
        return when {
            ctx.booleanLiteral() != null -> getBooleanValue(ctx.booleanLiteral())
            ctx.floatLiteral() != null -> getFloatValue(ctx.floatLiteral())
            ctx.INTEGER() != null -> getIntegerValue(ctx.INTEGER())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as const value.")
        }
    }

    private fun getConstValue(ctx: WebIdlParser.ConstValueContext, type: Type): Value {
        val value = getConstValue(ctx)
        return when(type) {
            is FloatType -> when(value) {
                is FloatValue -> value
                is IntegerValue -> FloatValue(value.value.toDouble())
                else -> throw IllegalArgumentException("Don't know how convert \"${value}\" to Float type.")
            }
            is IntegerType -> when(value) {
                is IntegerValue -> value
                else -> throw IllegalArgumentException("Don't know how convert \"${value}\" to Integer type.")
            }
            is BooleanType -> when(value) {
                is BooleanValue -> value
                else -> throw IllegalArgumentException("Don't know how convert \"${value}\" to Boolean type.")
            }
            else -> throw IllegalArgumentException("Don't know how convert \"${value}\" to ${type}.")
        }
    }

    private fun getBooleanValue(ctx: WebIdlParser.BooleanLiteralContext): BooleanValue {
        return BooleanValue(ctx.text == "true")
    }

    private fun getFloatValue(ctx: WebIdlParser.FloatLiteralContext): FloatValue {
        return FloatValue(ctx.text.toDouble())
    }

    private fun getIntegerValue(ctx: TerminalNode): IntegerValue {
        val text = ctx.text
        return IntegerValue(
            when {
                text.startsWith("0x") -> text.substring(2).toLong(16)
                else -> text.toLong()
            }
        )
    }

    override fun exitReadOnlyMember(ctx: WebIdlParser.ReadOnlyMemberContext) {
        val rCtx = ctx.readOnlyMemberRest()
        val member = when {
            rCtx.attributeRest() != null -> getAttribute(rCtx.attributeRest(), true, false)
            rCtx.maplikeRest() != null -> getMapLike(rCtx.maplikeRest(), true)
            rCtx.setlikeRest() != null -> getSetLike(rCtx.setlikeRest(), true)
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as read only member.")
        }
        irBuilder.addMember(member)
    }

    override fun exitReadWriteAttribute(ctx: WebIdlParser.ReadWriteAttributeContext) {
        irBuilder.addMember(getAttribute(ctx.attributeRest(), false, false))
    }

    override fun exitMixinMember(ctx: WebIdlParser.MixinMemberContext) {
        if(ctx.attributeRest() != null) {
            irBuilder.addMember(getAttribute(ctx.attributeRest(), false, false))
        }
    }

    private fun getMapLike(ctx: WebIdlParser.MaplikeRestContext, readOnly: Boolean): MapLike {
        return MapLike(
            getType(ctx.typeWithExtendedAttributes(0).type()),
            getType(ctx.typeWithExtendedAttributes(1).type()),
            readOnly
        )
    }

    private fun getSetLike(ctx: WebIdlParser.SetlikeRestContext, readOnly: Boolean): SetLike {
        return SetLike(getType(ctx.typeWithExtendedAttributes().type()), readOnly)
    }

    override fun exitInheritAttribute(ctx: WebIdlParser.InheritAttributeContext) {
        irBuilder.addMember(
            getAttribute(ctx.attributeRest(), false, true)
        )
    }

    private fun getAttribute(ctx: WebIdlParser.AttributeRestContext, readOnly: Boolean, inherit: Boolean): Attribute {
        val type = getType(ctx.typeWithExtendedAttributes().type())
        val name = getAttributeName(ctx.attributeName())
        return Attribute(readOnly, inherit, type, name)
    }

    private fun getAttributeName(ctx: WebIdlParser.AttributeNameContext): AttributeName {
        return when {
            ctx.attributeNameKeyword() != null -> getAttributeNameKeyword(ctx.attributeNameKeyword())
            else -> AttributeName.Reference(Identifier(ctx.text))
        }
    }

    private fun getAttributeNameKeyword(ctx: WebIdlParser.AttributeNameKeywordContext): AttributeName.Keyword {
        return when (ctx.text) {
            "async" -> AttributeName.Keyword.Async
            "required" -> AttributeName.Keyword.Required
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as attribute name keyword.")
        }
    }

    private fun getType(ctx: WebIdlParser.TypeContext): Type {
        return when {
            ctx.singleType() != null -> getSingleType(ctx.singleType())
            ctx.unionType() != null -> getNullableType(ctx.widlNull().text.isNotEmpty(), getUnionType(ctx.unionType()))
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as type.")
        }
    }

    private fun getSingleType(ctx: WebIdlParser.SingleTypeContext): Type {
        return when {
            ctx.distinguishableType() != null -> getDistinguishableType(ctx.distinguishableType())
            ctx.text == "any" -> AnyType
            ctx.promiseType() != null -> getPromiseType(ctx.promiseType())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as single type.")
        }
    }

    private fun getUnionType(ctx: WebIdlParser.UnionTypeContext): UnionType {
        val types =
            mutableListOf(getUnionMemberType(ctx.unionMemberType(0)), getUnionMemberType(ctx.unionMemberType(1)))
        var unionMemberTypes = ctx.unionMemberTypes()
        while (unionMemberTypes != null) {
            if (unionMemberTypes.unionMemberType() != null) {
                types += getUnionMemberType(unionMemberTypes.unionMemberType())
            }
            unionMemberTypes = unionMemberTypes.unionMemberTypes()
        }
        return UnionType(types)
    }

    private fun getUnionMemberType(ctx: WebIdlParser.UnionMemberTypeContext): Type {
        return when {
            ctx.distinguishableType() != null -> getDistinguishableType(ctx.distinguishableType())
            ctx.unionType() != null -> getNullableType(ctx.widlNull().text.isNotEmpty(), getUnionType(ctx.unionType()))
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as union type member.")
        }
    }

    private fun getDistinguishableType(ctx: WebIdlParser.DistinguishableTypeContext): Type {
        val nullable = ctx.widlNull().text.isNotEmpty()
        val type = when {
            ctx.primitiveType() != null -> getPrimitiveType(ctx.primitiveType())
            ctx.stringType() != null -> getStringType(ctx.stringType())
            ctx.IDENTIFIER() != null -> getTypeReference(ctx.IDENTIFIER())
            ctx.bufferRelatedType() != null -> getBufferRelatedType(ctx.bufferRelatedType())
            ctx.recordType() != null -> getRecordType(ctx.recordType())
            else -> when (ctx.start.text) {
                "sequence" -> SequenceType(getType(ctx.typeWithExtendedAttributes().type()))
                "object" -> ObjectType
                "symbol" -> SymbolType
                "FrozenArray" -> FrozenArrayType(getType(ctx.typeWithExtendedAttributes().type()))
                "ObservableArray" -> ObservableArrayType(getType(ctx.typeWithExtendedAttributes().type()))
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as distinguishable type.")
            }
        }
        return getNullableType(nullable, type)
    }

    private fun getPromiseType(ctx: WebIdlParser.PromiseTypeContext): PromiseType {
        return PromiseType(getType(ctx.type()))
    }

    private fun getNullableType(nullable: Boolean, type: Type): Type {
        return when (nullable) {
            true -> NullableType(type)
            false -> type
        }
    }

    private fun getStringType(ctx: WebIdlParser.StringTypeContext): StringType {
        return when (ctx.text) {
            "ByteString" -> StringType.ByteString
            "DOMString" -> StringType.DOMString
            "USVString" -> StringType.USVString
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as string type.")
        }
    }

    private fun getBufferRelatedType(ctx: WebIdlParser.BufferRelatedTypeContext): BufferRelatedType {
        return when (ctx.text) {
            "ArrayBuffer" -> BufferRelatedType.ArrayBuffer
            "DataView" -> BufferRelatedType.DataView
            "Int8Array" -> BufferRelatedType.Int8Array
            "Int16Array" -> BufferRelatedType.Int16Array
            "Int32Array" -> BufferRelatedType.Int32Array
            "Uint8Array" -> BufferRelatedType.Uint8Array
            "Uint16Array" -> BufferRelatedType.Uint16Array
            "Uint32Array" -> BufferRelatedType.Uint32Array
            "Uint8ClampedArray" -> BufferRelatedType.Uint8ClampedArray
            "Float32Array" -> BufferRelatedType.Float32Array
            "Float64Array" -> BufferRelatedType.Float64Array
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as buffer related type.")
        }
    }

    private fun getRecordType(ctx: WebIdlParser.RecordTypeContext): RecordType {
        return RecordType(getStringType(ctx.stringType()), getType(ctx.typeWithExtendedAttributes().type()))
    }

    override fun exitOperation(ctx: WebIdlParser.OperationContext) {
        val operation = when {
            ctx.regularOperation() != null -> getRegularOperation(ctx.regularOperation())
            ctx.specialOperation() != null -> getSpecialOperation(ctx.specialOperation())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as operation.")
        }
        irBuilder.addMember(operation)
    }

    private fun getRegularOperation(ctx: WebIdlParser.RegularOperationContext): Operation {
        return Operation(
            null,
            getType(ctx.type()),
            getOptionalOperationName(ctx.operationRest().optionalOperationName()),
            getArgumentList(ctx.operationRest().argumentList())
        )
    }

    private fun getSpecialOperation(ctx: WebIdlParser.SpecialOperationContext): Operation {
        val regularOperation = getRegularOperation(ctx.regularOperation())
        return Operation(
            getSpecial(ctx.special()),
            regularOperation.type,
            regularOperation.name,
            regularOperation.arguments
        )
    }

    private fun getSpecial(ctx: WebIdlParser.SpecialContext): Operation.Special {
        val special = Operation.Special.byKeyword[ctx.text]
        if (special != null) {
            return special
        } else {
            throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as special.")
        }
    }

    private fun getOptionalOperationName(ctx: WebIdlParser.OptionalOperationNameContext): OperationName? {
        return if (ctx.operationName() != null) {
            getOperationName(ctx.operationName())
        } else {
            null
        }
    }

    private fun getOperationName(ctx: WebIdlParser.OperationNameContext): OperationName {
        return when {
            ctx.operationNameKeyword() != null -> getOperationNameKeyword(ctx.operationNameKeyword())
            ctx.IDENTIFIER() != null -> OperationName.Reference(Identifier(ctx.IDENTIFIER().text))
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as operation name.")
        }
    }

    private fun getOperationNameKeyword(ctx: WebIdlParser.OperationNameKeywordContext): OperationName.Keyword {
        return when (ctx.text) {
            "includes" -> OperationName.Keyword.Includes
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as operation name keyword.")
        }
    }

    private fun getArgumentList(ctx: WebIdlParser.ArgumentListContext): List<Argument> {
        val arguments = mutableListOf<Argument>()
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

    private fun getArgument(ctx: WebIdlParser.ArgumentContext): Argument {
        return getArgument(ctx.argumentRest())
    }

    private fun getArgument(ctx: WebIdlParser.ArgumentRestContext): Argument {
        return when {
            ctx.typeWithExtendedAttributes() != null -> {
                val type = getType(ctx.typeWithExtendedAttributes().type())
                Argument(
                    true,
                    type,
                    false,
                    getArgumentName(ctx.argumentName()),
                    getDefaultValue(ctx.widlDefault(), type)
                )
            }
            else -> Argument(
                false,
                getType(ctx.type()),
                ctx.ellipsis().text.isNotEmpty(),
                getArgumentName(ctx.argumentName()),
                null
            )
        }
    }

    private fun getDefaultValue(ctx: WebIdlParser.WidlDefaultContext): Value? {
        return if (ctx.defaultValue() != null) {
            getDefaultValue(ctx.defaultValue())
        } else {
            null
        }
    }

    private fun getDefaultValue(ctx: WebIdlParser.WidlDefaultContext, type: Type): Value? {
        return if (ctx.defaultValue() != null) {
            getDefaultValue(ctx.defaultValue(), type)
        } else {
            null
        }
    }

    private fun getDefaultValue(ctx: WebIdlParser.DefaultValueContext): Value {
        return when {
            ctx.constValue() != null -> getConstValue(ctx.constValue())
            ctx.STRING() != null -> getStringValue(ctx.STRING())
            else -> when (ctx.start.text) {
                "[" -> EmptyArrayValue
                "{" -> EmptyObjectValue
                "null" -> NullValue
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as default value.")
            }
        }
    }

    private fun getDefaultValue(ctx: WebIdlParser.DefaultValueContext, type: Type): Value {
        return when {
            ctx.constValue() != null -> getConstValue(ctx.constValue(), type)
            ctx.STRING() != null -> getStringValue(ctx.STRING())
            else -> when (ctx.start.text) {
                "[" -> EmptyArrayValue
                "{" -> EmptyObjectValue
                "null" -> NullValue
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as default value.")
            }
        }
    }

    private fun getStringValue(ctx: TerminalNode): StringValue {
        val value = ctx.text
        return StringValue(value.substring(1, value.length - 1))
    }

    private fun getArgumentName(ctx: WebIdlParser.ArgumentNameContext): ArgumentName {
        return when {
            ctx.argumentNameKeyword() != null -> getArgumentNameKeyword(ctx.argumentNameKeyword())
            ctx.IDENTIFIER() != null -> ArgumentName.Reference(Identifier(ctx.IDENTIFIER().text))
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as argument name.")
        }
    }

    private fun getArgumentNameKeyword(ctx: WebIdlParser.ArgumentNameKeywordContext): ArgumentName.Keyword {
        val keyword = ArgumentNameKeyword.byName[ctx.text]
        if (keyword != null) {
            return ArgumentName.Keyword(keyword)
        } else {
            throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as argument name keyword.")
        }
    }

    override fun exitConstructor(ctx: WebIdlParser.ConstructorContext) {
        irBuilder.addMember(
            Constructor(
                getArgumentList(ctx.argumentList())
            )
        )
    }

    override fun exitStringifierRest(ctx: WebIdlParser.StringifierRestContext) {
        val stringifier = when {
            ctx.attributeRest() != null ->
                Stringifier.AttributeStringifier(
                    getAttribute(ctx.attributeRest(), ctx.optionalReadOnly().text.isNotEmpty(), false)
                )
            ctx.regularOperation() != null ->
                Stringifier.OperationStringifier(
                    getRegularOperation(ctx.regularOperation())
                )
            else -> Stringifier.EmptyStringifier
        }
        irBuilder.addMember(stringifier)
    }

    override fun exitStaticMember(ctx: WebIdlParser.StaticMemberContext) {
        val rCtx = ctx.staticMemberRest()
        val member = when {
            rCtx.attributeRest() != null ->
                getAttribute(rCtx.attributeRest(), rCtx.optionalReadOnly().text.isNotEmpty(), false)
            rCtx.regularOperation() != null ->
                getRegularOperation(rCtx.regularOperation())
            else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as static member.")
        }
        irBuilder.addMember(Static(member))
    }

    override fun exitIterable(ctx: WebIdlParser.IterableContext) {
        val left = getType(ctx.typeWithExtendedAttributes().type())
        val right = getOptionalType(ctx.optionalType())
        val iterable = if (right != null) {
            Iterable.MapIterable(left, right)
        } else {
            Iterable.CollectionIterable(left)
        }
        irBuilder.addMember(iterable)
    }

    private fun getOptionalType(ctx: WebIdlParser.OptionalTypeContext): Type? {
        return if (ctx.typeWithExtendedAttributes() != null) {
            getType(ctx.typeWithExtendedAttributes().type())
        } else {
            null
        }
    }

    override fun exitAsyncIterable(ctx: WebIdlParser.AsyncIterableContext) {
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
        irBuilder.addMember(iterable)
    }

    override fun exitReadWriteMaplike(ctx: WebIdlParser.ReadWriteMaplikeContext) {
        irBuilder.addMember(getMapLike(ctx.maplikeRest(), false))
    }

    override fun exitReadWriteSetlike(ctx: WebIdlParser.ReadWriteSetlikeContext) {
        irBuilder.addMember(getSetLike(ctx.setlikeRest(), false))
    }

    override fun enterNamespace(ctx: WebIdlParser.NamespaceContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.NAMESPACE)
    }

    override fun exitNamespace(ctx: WebIdlParser.NamespaceContext?) {
        irBuilder.exitDeclaration()
    }

    override fun exitNamespaceMember(ctx: WebIdlParser.NamespaceMemberContext) {
        irBuilder.addMember(
            when {
                ctx.regularOperation() != null -> getRegularOperation(ctx.regularOperation())
                ctx.attributeRest() != null -> getAttribute(ctx.attributeRest(), true, false)
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as namespace member.")
            }
        )
    }

    override fun enterDictionary(ctx: WebIdlParser.DictionaryContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.DICTIONARY)
    }

    override fun exitDictionary(ctx: WebIdlParser.DictionaryContext) {
        irBuilder.exitDeclaration()
    }

    override fun exitDictionaryMemberRest(ctx: WebIdlParser.DictionaryMemberRestContext) {
        irBuilder.addMember(
            when {
                ctx.typeWithExtendedAttributes() != null -> DictionaryEntry(
                    true,
                    getType(ctx.typeWithExtendedAttributes().type()),
                    Identifier(ctx.IDENTIFIER().text),
                    null
                )
                ctx.type() != null -> DictionaryEntry(
                    true,
                    getType(ctx.type()),
                    Identifier(ctx.IDENTIFIER().text),
                    getDefaultValue(ctx.widlDefault())
                )
                else -> throw IllegalArgumentException("Don't know how to interpret \"${ctx.text}\" as dictionary member.")
            }
        )
    }

    override fun enterPartialDictionary(ctx: WebIdlParser.PartialDictionaryContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.DICTIONARY)
    }

    override fun exitPartialDictionary(ctx: WebIdlParser.PartialDictionaryContext) {
        irBuilder.exitDeclaration()
    }

    override fun enterWidlEnum(ctx: WebIdlParser.WidlEnumContext) {
        irBuilder.enterDeclaration(ctx.IDENTIFIER().text, CitizenType.ENUM)
    }

    override fun exitWidlEnum(ctx: WebIdlParser.WidlEnumContext) {
        irBuilder.exitDeclaration()
    }

    override fun exitEnumValueListString(ctx: WebIdlParser.EnumValueListStringContext) {
        if (ctx.STRING() != null) {
            val text = ctx.STRING().text
            irBuilder.addMember(
                EnumEntry(text.substring(1, text.length - 1))
            )
        }
    }

    private fun getCallback(ctx: WebIdlParser.CallbackRestContext): Callback {
        return Callback(Identifier(ctx.IDENTIFIER().text), getType(ctx.type()), getArgumentList(ctx.argumentList()))
    }

    override fun exitTypedef(ctx: WebIdlParser.TypedefContext) {
        irBuilder.addTypedef(getType(ctx.typeWithExtendedAttributes().type()), ctx.IDENTIFIER().text)
    }
}
