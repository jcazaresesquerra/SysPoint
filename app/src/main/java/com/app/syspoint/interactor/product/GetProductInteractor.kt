package com.app.syspoint.interactor.product

import com.app.syspoint.models.Product
import com.app.syspoint.repository.database.bean.ProductoBean

abstract class GetProductInteractor {

    interface OnGetProductsListener {
        fun onGetProductsSuccess(products: List<ProductoBean?>)
        fun onGetProductsError()
    }

    interface OnGetProductByIdListener {
        fun onGetProductByIDSuccess(products: ProductoBean)
        fun onGetProductByIdError()
    }

    interface OnSaveProductsListener {
        fun onSaveProductsSuccess()
        fun onSaveProductsError()
    }

    open fun executeGetProducts(onGetProductsListener: OnGetProductsListener) {}
    open fun executeGetProductById(product: String, onGetProductByIdListener: OnGetProductByIdListener) {}
    open fun executeSaveProducts(products: List<Product>, onSaveProductsListener: OnSaveProductsListener) {}
}