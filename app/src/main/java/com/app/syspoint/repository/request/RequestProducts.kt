package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.product.GetProductInteractor
import com.app.syspoint.models.Product
import com.app.syspoint.models.json.ProductJson
import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.repository.database.dao.ProductDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class RequestProducts {
    companion object {

        fun requestProducts(onGetProductsListener: GetProductInteractor.OnGetProductsListener): Call<ProductJson> {
            val getProducts = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllProductos()

            getProducts.enqueue(object: Callback<ProductJson> {
                override fun onResponse(call: Call<ProductJson>, response: Response<ProductJson>) {
                    if (response.isSuccessful) {
                        val products = arrayListOf<ProductoBean>()
                        val productDao = ProductDao()
                        for (items in response.body()!!.products!!) {

                            val productBean = productDao.getProductoByArticulo(items!!.articulo)
                            if (productBean == null) {
                                //Creamos el producto
                                val producto = ProductoBean()
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
                                    productDao.save(productBean)
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
            val productJson = ProductJson()
            productJson.products = productList
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