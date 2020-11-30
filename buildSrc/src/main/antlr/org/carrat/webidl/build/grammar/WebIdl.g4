grammar WebIdl;

@header {
package org.carrat.webidl.build.grammar;
}

document
    :    definitions EOF
    ;

definitions
    :    extendedAttributeList definition definitions
    |    // ε
    ;

definition
    :    callbackOrInterfaceOrMixin
    |    namespace
    |    partial
    |    dictionary
    |    widlEnum
    |    typedef
    |    includesStatement
    ;

argumentNameKeyword
    :    'async'
    |    'attribute'
    |    'callback'
    |    'const'
    |    'constructor'
    |    'deleter'
    |    'dictionary'
    |    'enum'
    |    'getter'
    |    'includes'
    |    'inherit'
    |    'interface'
    |    'iterable'
    |    'maplike'
    |    'mixin'
    |    'namespace'
    |    'partial'
    |    'readonly'
    |    'required'
    |    'setlike'
    |    'setter'
    |    'static'
    |    'stringifier'
    |    'typedef'
    |    'unrestricted'
    ;

callbackOrInterfaceOrMixin
    :    'callback' callbackRestOrInterface
    |    'interface' interfaceOrMixin
    ;

interfaceOrMixin
    :    interfaceRest
    |    mixinRest
    ;

interfaceRest
    :    IDENTIFIER inheritance '{' interfaceMembers '}' ';'
    ;

partial
    :    'partial' partialDefinition
    ;

partialDefinition
    :    'interface' partialInterfaceOrPartialMixin
    |    partialDictionary
    |    namespace
    ;

partialInterfaceOrPartialMixin
    :    partialInterfaceRest
    |    mixinRest
    ;

partialInterfaceRest
    :    IDENTIFIER '{' partialInterfaceMembers '}' ';'
    ;

interfaceMembers
    :    extendedAttributeList interfaceMember interfaceMembers
    |    // ε
    ;

interfaceMember
    :    partialInterfaceMember
    |    constructor
    ;

partialInterfaceMembers
    :    extendedAttributeList partialInterfaceMember partialInterfaceMembers
    |    // ε
    ;

partialInterfaceMember
    :    widlConst
    |    operation
    |    stringifier
    |    staticMember
    |    iterable
    |    asyncIterable
    |    readOnlyMember
    |    readWriteAttribute
    |    readWriteMaplike
    |    readWriteSetlike
    |    inheritAttribute
    ;

inheritance
    :    ':' IDENTIFIER
    |    // ε
    ;

mixinRest
    :    'mixin' IDENTIFIER '{' mixinMembers '}' ';'
    ;

mixinMembers
    :    extendedAttributeList mixinMember mixinMembers
    |    // ε
    ;

mixinMember
    :    widlConst
    |    regularOperation
    |    stringifier
    |    optionalReadOnly attributeRest
    ;

includesStatement
    :    IDENTIFIER 'includes' IDENTIFIER ';'
    ;

callbackRestOrInterface
    :    callbackRest
    |    'interface' IDENTIFIER '{' callbackInterfaceMembers '}' ';'
    ;

callbackInterfaceMembers
    :    extendedAttributeList callbackInterfaceMember callbackInterfaceMembers
    |    // ε
    ;

callbackInterfaceMember
    :    widlConst
    |    regularOperation
    ;

widlConst
    :    'const' constType IDENTIFIER '=' constValue ';'
    ;

constValue
    :    booleanLiteral
    |    floatLiteral
    |    INTEGER
    ;

booleanLiteral
    :    'true'
    |    'false'
    ;

floatLiteral
    :    DECIMAL
    |    '-Infinity'
    |    'Infinity'
    |    'NaN'
    ;

constType
    :    primitiveType
    |    IDENTIFIER
    ;

readOnlyMember
    :    'readonly' readOnlyMemberRest
    ;

readOnlyMemberRest
    :    attributeRest
    |    maplikeRest
    |    setlikeRest
    ;

readWriteAttribute
    :    attributeRest
    ;

inheritAttribute
    :    'inherit' attributeRest
    ;

attributeRest
    :    'attribute' typeWithExtendedAttributes attributeName ';'
    ;

attributeName
    :    'float'
    |    attributeNameKeyword
    |    IDENTIFIER
    ;

attributeNameKeyword
    :    'async'
    |    'required'
    ;

optionalReadOnly
    :    'readonly'
    |    // ε
    ;

defaultValue
    :    constValue
    |    STRING
    |    '[' ']'
    |    '{' '}'
    |    'null'
    ;

operation
    :    regularOperation
    |    specialOperation
    ;

