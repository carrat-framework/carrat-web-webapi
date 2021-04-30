package org.carrat.webidl.build.dukat

import org.carrat.webidl.build.compile.model.ir.Target
import org.carrat.webidl.build.compile.model.kotlinir.KName
import org.carrat.webidl.build.compile.model.kotlinir.KPackage
import org.carrat.webidl.build.compile.model.kotlinir.KTypeAlias
import org.carrat.webidl.build.compile.model.kotlinir.value.KDefinedExternally
import org.jetbrains.dukat.astModel.TypeAliasModel

class CommonTranslator(`package`: KPackage) :
    BaseTranslator(`package`, Target.COMMON, false, false, KDefinedExternally) {
    override fun translateTypeAlias(typeAlias: TypeAliasModel): KTypeAlias {
        return KTypeAlias(KName(`package`, typeAlias.name.toString()), translateType(typeAlias.typeReference))
    }
}
