package org.carrat.webidl.build.compile.model.webidlir.types

sealed class BufferRelatedType : Type() {
    object ArrayBuffer : BufferRelatedType()
    object DataView : BufferRelatedType()
    object Int8Array : BufferRelatedType()
    object Int16Array : BufferRelatedType()
    object Int32Array : BufferRelatedType()
    object Uint8Array : BufferRelatedType()
    object Uint16Array : BufferRelatedType()
    object Uint32Array : BufferRelatedType()
    object Uint8ClampedArray : BufferRelatedType()
    object Float32Array : BufferRelatedType()
    object Float64Array : BufferRelatedType()
}