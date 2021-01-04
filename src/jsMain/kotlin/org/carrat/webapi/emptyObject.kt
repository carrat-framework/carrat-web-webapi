package org.carrat.webapi

public actual fun <T> emptyObject() : T {
    return js("{}") as T
}
