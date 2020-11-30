package org.carrat.webidl.build.compile.model.webidlir

enum class CitizenType {
    INTERFACE {
        override fun create(identifier: Identifier, inherits: Identifier?, members: List<Member>) : Declaration {
            return Interface(identifier, inherits, members)
        }
    },
    MIXIN {
        override fun create(identifier: Identifier, inherits: Identifier?, members: List<Member>): Declaration {
            return InterfaceMixin(identifier, members)
        }
    },
    CALLBACK_INTERFACE {
        override fun create(identifier: Identifier, inherits: Identifier?, members: List<Member>): Declaration {
            return CallbackInterface(identifier, members)
        }
    },
    NAMESPACE {
        override fun create(identifier: Identifier, inherits: Identifier?, members: List<Member>): Declaration {
            return Namespace(identifier, members)
        }
    },
    DICTIONARY {
        override fun create(identifier: Identifier, inherits: Identifier?, members: List<Member>): Declaration {
            return Dictionary(identifier, inherits, members)
        }
    },
    ENUM {
        override fun create(identifier: Identifier, inherits: Identifier?, members: List<Member>): Declaration {
            return Enum(
                identifier,
                values = members.map { (it as EnumEntry).name }
            )
        }
    };

    abstract fun create(identifier: Identifier, inherits : Identifier?, members : List<Member>) : Declaration
}