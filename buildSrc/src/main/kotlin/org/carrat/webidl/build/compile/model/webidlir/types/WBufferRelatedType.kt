package org.carrat.webidl.build.compile.model.webidlir.types

import org.carrat.webidl.build.compile.model.ir.type.IrDynamic
import org.carrat.webidl.build.compile.model.ir.type.TypeExpression

sealed class WBufferRelatedType : WType() {
    object ArrayBuffer : WBufferRelatedType()
    object DataView : WBufferRelatedType()
    object Int8Array : WBufferRelatedType()
    object Int16Array : WBufferRelatedType()
    object Int32Array : WBufferRelatedType()
    object Uint8Array : WBufferRelatedType()
    object Uint16Array : WBufferRelatedType()
    object Uint32Array : WBufferRelatedType()
    object Uint8ClampedArray : WBufferRelatedType()
    object Float32Array : WBufferRelatedType()
    object Float64Array : WBufferRelatedType()

    override fun toIr(): TypeExpression {
        return IrDynamic //TODO
    }
}
