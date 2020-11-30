/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.carrat.webapi

public actual open external class Promise<out T> actual constructor(executor: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {

    public actual open fun <S> then(onFulfilled: ((T) -> S)?): Promise<S>

    public actual open fun <S> then(onFulfilled: ((T) -> S)?, onRejected: ((Throwable) -> S)?): Promise<S>

    public actual open fun <S> catch(onRejected: (Throwable) -> S): Promise<S>

    public actual companion object {
        public actual fun <S> all(promise: Array<out Promise<S>>): Promise<Array<out S>>

        public actual fun <S> race(promise: Array<out Promise<S>>): Promise<S>

        public actual fun reject(e: Throwable): Promise<Nothing>

        public actual fun <S> resolve(e: S): Promise<S>
        public actual fun <S> resolve(e: Promise<S>): Promise<S>
    }
}
