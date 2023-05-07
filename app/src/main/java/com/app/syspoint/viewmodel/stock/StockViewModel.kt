package com.app.syspoint.viewmodel.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.syspoint.models.sealed.StockViewState
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao
import com.app.syspoint.repository.objectBox.entities.StockBox

class StockViewModel: ViewModel() {

    val stockViewState = MutableLiveData<StockViewState>()
    private val stockData = MutableLiveData<List<StockBox?>>()

    fun finishStock() {
        if (!stockData.value.isNullOrEmpty()) {
            stockViewState.postValue(StockViewState.ConfirmStockState)
        } else {
            stockViewState.postValue(StockViewState.EmptyStockState)
        }
    }

    fun hanldeCloseStock() {
        if (stockData.value.isNullOrEmpty()) {
            stockViewState.postValue(StockViewState.CannotCloseStockState)
        } else {
            stockViewState.postValue(StockViewState.CloseCurrentStockState)
        }
    }

    fun closeStock() {
        val mList = StockDao().list() as List<StockBox>
        stockData.value = mList

        for (item in mList) {
            val productDao = ProductDao()
            val productoBean = productDao.getProductoByArticulo(item.articulo!!.target.articulo)
            if (productoBean != null) {
                //Actualiza la existencia del articulo
                productoBean.existencia = 0
                productDao.insert(productoBean)
            }
        }

        val stockDao = StockDao()
        stockDao.clear()

        val historialDao = StockHistoryDao()
        historialDao.clear()

        stockViewState.postValue(StockViewState.ClosedStockState(mList))
    }

    fun refreshStock() {
        val mList = StockDao().list() as List<StockBox>
        stockData.value = mList
        stockViewState.value = StockViewState.RefreshStockState(mList)
    }

    fun setUpStock() {
        val mList = StockDao().list() as List<StockBox>
        stockData.value = mList
        stockViewState.postValue(StockViewState.SetUpStockState(mList))
    }

    fun setUpPrinter() {

    }
}