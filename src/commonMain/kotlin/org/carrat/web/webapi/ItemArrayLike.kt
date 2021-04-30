package org.carrat.web.webapi

public expect interface ItemArrayLike<out T> {
    public val length: Int
    public fun item(index: Int): T?
}

/**
 * Returns the view of this `ItemArrayLike<T>` collection as `List<T>`
 */
public expect fun <T> ItemArrayLike<T>.asList(): List<T>
