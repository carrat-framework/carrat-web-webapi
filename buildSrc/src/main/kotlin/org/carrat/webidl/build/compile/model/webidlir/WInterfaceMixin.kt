package org.carrat.webidl.build.compile.model.webidlir

import org.carrat.webidl.build.compile.model.ir.Declaration
import org.carrat.webidl.build.compile.model.ir.Interface

class WInterfaceMixin(
    override val sourceFile: String,
    override val identifier: String,
    override val includes: List<String>,
    override val members: List<WMember>
) : WAnyInterface() {
    override fun WidlContext.toIr(): Declaration {
        return Interface(
            identifier,
            members.filterIsInstance<WIncludes>().map { it.inherits },
            members.mapNotNull { with(it) { toIr(identifier) } },
            false
        )
    }
}
