package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Class
import org.carrat.webidl.build.compile.model.ir.Declaration

class WInterface(
    override val sourceFile: String,
    override val identifier: String,
    override val inherits: String?,
    override val includes: List<String>,
    override val members: List<WMember>
) : WAnyInterface() {
    override fun WidlContext.toIr(): Declaration {
        return Class(
            identifier,
            inherits,
            includes,
            members.mapNotNull { with(it) { toIr(identifier) } }
        )
    }
}
