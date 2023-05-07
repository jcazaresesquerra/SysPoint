package com.app.syspoint.viewmodel.printer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.syspoint.models.sealed.ConfigurePrinterViewState
import com.app.syspoint.repository.objectBox.dao.PrinterDao
import com.app.syspoint.repository.objectBox.entities.PrinterBox

class ConfigurePrinterViewModel: ViewModel() {

    val configurePrinterViewState = MutableLiveData<ConfigurePrinterViewState>()

    fun configurePrinter(deviceAddress: String, deviceInfo: String) {
        val printerBean = PrinterBox()
        val printerDao = PrinterDao()
        printerDao.clear()
        printerBean.id = java.lang.Long.valueOf(1)
        printerBean.address = deviceAddress
        printerBean.name = deviceInfo
        printerBean.idPrinter = java.lang.Long.valueOf(1)
        printerDao.insert(printerBean)

        configurePrinterViewState.postValue(ConfigurePrinterViewState.PrinterConfigured)
    }
}