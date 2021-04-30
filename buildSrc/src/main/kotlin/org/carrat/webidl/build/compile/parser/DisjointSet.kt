package org.carrat.webidl.build.compile.parser

internal class DisjointSet<Element>(
    val element: Element
) {
    var parent: DisjointSet<Element> = this

    fun find(): DisjointSet<Element> {
        if (this != parent) {
            parent = parent.find()
        }
        return parent
    }
}

internal fun <Element> union(a: DisjointSet<Element>, b: DisjointSet<Element>) {
    a.find().parent = b.find()
}
