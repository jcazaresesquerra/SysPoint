package com.app.syspoint.interactor.file

abstract class FileInteractor {

    interface OnPostFileListener {
        fun onPostFileSuccess()
        fun onPostFileError()
    }

    open fun executePostFile(onPostFileListener: OnPostFileListener) {}
}