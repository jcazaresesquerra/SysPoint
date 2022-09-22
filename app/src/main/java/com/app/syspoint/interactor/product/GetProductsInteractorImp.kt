package com.app.syspoint.interactor.product

import com.app.syspoint.models.Product
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

    override fun executeGetProductById(product: String, onGetProductByIdListener: OnGetProductByIdListener) {
        super.executeGetProductById(product, onGetProductByIdListener)
        GlobalScope.launch {
            RequestProducts.requestProductById(product, onGetProductByIdListener)
        }
    }

    override fun executeSaveProducts(productList: List<Product>, onSaveProductsListener: OnSaveProductsListener) {
        super.executeSaveProducts(productList, onSaveProductsListener)
        GlobalScope.launch {
            RequestProducts.saveProducts(productList, onSaveProductsListener)
        }
    }
}