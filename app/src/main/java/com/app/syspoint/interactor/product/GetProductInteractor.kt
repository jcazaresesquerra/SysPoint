package com.app.syspoint.interactor.product

import com.app.syspoint.repository.database.bean.ProductoBean

abstract class GetProductInteractor {

    interface OnGetProductsListener {
        fun onGetProductsSuccess(products: List<ProductoBean?>)
        fun onGetProductsError()
    }

    open fun executeGetProducts(onGetProductsListener: OnGetProductsListener) {}
}