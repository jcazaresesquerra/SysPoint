package com.app.syspoint.interactor.file

import java.io.File

abstract class FileInteractor {

    interface OnPostFileListener {
        fun onPostFileSuccess()
        fun onPostFileError()
    }

    open fun executePostFile(image: File, sellId: String, onPostFileListener: OnPostFileListener) {}
}