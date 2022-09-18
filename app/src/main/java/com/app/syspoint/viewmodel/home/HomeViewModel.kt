package com.app.syspoint.viewmodel.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.syspoint.models.sealed.HomeViewState

class HomeViewModel: ViewModel() {
    val homeViewState = MutableLiveData<HomeViewState>()


}