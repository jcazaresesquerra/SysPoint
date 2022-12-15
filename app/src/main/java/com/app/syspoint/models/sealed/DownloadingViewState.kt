package com.app.syspoint.models.sealed

sealed class DownloadingViewState {
    object StartDownloadViewState: DownloadingViewState()
    object DownloadCompletedViewState: DownloadingViewState()
    object DownloadCancelledViewState: DownloadingViewState()
}
