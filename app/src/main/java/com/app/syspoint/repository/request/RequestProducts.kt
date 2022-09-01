package com.app.syspoint.repository.request

import com.app.syspoint.interactor.product.GetProductInteractor
import com.app.syspoint.models.json.ProductJson
import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.repository.database.dao.ProductDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestProducts {
    companion object {

        fun requestProducts(onGetProductsListener: GetProductInteractor.OnGetProductsListener) {
            val getProducts = ApiServices.getClientRestrofit().create(PointApi::class.java).allProductos
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
                                producto.unidad_medida = items.unidadMedida
                                producto.clave_sat = items.claveSat
                                producto.unidad_sat = items.unidadSat
                                producto.precio = items.precio
                                producto.costo = items.costo
                                producto.iva = items.iva
                                producto.ieps = items.ieps
                                producto.prioridad = items.prioridad
                                producto.region = items.region
                                producto.codigo_alfa = items.codigoAlfa
                                producto.codigo_barras = items.codigoBarras
                                producto.path_img = items.pathImage
                                dao.insert(producto)
                                products.add(producto)
                            } else {
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
        }
    }
}