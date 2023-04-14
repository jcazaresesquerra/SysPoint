package com.app.syspoint.models.sealed

import com.app.syspoint.repository.objectBox.entities.ProductBox

sealed class ProductViewState {
    data class SetUpProductsState(val data: List<ProductBox?>): ProductViewState()
    data class RefreshProductsState(val data: List<ProductBox?>): ProductViewState()
    object ShowProgressState: ProductViewState()
    object DismissProgressState: ProductViewState()
    object NetworkDisconnectedState: ProductViewState()
    data class EditProductState(val item: String): ProductViewState()
    object CanNotEditProductState: ProductViewState()
    object LoadingStartState: ProductViewState()
    object LoadingFinishState: ProductViewState()
    data class GetProductsSuccess(val products: List<ProductBox?>): ProductViewState()
    data class GetProductsError(val error: String): ProductViewState()
}
