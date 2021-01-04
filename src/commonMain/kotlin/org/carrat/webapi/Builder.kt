package org.carrat.webapi

/**
 * Creates a new element which can be configured via a function
 */
public fun Document.createElement(name: String, init: Element.() -> Unit = {}): Element {
    val elem = createElement(name)
    elem.init()
    return elem
}

/**
 * Creates a new element to an element which has an owner Document which can be configured via a function
 */
public fun Element.createElement(name: String, doc: Document? = null, init: Element.() -> Unit = {}): Element {
    val elem = ownerDocument(doc).createElement(name)
    elem.init()
    return elem
}

/**
 * Adds a newly created element which can be configured via a function
 */
public fun Document.addElement(name: String, init: Element.() -> Unit = {}): Element {
    val child = createElement(name, init)
    this.appendChild(child)
    return child
}

/**
 * Adds a newly created element to an element which has an owner Document which can be configured via a function
 */
public fun Element.addElement(name: String, doc: Document? = null, init: Element.() -> Unit = {}): Element {
    val child = createElement(name, doc, init)
    this.appendChild(child)
    return child
}

