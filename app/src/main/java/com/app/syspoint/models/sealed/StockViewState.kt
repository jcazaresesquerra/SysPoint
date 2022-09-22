package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.bean.PrinterBean

sealed class StockViewState {
    data class SetUpStockState(val data: List<InventarioBean?>): StockViewState()
    data class SetUpPrinterState(val data: List<PrinterBean?>): StockViewState()
    data class RefreshStockState(val data: List<InventarioBean?>): StockViewState()
    object ConfirmStockState: StockViewState()
    object EmptyStockState: StockViewState()
    object CannotCloseStockState: StockViewState()
    object CloseCurrentStockState: StockViewState()
    data class ClosedStockState(val data: List<InventarioBean?>): StockViewState()

}