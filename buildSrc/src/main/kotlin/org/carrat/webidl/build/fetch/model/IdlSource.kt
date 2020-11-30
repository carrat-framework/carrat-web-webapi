package org.carrat.webidl.build.fetch.model

import kotlinx.serialization.Serializable

@Serializable
data class IdlSource(
    val url : String,
    val title : String,
    var deprecated: Boolean? = null,
    val local: Boolean? = null
)