regularOperation
    :    type operationRest
    ;

specialOperation
    :    special regularOperation
    ;

special
    :    'getter'
    |    'setter'
    |    'deleter'
    ;

operationRest
    :    optionalOperationName '(' argumentList ')' ';'
    ;

optionalOperationName
    :    operationName
    |    // ε
    ;

operationName
    :    operationNameKeyword
    |    IDENTIFIER
    ;

operationNameKeyword
    :    'includes'
    ;

argumentList
    :    argument arguments
    |    // ε
    ;

arguments
    :    ',' argument arguments
    |    // ε
    ;

argument
    :    extendedAttributeList argumentRest
    ;

argumentRest
    :    'optional' typeWithExtendedAttributes argumentName widlDefault
    |    type ellipsis argumentName
    ;

argumentName
    :    argumentNameKeyword
    |    IDENTIFIER
    ;

ellipsis
    :    '...'
    |    // ε
    ;

constructor
    :    'constructor' '(' argumentList ')' ';'
    ;

stringifier
    :    'stringifier' stringifierRest
    ;

stringifierRest
    :    optionalReadOnly attributeRest
    |    regularOperation
    |    ';'
    ;

staticMember
    :    'static' staticMemberRest
    ;

staticMemberRest
    :    optionalReadOnly attributeRest
    |    regularOperation
    ;

iterable
    :    'iterable' '<' typeWithExtendedAttributes optionalType '>' ';'
    ;

optionalType
    :    ',' typeWithExtendedAttributes
    |    // ε
    ;

asyncIterable
    :    'async' 'iterable' '<' typeWithExtendedAttributes optionalType '>' optionalArgumentList ';'
    ;

optionalArgumentList
    :    '(' argumentList ')'
    |    // ε
    ;

readWriteMaplike
    :    maplikeRest
    ;

maplikeRest
    :    'maplike' '<' typeWithExtendedAttributes ',' typeWithExtendedAttributes '>' ';'
    ;

readWriteSetlike
    :    setlikeRest
    ;

setlikeRest
    :    'setlike' '<' typeWithExtendedAttributes '>' ';'
    ;

namespace
    :    'namespace' IDENTIFIER '{' namespaceMembers '}' ';'
    ;

namespaceMembers
    :    extendedAttributeList namespaceMember namespaceMembers
    |    // ε
    ;

namespaceMember
    :    regularOperation
    |    'readonly' attributeRest
    ;

dictionary
    :    'dictionary' IDENTIFIER inheritance '{' dictionaryMembers '}' ';'
    ;

dictionaryMembers
    :    dictionaryMember dictionaryMembers
    |    // ε
    ;

dictionaryMember
    :    extendedAttributeList dictionaryMemberRest
    ;

dictionaryMemberRest
    :    'required' typeWithExtendedAttributes IDENTIFIER ';'
    |    type IDENTIFIER widlDefault ';'
    ;

partialDictionary
    :    'dictionary' IDENTIFIER '{' dictionaryMembers '}' ';'
    ;

widlDefault
    :    '=' defaultValue
    |    // ε
    ;

widlEnum
    :    'enum' IDENTIFIER '{' enumValueList '}' ';'
    ;

enumValueList
    :    STRING enumValueListComma
    ;

enumValueListComma
    :    ',' enumValueListString
    |    // ε
    ;

enumValueListString
    :    STRING enumValueListComma
    |    // ε
    ;

callbackRest
    :    IDENTIFIER '=' type '(' argumentList ')' ';'
    ;

typedef
    :    'typedef' typeWithExtendedAttributes IDENTIFIER ';'
    ;

type
    :    singleType
    |    unionType widlNull
    ;

typeWithExtendedAttributes
    :    extendedAttributeList type
    ;

singleType
    :    distinguishableType
    |    'any'
    |    promiseType
    ;

unionType
    :    '(' unionMemberType 'or' unionMemberType unionMemberTypes ')'
    ;

unionMemberType
    :    extendedAttributeList distinguishableType
    |    unionType widlNull
    ;

unionMemberTypes
    :    'or' unionMemberType unionMemberTypes
    |    // ε
    ;

distinguishableType
    :    primitiveType widlNull
    |    stringType widlNull
    |    IDENTIFIER widlNull
    |    'sequence' '<' typeWithExtendedAttributes '>' widlNull
    |    'object' widlNull
    |    'symbol' widlNull
    |    bufferRelatedType widlNull
    |    'FrozenArray' '<' typeWithExtendedAttributes '>' widlNull
    |    'ObservableArray' '<' typeWithExtendedAttributes '>' widlNull
    |    recordType widlNull
    ;

