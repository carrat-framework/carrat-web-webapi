package org.carrat.webidl.build.compile.model.kotlinir

data class KPackage(val parent: KPackage?, val name: String) {
    override fun toString(): String {
        return if (parent != null) {
            "$parent.$name"
        } else {
            name
        }
    }

    companion object {
        fun parse(packageName: String): KPackage? {
            return packageName.split('.').fold<String, KPackage?>(null) { p, n -> KPackage(p, n) }!!
        }
    }
}
