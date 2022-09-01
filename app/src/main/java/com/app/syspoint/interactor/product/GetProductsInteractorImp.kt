package com.app.syspoint.interactor.product

import com.app.syspoint.repository.request.RequestProducts
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetProductsInteractorImp: GetProductInteractor() {

    override fun executeGetProducts(onGetProductsListener: OnGetProductsListener) {
        super.executeGetProducts(onGetProductsListener)
        GlobalScope.launch {
            RequestProducts.requestProducts(onGetProductsListener)
        }
    }
}