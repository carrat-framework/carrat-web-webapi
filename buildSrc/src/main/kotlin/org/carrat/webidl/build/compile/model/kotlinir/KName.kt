package org.carrat.webidl.build.compile.model.kotlinir

import com.squareup.kotlinpoet.ClassName

data class KName(
    val `package`: KPackage,
    val name: String
) {
    fun toPoet(): ClassName {
        return ClassName(`package`.toString(), name)
    }

    override fun toString(): String {
        return "$`package`.$name"
    }


}
