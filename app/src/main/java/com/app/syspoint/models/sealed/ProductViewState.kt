package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.ProductoBean

sealed class ProductViewState {
    data class SetUpProductsState(val data: List<ProductoBean?>): ProductViewState()
    data class RefreshProductsState(val data: List<ProductoBean?>): ProductViewState()
    object ShowProgressState: ProductViewState()
    object DismissProgressState: ProductViewState()
    object NetworkDisconnectedState: ProductViewState()
    data class EditProductState(val item: String): ProductViewState()
    object CanNotEditProductState: ProductViewState()
    object LoadingStartState: ProductViewState()
    object LoadingFinishState: ProductViewState()
    data class GetProductsSuccess(val products: List<ProductoBean?>): ProductViewState()
    data class GetProductsError(val error: String): ProductViewState()
}
