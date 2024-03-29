package org.carrat.web.webapi

public actual external interface ItemArrayLike<out T> {
    public actual val length: Int
    public actual fun item(index: Int): T?
}

public actual fun <T> ItemArrayLike<T>.asList(): List<T> = object : AbstractList<T>() {
    override val size: Int get() = this@asList.length

    override fun get(index: Int): T = when (index) {
        in 0..lastIndex -> this@asList.item(index).unsafeCast<T>()
        else -> throw IndexOutOfBoundsException("index $index is not in range [0..$lastIndex]")
    }
}
