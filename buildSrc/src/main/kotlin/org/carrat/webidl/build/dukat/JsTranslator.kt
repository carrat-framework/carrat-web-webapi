package org.carrat.webidl.build.dukat

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.KTypeAlias
import org.carrat.webidl.build.compile.model.kotlinir.value.KDefinedExternally
import org.jetbrains.dukat.astModel.TypeAliasModel

class JsTranslator(`package`: KPackage) : BaseTranslator(`package`, Target.JS, true, true, KDefinedExternally) {
    override fun translateTypeAlias(typeAlias: TypeAliasModel): KTypeAlias? = null
}
