package org.carrat.webidl.build.compile.model.webidlir

enum class CitizenType {
    INTERFACE {
        override fun create(
            sourceFile: String,
            identifier: String,
            inherits: String?,
            includes: List<String>,
            members: List<WMember>
        ): WDeclaration {
            return WInterface(sourceFile, identifier, inherits, includes, members)
        }
    },
    MIXIN {
        override fun create(
            sourceFile: String,
            identifier: String,
            inherits: String?,
            includes: List<String>,
            members: List<WMember>
        ): WDeclaration {
            return WInterfaceMixin(sourceFile, identifier, includes, members)
        }
    },
    CALLBACK_INTERFACE {
        override fun create(
            sourceFile: String,
            identifier: String,
            inherits: String?,
            includes: List<String>,
            members: List<WMember>
        ): WDeclaration {
            assert(includes.isEmpty())
            return WCallbackInterface(sourceFile, identifier, members)
        }
    },
    NAMESPACE {
        override fun create(
            sourceFile: String,
            identifier: String,
            inherits: String?,
            includes: List<String>,
            members: List<WMember>
        ): WDeclaration {
            assert(includes.isEmpty())
            return WNamespace(sourceFile, identifier, members)
        }
    },
    DICTIONARY {
        override fun create(
            sourceFile: String,
            identifier: String,
            inherits: String?,
            includes: List<String>,
            members: List<WMember>
        ): WDeclaration {
            assert(includes.isEmpty())
            return WDictionary(sourceFile, identifier, inherits, members)
        }
    },
    ENUM {
        override fun create(
            sourceFile: String,
            identifier: String,
            inherits: String?,
            includes: List<String>,
            members: List<WMember>
        ): WDeclaration {
            assert(includes.isEmpty())
            return WEnum(
                sourceFile,
                identifier,
                values = members.map { (it as EnumEntry).name.value }
            )
        }
    };

    abstract fun create(
        sourceFile: String,
        identifier: String,
        inherits: String?,
        includes: List<String>,
        members: List<WMember>
    ): WDeclaration
}
