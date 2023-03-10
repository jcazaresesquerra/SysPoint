package com.app.syspoint.models.sealed

import java.io.File

sealed class DownloadApkViewState {
    data class ApkOldVersion(val baseUpdateUrl: String, val versionToDownload: String): DownloadApkViewState()
    data class DownloadApkSuccess(val file: File, val versionToDownload: String): DownloadApkViewState()
    data class DownloadApkError(val versionToDownload: String): DownloadApkViewState()
}
