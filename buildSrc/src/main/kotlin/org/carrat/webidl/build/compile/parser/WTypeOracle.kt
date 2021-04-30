package org.carrat.webidl.build.compile.parser

import org.carrat.webidl.build.compile.model.webidlir.WIndex
import org.carrat.webidl.build.compile.model.webidlir.types.WType
import org.carrat.webidl.build.compile.model.webidlir.types.WTypeExpression

class WTypeOracle(
    val index: WIndex
) {
    private val resolvedTypes = mutableMapOf<WTypeExpression, WType>()

    fun resolveType() {

    }
}
