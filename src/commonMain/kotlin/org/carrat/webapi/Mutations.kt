package org.carrat.webapi

/** Removes all the children from this node */
public fun Node.clear() {
    while (hasChildNodes()) {
        removeChild(firstChild!!)
    }
}

/**
 * Removes this node from parent node. Does nothing if no parent node
 */
public fun Node.removeFromParent() {
    parentNode?.removeChild(this)
}

public operator fun Node.plus(child: Node): Node {
    appendChild(child)
    return this
}

public operator fun Element.plus(text: String): Element = appendText(text)
public operator fun Element.plusAssign(text: String): Unit {
    appendText(text)
}

/** Returns the owner document of the element or uses the provided document */
public fun Node.ownerDocument(doc: Document? = null): Document = when {
    nodeType == Node.DOCUMENT_NODE -> this as Document
    else -> doc ?: ownerDocument ?: throw IllegalArgumentException("Neither node contains nor parameter doc provides an owner document for $this")
}

/**
 * Creates text node and append it to the element
 */
public fun Element.appendText(text: String, doc : Document? = null): Element {
    appendChild(ownerDocument(doc).createTextNode(text))
    return this
}

/**
 * Appends the node to the specified parent element
 */
public fun Node.appendTo(parent: Element) {
    parent.appendChild(this)
}
