package com.app.syspoint.models.sealed

import com.app.syspoint.repository.objectBox.entities.PrinterBox
import com.app.syspoint.repository.objectBox.entities.StockBox

sealed class StockViewState {
    data class SetUpStockState(val data: List<StockBox?>): StockViewState()
    data class SetUpPrinterState(val data: List<PrinterBox?>): StockViewState()
    data class RefreshStockState(val data: List<StockBox?>): StockViewState()
    object ConfirmStockState: StockViewState()
    object EmptyStockState: StockViewState()
    object CannotCloseStockState: StockViewState()
    object CloseCurrentStockState: StockViewState()
    data class ClosedStockState(val data: List<StockBox?>): StockViewState()

}