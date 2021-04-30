package org.carrat.web.webapi

public actual open class Promise<out T> actual constructor(executor: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    init {
        jsOnly()
    }

    public actual open fun <S> then(onFulfilled: ((T) -> S)?): Promise<S> = jsOnly()

    public actual open fun <S> then(onFulfilled: ((T) -> S)?, onRejected: ((Throwable) -> S)?): Promise<S> = jsOnly()

    public actual open fun <S> catch(onRejected: (Throwable) -> S): Promise<S> = jsOnly()

    public actual companion object {
        public actual fun <S> all(promise: Array<out Promise<S>>): Promise<Array<out S>> = jsOnly()

        public actual fun <S> race(promise: Array<out Promise<S>>): Promise<S> = jsOnly()

        public actual fun reject(e: Throwable): Promise<Nothing> = jsOnly()

        public actual fun <S> resolve(e: S): Promise<S> = jsOnly()
        public actual fun <S> resolve(e: Promise<S>): Promise<S> = jsOnly()
    }
}