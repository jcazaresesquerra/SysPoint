package com.app.syspoint.viewmodel.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.syspoint.models.sealed.StockViewState

class StockViewModel: ViewModel() {

    val stockViewState = MutableLiveData<StockViewState>()


}