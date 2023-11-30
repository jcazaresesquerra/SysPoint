package com.app.syspoint.models.sealed

sealed class PrecaptureViewState {
    data class PrecaptureFinished(var params: HashMap<String, String>): PrecaptureViewState()
    object SaveVisitSuccessState: PrecaptureViewState()
    object SaveVisitErrorState: PrecaptureViewState()
    object SaveClientSuccessState: PrecaptureViewState()
    object SaveClientErrorState: PrecaptureViewState()
}