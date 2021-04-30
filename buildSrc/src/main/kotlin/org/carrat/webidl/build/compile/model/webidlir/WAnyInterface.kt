package org.carrat.webidl.build.compile.model.webidlir

abstract class WAnyInterface : WDeclaration() {
    override val inherits: String? = null
    abstract override val members: List<WMember>
    abstract val includes: List<String>
}
