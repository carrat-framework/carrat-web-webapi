package org.carrat.webidl.build.dukat

import com.google.common.collect.Multimaps
import org.carrat.webidl.build.compile.model.webidlir.*
import org.carrat.webidl.build.compile.model.webidlir.types.*
import org.jetbrains.dukat.idlDeclarations.*

object WidlToDukatTranslator {
    fun translate(declarations: Collection<WDeclaration>): IDLSourceSetDeclaration {
        return IDLSourceSetDeclaration(
            Multimaps.index(declarations) { it!!.sourceFile }.asMap()
                .map {
                    IDLFileDeclaration(
                        it.key,
                        it.value.map { translateDeclaration(it) } + it.value.flatMap { collectIncludes(it) },
                        emptyList(),
                        null
                    )
                }
        )
    }

    private fun translateDeclaration(declaration: WDeclaration): IDLTopLevelDeclaration {
        return when (declaration) {
            is WTypedef -> translateTypedef(declaration)
            is WCallback -> translateCallback(declaration)
            is WCallbackInterface -> translateCallbackInterface(declaration)
            is WInterfaceMixin -> translateInterfaceMixin(declaration)
            is WInterface -> translateInterface(declaration)
            is WEnum -> translateEnum(declaration)
            is WNamespace -> translateNamespace(declaration)
            is WDictionary -> translateDictionary(declaration)
            else -> TODO()
        }
    }

    private fun collectIncludes(declaration: WDeclaration): Collection<IDLIncludesStatementDeclaration> {
        return when (declaration) {
            is WAnyInterface -> {
                declaration.includes.map {
                    IDLIncludesStatementDeclaration(
                        IDLSingleTypeDeclaration(declaration.identifier, null, false),
                        IDLSingleTypeDeclaration(it, null, false)
                    )
                }
            }
            else -> emptyList()
        }
    }

    private fun translateTypedef(typedef: WTypedef): IDLTypedefDeclaration {
        return IDLTypedefDeclaration(typedef.identifier, translateTypeExpression(typedef.type))
    }

    private fun translateCallback(callback: WCallback): IDLTypedefDeclaration {
        return IDLTypedefDeclaration(
            callback.identifier,
            IDLFunctionTypeDeclaration(
                callback.identifier,
                translateTypeExpression(callback.type),
                callback.arguments.map { translateArgument(it) },
                false
            )
        )
    }

    private fun translateCallbackInterface(declaration: WCallbackInterface): IDLInterfaceDeclaration {
        val operations = declaration.members.filterIsInstance<WOperation>().map { translateOperation(it) }
        return IDLInterfaceDeclaration(
            declaration.identifier,
            declaration.members.mapNotNull { translateAttribute(it) },
            operations.filterIsInstance<IDLOperationDeclaration>(),
            primaryConstructor = null,
            constructors = emptyList(),//TODO??
            parents = declaration.inherits?.let { listOf(idlSingleType(it)) } ?: emptyList(),
            unions = emptyList(),
            extendedAttributes = emptyList(),
            getters = operations.filterIsInstance<IDLGetterDeclaration>(),
            setters = operations.filterIsInstance<IDLSetterDeclaration>(),
            kind = InterfaceKind.INTERFACE,
            callback = true,
            generated = false,
            partial = false,
            mixin = false
        )
    }

    private fun translateInterfaceMixin(declaration: WInterfaceMixin): IDLInterfaceDeclaration {
        val operations = declaration.members.filterIsInstance<WOperation>().map { translateOperation(it) }
        return IDLInterfaceDeclaration(
            declaration.identifier,
            declaration.members.mapNotNull { translateAttribute(it) },
            operations.filterIsInstance<IDLOperationDeclaration>(),
            primaryConstructor = null,
            constructors = emptyList(),//TODO??
            parents = declaration.inherits?.let { listOf(idlSingleType(it)) } ?: emptyList(),
            unions = emptyList(),
            extendedAttributes = emptyList(),
            getters = operations.filterIsInstance<IDLGetterDeclaration>(),
            setters = operations.filterIsInstance<IDLSetterDeclaration>(),
            kind = InterfaceKind.INTERFACE,
            callback = false,
            generated = false,
            partial = false,
            mixin = true
        )
    }

    private fun translateInterface(declaration: WInterface): IDLInterfaceDeclaration {
        val operations = declaration.members.filterIsInstance<WOperation>().map { translateOperation(it) }
        return IDLInterfaceDeclaration(
            declaration.identifier,
            declaration.members.mapNotNull { translateAttribute(it) },
            operations.filterIsInstance<IDLOperationDeclaration>(),
            primaryConstructor = null,
            constructors = declaration.members.filterIsInstance<WConstructor>().map { translateConstructor(it) },
            parents = declaration.inherits?.let { listOf(idlSingleType(it)) } ?: emptyList(),
            unions = emptyList(),
            extendedAttributes = emptyList(),
            getters = operations.filterIsInstance<IDLGetterDeclaration>(),
            setters = operations.filterIsInstance<IDLSetterDeclaration>(),
            kind = InterfaceKind.INTERFACE,
            callback = false,
            generated = false,
            partial = false,
            mixin = false
        )
    }

