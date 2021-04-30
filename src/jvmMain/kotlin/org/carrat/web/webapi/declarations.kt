package org.carrat.web.webapi

public actual val window: Window
    get() = jsOnly()

public actual val document: Document
    get() = jsOnly()

public actual val localStorage: Storage
    get() = jsOnly()

public actual val sessionStorage: Storage
    get() = jsOnly()
