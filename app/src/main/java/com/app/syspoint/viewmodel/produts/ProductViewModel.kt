package com.app.syspoint.viewmodel.produts

import androidx.lifecycle.MutableLiveData
import com.app.syspoint.interactor.product.GetProductInteractor
import com.app.syspoint.interactor.product.GetProductsInteractorImp
import com.app.syspoint.models.sealed.ProductViewState
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.repository.objectBox.dao.RolesDao
import com.app.syspoint.repository.objectBox.entities.ProductBox
import com.app.syspoint.viewmodel.BaseViewModel

class ProductViewModel: BaseViewModel() {

    val productViewState = MutableLiveData<ProductViewState>()

    fun setUpProducts() {
        val data = ProductDao().getActiveProducts()
        productViewState.postValue(ProductViewState.SetUpProductsState(data))
    }

    fun refreshProducts() {
        val data = ProductDao().getActiveProducts()
        productViewState.postValue(ProductViewState.RefreshProductsState(data))
    }

    fun checkConnectivity() {
        productViewState.postValue(ProductViewState.ShowProgressState)

        NetworkStateTask { connected: Boolean ->
            productViewState.value = ProductViewState.DismissProgressState

            if (!connected) {
                productViewState.value = ProductViewState.NetworkDisconnectedState
            } else {
                getProducts()
            }
        }.execute()
    }

    fun handleSelection(name: String?, productBean: ProductBox) {
        var identificador = ""

        //Obtiene el nombre del vendedor
        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        if (vendedoresBean != null) {
            identificador = vendedoresBean.identificador!!
        }
        val rolesDao = RolesDao()
        val rolesBean = rolesDao.getRolByEmpleado(identificador, "Productos")
        if (name == null || name.compareTo("Editar", ignoreCase = true) == 0) {
            if (rolesBean != null) {
                if (rolesBean.active) {
                    productViewState.postValue(ProductViewState.EditProductState(productBean.articulo!!))
                } else {
                    productViewState.postValue(ProductViewState.CanNotEditProductState)
                }
            }
        }
    }

    fun getProducts() {
        productViewState.value = ProductViewState.LoadingStartState
        GetProductsInteractorImp().executeGetProducts(object: GetProductInteractor.OnGetProductsListener {
            override fun onGetProductsSuccess(products: List<ProductBox?>) {
                productViewState.value = ProductViewState.LoadingFinishState
                productViewState.value = ProductViewState.GetProductsSuccess(products)
            }

            override fun onGetProductsError() {
                productViewState.value = ProductViewState.LoadingFinishState
                productViewState.value = ProductViewState.GetProductsError("")
            }
        })
    }
}