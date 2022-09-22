package com.app.syspoint.interactor.file

import com.app.syspoint.repository.request.RequestFile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class FileInteractorImp: FileInteractor() {

    override fun executePostFile(
        image: File,
        sellId: String,
        onPostFileListener: OnPostFileListener
    ) {
        super.executePostFile(image, sellId, onPostFileListener)
        GlobalScope.launch {
            RequestFile.saveFile(image, sellId, onPostFileListener)
        }
    }
}