    private fun translateEnum(declaration: WEnum): IDLEnumDeclaration {
        return IDLEnumDeclaration(
            declaration.identifier,
            declaration.values,
            emptyList(),
            false
        )
    }

    private fun translateNamespace(declaration: WNamespace): IDLNamespaceDeclaration {
        val operations = declaration.members.filterIsInstance<WOperation>().map { translateOperation(it) }
        return IDLNamespaceDeclaration(
            declaration.identifier,
            declaration.members.mapNotNull { translateAttribute(it) },
            operations.filterIsInstance<IDLOperationDeclaration>(),
            false
        )
    }

    private fun translateDictionary(declaration: WDictionary): IDLDictionaryDeclaration {
        return IDLDictionaryDeclaration(
            declaration.identifier,
            declaration.members.filterIsInstance<WDictionaryEntry>().map { translateDictionaryEntry(it) },
            emptyList(),
            emptyList(),
            false
        )
    }

    private fun translateDictionaryEntry(entry: WDictionaryEntry): IDLDictionaryMemberDeclaration {
        return IDLDictionaryMemberDeclaration(
            entry.identifier,
            translateTypeExpression(entry.type),
            null,//TODO
            entry.required
        )
    }

    private fun translateAttribute(member: WMember): IDLAttributeDeclaration? {
        return when (member) {
            is WAttribute -> IDLAttributeDeclaration(
                member.name,
                translateTypeExpression(member.type),
                member.static,
                member.readOnly,
                false
            )
            is WConstant -> IDLAttributeDeclaration(
                member.name,
                translateTypeExpression(member.type),
                true,
                true,
                false
            )
            else -> null
        }
    }

