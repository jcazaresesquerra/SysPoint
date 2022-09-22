package com.app.syspoint.models.sealed

sealed class ConfigurePrinterViewState {
    object PrinterConfigured: ConfigurePrinterViewState()
}