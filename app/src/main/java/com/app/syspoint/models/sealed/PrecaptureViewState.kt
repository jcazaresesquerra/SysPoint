package com.app.syspoint.models.sealed

import kotlin.collections.HashMap

sealed class PrecaptureViewState {
    data class PrecaptureFinished(var params: HashMap<String, String>): PrecaptureViewState()
    object SaveVisitSuccessState: PrecaptureViewState()
    object SaveVisitErrorState: PrecaptureViewState()
    object SaveClientSuccessState: PrecaptureViewState()
    object SaveClientErrorState: PrecaptureViewState()
}