package org.carrat.web.webapi

public actual fun <T> emptyObject(): T {
    return js("{}") as T
}
