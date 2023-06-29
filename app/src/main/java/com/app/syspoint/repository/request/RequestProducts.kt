package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.product.GetProductInteractor
import com.app.syspoint.models.Product
import com.app.syspoint.models.json.BaseBodyJson
import com.app.syspoint.models.json.ProductJson
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.repository.objectBox.entities.ProductBox
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class RequestProducts {
    companion object: BaseRequest() {

        fun requestProducts(): Call<ProductJson> {
            val employee = getEmployee()
            val baseBodyJson = BaseBodyJson(clientId = employee?.clientId?:"tenet")
            return ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllProductos(baseBodyJson)
        }

        fun requestProducts(onGetProductsListener: GetProductInteractor.OnGetProductsListener): Call<ProductJson> {
            val employee = getEmployee()
            val baseBodyJson = BaseBodyJson(clientId = employee?.clientId?:"tenet")
            val getProducts = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllProductos(baseBodyJson)

            getProducts.enqueue(object: Callback<ProductJson> {
                override fun onResponse(call: Call<ProductJson>, response: Response<ProductJson>) {
                    if (response.isSuccessful) {
                        val products = arrayListOf<ProductBox>()
                        val productDao = ProductDao()
                        response.body()!!.products!!.map {items ->
                            val productBean = productDao.getProductoByArticulo(items!!.articulo)
                            if (productBean == null) {
                                //Creamos el producto
                                val producto = ProductBox()
                                val dao = ProductDao()
                                producto.articulo = items.articulo
                                producto.descripcion = items.descripcion
                                producto.status = items.status
                                producto.precio = items.precio
                                producto.iva = items.iva
                                producto.codigo_barras = items.codigoBarras
                                producto.path_img = items.pathImage
                                producto.updatedAt = items.updatedAt
                                dao.insert(producto)
                                products.add(producto)
                            } else {
                                val update = if (!productBean.updatedAt.isNullOrEmpty() && !items.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = formatter.parse(items.updatedAt)
                                    val dateBean = formatter.parse(productBean.updatedAt)
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    productBean.articulo = items.articulo
                                    productBean.descripcion = items.descripcion
                                    productBean.status = items.status
                                    productBean.precio = items.precio
                                    productBean.iva = items.iva
                                    productBean.codigo_barras = items.codigoBarras
                                    productBean.path_img = items.pathImage
                                    productBean.updatedAt = items.updatedAt
                                    productDao.insert(productBean)
                                }
                                products.add(productBean)
                            }
                        }
                        onGetProductsListener.onGetProductsSuccess(products)
                    } else {
                        onGetProductsListener.onGetProductsError()
                    }
                }

                override fun onFailure(call: Call<ProductJson>, t: Throwable) {
                    onGetProductsListener.onGetProductsError()
                }

            })
            return getProducts
        }

        fun requestProductById(product: String, onGetProductByIdListener: GetProductInteractor.OnGetProductByIdListener) {

        }

        fun saveProducts(productList: List<Product>, onSaveProductsListener: GetProductInteractor.OnSaveProductsListener) {
            val employee = getEmployee()
            val productJson = ProductJson()
            productJson.products = productList
            productJson.clientId = employee?.clientId?:"tenet"
            val json = Gson().toJson(productJson)
            Log.d("SinProductos", json)

            val saveProducts = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).sendProducto(productJson)

            saveProducts.enqueue(object: Callback<ProductJson> {
                override fun onResponse(call: Call<ProductJson>, response: Response<ProductJson>) {
                    if (response.isSuccessful) {
                        onSaveProductsListener.onSaveProductsSuccess()
                    } else {
                        val error = response.errorBody()!!.string()
                        onSaveProductsListener.onSaveProductsError()
                    }
                }

                override fun onFailure(call: Call<ProductJson>, t: Throwable) {
                    onSaveProductsListener.onSaveProductsError()
                }
            })

        }
    }
}