primitiveType
    :    unsignedIntegerType
    |    unrestrictedFloatType
    |    'undefined'
    |    'boolean'
    |    'byte'
    |    'octet'
    |    'bigint'
    ;

unrestrictedFloatType
    :    'unrestricted' floatType
    |    floatType
    ;

floatType
    :    'float'
    |    'double'
    ;

unsignedIntegerType
    :    'unsigned' integerType
    |    integerType
    ;

integerType
    :    'short'
    |    'long' optionalLong
    ;

optionalLong
    :    'long'
    |    // ε
    ;

stringType
    :    'ByteString'
    |    'DOMString'
    |    'USVString'
    ;

promiseType
    :    'Promise' '<' type '>'
    ;

recordType
    :    'record' '<' stringType ',' typeWithExtendedAttributes '>'
    ;

widlNull
    :    '?'
    |    // ε
    ;

bufferRelatedType
    :    'ArrayBuffer'
    |    'DataView'
    |    'Int8Array'
    |    'Int16Array'
    |    'Int32Array'
    |    'Uint8Array'
    |    'Uint16Array'
    |    'Uint32Array'
    |    'Uint8ClampedArray'
    |    'Float32Array'
    |    'Float64Array'
    ;

extendedAttributeList
    :    '[' extendedAttribute extendedAttributes ']'
    |    // ε
    ;

extendedAttributes
    :    ',' extendedAttribute extendedAttributes
    |    // ε
    ;

extendedAttribute
    :    '(' extendedAttributeInner ')' extendedAttributeRest
    |    '[' extendedAttributeInner ']' extendedAttributeRest
    |    '{' extendedAttributeInner '}' extendedAttributeRest
    |    other extendedAttributeRest
    ;

extendedAttributeRest
    :    extendedAttribute
    |    // ε
    ;

extendedAttributeInner
    :    '(' extendedAttributeInner ')' extendedAttributeInner
    |    '[' extendedAttributeInner ']' extendedAttributeInner
    |    '{' extendedAttributeInner '}' extendedAttributeInner
    |    otherOrComma extendedAttributeInner
    |    // ε
    ;

other
    :    INTEGER
    |    DECIMAL
    |    IDENTIFIER
    |    STRING
    |    OTHER
    |    '-'
    |    '-Infinity'
    |    '.'
    |    '...'
    |    ':'
    |    ';'
    |    '<'
    |    '='
    |    '>'
    |    '?'
    |    'ByteString'
    |    'DOMString'
    |    'FrozenArray'
    |    'Infinity'
    |    'NaN'
    |    'ObservableArray'
    |    'Promise'
    |    'USVString'
    |    'any'
    |    'bigint'
    |    'boolean'
    |    'byte'
    |    'double'
    |    'false'
    |    'float'
    |    'long'
    |    'null'
    |    'object'
    |    'octet'
    |    'or'
    |    'optional'
    |    'record'
    |    'sequence'
    |    'short'
    |    'symbol'
    |    'true'
    |    'unsigned'
    |    'undefined'
    |    argumentNameKeyword
    |    bufferRelatedType
    ;

otherOrComma
    :    other
    |    ','
    ;

identifierList
    :    IDENTIFIER identifiers
    ;

identifiers
    :    ',' IDENTIFIER identifiers
    |    // ε
    ;

extendedAttributeNoArgs
    :    IDENTIFIER
    ;

extendedAttributeArgList
    :    IDENTIFIER '(' argumentList ')'
    ;

extendedAttributeIdent
    :    IDENTIFIER '=' IDENTIFIER
    ;

extendedAttributeIdentList
    :    IDENTIFIER '=' '(' identifierList ')'
    ;

extendedAttributeNamedArgList
    :    IDENTIFIER '=' IDENTIFIER '(' argumentList ')'
    ;

INTEGER
    :    '-'?([1-9][0-9]*|'0'[Xx][0-9A-Fa-f]+|'0'[0-7]*)
    ;

DECIMAL
    :    '-'?(([0-9]+'.'[0-9]*|[0-9]*'.'[0-9]+)([Ee][+\-]?[0-9]+)?|[0-9]+[Ee][+\-]?[0-9]+)
    ;

IDENTIFIER
    :    [_\-]?[A-Za-z][0-9A-Z_a-z\-]*
    ;

STRING
    :    '"'~["]*'"'
    ;

WHITESPACE
    :    [\t\n\r ]+ -> skip
    ;

COMMENT
    :    (    '//' ~[\r\n]*
         |    '/*' .*? '*/'
         ) -> skip
    ;

OTHER
    :    ~[\t\n\r 0-9A-Za-z]
    ;
