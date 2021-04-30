package org.carrat.web.webapi

public actual interface ItemArrayLike<out T> {
    public actual val length: Int
    public actual fun item(index: Int): T?
}

public actual fun <T> ItemArrayLike<T>.asList(): List<T> = jsOnly()
