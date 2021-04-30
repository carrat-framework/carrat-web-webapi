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
    :    identifier inheritance '{' interfaceMembers '}' ';'
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
    :    identifier '{' partialInterfaceMembers '}' ';'
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
    :    ':' identifier
    |    // ε
    ;

mixinRest
    :    'mixin' identifier '{' mixinMembers '}' ';'
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
    :    identifier 'includes' identifier ';'
    ;

callbackRestOrInterface
    :    callbackRest
    |    'interface' identifier '{' callbackInterfaceMembers '}' ';'
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
    :    'const' constType identifier '=' constValue ';'
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
    |    identifier
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
    :    attributeNameKeyword
    |    identifier
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
    |    identifier
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
    |    identifier
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
    :    'namespace' identifier '{' namespaceMembers '}' ';'
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
    :    'dictionary' identifier inheritance '{' dictionaryMembers '}' ';'
    ;

dictionaryMembers
    :    dictionaryMember dictionaryMembers
    |    // ε
    ;

dictionaryMember
    :    extendedAttributeList dictionaryMemberRest
    ;

dictionaryMemberRest
    :    'required' typeWithExtendedAttributes identifier ';'
    |    type identifier widlDefault ';'
    ;

partialDictionary
    :    'dictionary' identifier '{' dictionaryMembers '}' ';'
    ;

widlDefault
    :    '=' defaultValue
    |    // ε
    ;

widlEnum
    :    'enum' identifier '{' enumValueList '}' ';'
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
    :    identifier '=' type '(' argumentList ')' ';'
    ;

typedef
    :    'typedef' typeWithExtendedAttributes identifier ';'
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
    |    identifier widlNull
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
    |    identifier
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
    :    identifier identifiers
    ;

identifiers
    :    ',' identifier identifiers
    |    // ε
    ;

extendedAttributeNoArgs
    :    identifier
    ;

extendedAttributeArgList
    :    identifier '(' argumentList ')'
    ;

extendedAttributeIdent
    :    identifier '=' identifier
    ;

extendedAttributeIdentList
    :    identifier '=' '(' identifierList ')'
    ;

extendedAttributeNamedArgList
    :    identifier '=' identifier '(' argumentList ')'
    ;

identifier
    :    IDENTIFIER
    |    'float'
    |    bufferRelatedType
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