    private fun translateOperation(operation: WOperation): IDLMemberDeclaration? {
        return when (operation.special) {
            WOperation.Special.GETTER ->
                IDLGetterDeclaration(
                    operation.name ?: "get",
                    translateArgument(operation.arguments.single()),
                    translateTypeExpression(operation.type)
                )
            WOperation.Special.SETTER ->
                IDLSetterDeclaration(
                    operation.name ?: "set",
                    translateArgument(operation.arguments[0]),
                    translateArgument(operation.arguments[1])
                )
            WOperation.Special.DELETER, null -> {
                if (operation.name != null) {
                    IDLOperationDeclaration(
                        operation.name,
                        translateTypeExpression(operation.type),
                        operation.arguments.map { translateArgument(it) },
                        operation.static,
                        false
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun translateConstructor(operation: WConstructor): IDLConstructorDeclaration {
        return IDLConstructorDeclaration(
            operation.arguments.map { translateArgument(it) }
        )
    }

    private fun translateArgument(argument: WArgument): IDLArgumentDeclaration {
        return IDLArgumentDeclaration(
            argument.argumentName,
            translateTypeExpression(argument.type),
            null, //TODO
            argument.optional,
            argument.vararg
        )
    }

    private fun translateTypeExpression(type: WTypeExpression): IDLTypeDeclaration {
        return when (type) {
            is WSequenceTypeExpression -> translateSequenceType(type)
            is WRecordTypeExpression -> translateRecordType(type)
            is WNullableTypeExpression -> translateNullableType(type)
            is WFrozenArrayTypeExpression -> translateFrozenArrayType(type)
            is WType -> translateType(type)
            is WUnionTypeExpression -> translateUnionType(type)
            is WPromiseTypeExpression -> translatePromiseType(type)
            is WTypeReference -> translateTypeReference(type)
            else -> TODO()
        }
    }

    private fun translateSequenceType(type: WSequenceTypeExpression): IDLSingleTypeDeclaration {
        return idlSingleType("sequence", translateTypeExpression(type.elementType))
    }

    private fun translateRecordType(type: WRecordTypeExpression): IDLSingleTypeDeclaration {
        return idlSingleType("record", null)
    }

    private fun translateNullableType(type: WNullableTypeExpression): IDLTypeDeclaration {
        return translateTypeExpression(type.baseType).toNullable()
    }

    private fun translateFrozenArrayType(type: WFrozenArrayTypeExpression): IDLSingleTypeDeclaration {
        return idlSingleType("FrozenArray", translateTypeExpression(type.elementType))
    }

    private fun translateType(type: WType): IDLTypeDeclaration {
        return when (type) {
            is WStringType -> translateStringType(type)
            WAnyType -> anyType
            is WPrimitiveType -> translatePrimitiveType(type)
            is WBufferRelatedType -> translateBufferRelatedType(type)
            WVoidType -> voidType
            WObjectType -> objectType
            WSymbolType -> symbolType
            else -> TODO()
        }
    }

    private fun translateUnionType(type: WUnionTypeExpression): IDLTypeDeclaration {
        val unionMembers = type.types.map { translateTypeExpression(it) }
        val name = unionMembers.sortedBy { it.name }.joinToString(
            separator = "Or",
            prefix = "Union"
        ) { it.name }
        return IDLUnionTypeDeclaration(name, unionMembers, null, false)
    }

    private fun translatePromiseType(type: WPromiseTypeExpression): IDLSingleTypeDeclaration {
        return idlSingleType("Promise", translateTypeExpression(type.memberType))
    }

    private fun translateTypeReference(type: WTypeReference): IDLSingleTypeDeclaration {
        return if (type.identifier == "dynamic") {
            idlSingleType("\$dynamic")
        } else {
            idlSingleType(type.identifier)
        }
    }

    private fun translateStringType(type: WStringType): IDLTypeDeclaration {
        val typeName = when (type) {
            WStringType.ByteString -> "ByteString"
            WStringType.DOMString -> "DOMString"
            WStringType.USVString -> "USVString"
        }

        return idlSingleType(typeName)
    }

    private fun translatePrimitiveType(type: WPrimitiveType): IDLSingleTypeDeclaration {
        return when (type) {
            is WIntegerType -> translateIntegerType(type)
            is WFloatType -> translateFloatType(type)
            WUndefinedType -> undefinedType
            WBooleanType -> booleanType
            WByteType -> byteType
            WOctetType -> octetType
            WBigIntType -> bigIntType
            else -> TODO()
        }
    }

    private fun translateIntegerType(type: WIntegerType): IDLSingleTypeDeclaration {
        val prefix = if (type.unsigned) "unsigned" else ""
        val suffix = when (type.baseType) {
            IntegerBaseType.SHORT -> "short"
            IntegerBaseType.LONG -> "long"
            IntegerBaseType.LONG_LONG -> "longlong"
        }
        return idlSingleType(prefix + suffix)
    }

    private fun translateFloatType(type: WFloatType): IDLSingleTypeDeclaration {
        val prefix = if (type.unrestricted) "unrestricted" else ""
        val suffix = when (type.baseType) {
            FloatBaseType.FLOAT -> "float"
            FloatBaseType.DOUBLE -> "double"
        }
        return idlSingleType(prefix + suffix)
    }

    private fun translateBufferRelatedType(type: WBufferRelatedType): IDLSingleTypeDeclaration {
        return when (type) {
            WBufferRelatedType.ArrayBuffer -> arrayBufferType
            WBufferRelatedType.DataView -> dataViewType
            WBufferRelatedType.Int8Array -> int8ArrayType
            WBufferRelatedType.Int16Array -> int16ArrayType
            WBufferRelatedType.Int32Array -> int32ArrayType
            WBufferRelatedType.Uint8Array -> uInt8ArrayType
            WBufferRelatedType.Uint16Array -> uInt16ArrayType
            WBufferRelatedType.Uint32Array -> uInt32ArrayType
            WBufferRelatedType.Uint8ClampedArray -> uInt8ClampedArrayType
            WBufferRelatedType.Float32Array -> float32ArrayType
            WBufferRelatedType.Float64Array -> float64ArrayType
        }
    }

    private val anyType = idlSingleType("any")
    private val voidType = idlSingleType("void")
    private val objectType = idlSingleType("object")
    private val symbolType = idlSingleType("symbol")

    private val undefinedType = idlSingleType("undefined")
    private val booleanType = idlSingleType("boolean")
    private val byteType = idlSingleType("byte")
    private val octetType = idlSingleType("octet")
    private val bigIntType = idlSingleType("bigint")

    private val arrayBufferType = idlSingleType("ArrayBuffer")
    private val dataViewType = idlSingleType("DataView")
    private val int8ArrayType = idlSingleType("Int8Array")
    private val int16ArrayType = idlSingleType("Int16Array")
    private val int32ArrayType = idlSingleType("Int32Array")
    private val uInt8ArrayType = idlSingleType("Uint8Array")
    private val uInt16ArrayType = idlSingleType("Uint16Array")
    private val uInt32ArrayType = idlSingleType("Uint32Array")
    private val uInt8ClampedArrayType = idlSingleType("Uint8ClampedArray")
    private val float32ArrayType = idlSingleType("Float32Array")
    private val float64ArrayType = idlSingleType("Float64Array")


    private fun idlSingleType(name: String, typeParameter: IDLTypeDeclaration? = null): IDLSingleTypeDeclaration {
        return IDLSingleTypeDeclaration(
            name,
            typeParameter,
            false
        )
    }
}
