package com.app.syspoint.repository.request.http

import com.app.syspoint.models.Data
import com.app.syspoint.models.ResponseVenta
import com.app.syspoint.models.json.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PointApi {

    // GET AL DATA RESONSE
    @GET("getAllData")
    fun getAllData(): Call<Data>

    @GET("getAllDataByDate")
    fun getAllDataByDate(): Call<Data>

    // Empleados
    @GET("getAllEmpleados")
    fun getAllEmpleados(): Call<EmployeeJson>

    @GET("getEmpleadoByID")
    fun getEmpleadoByID(@Query("identificador") identificador: String?): Call<EmployeeJson>

    @POST("saveEmpleado")
    fun sendEmpleado(@Body param: EmployeeJson?): Call<EmployeeJson>

    // PRODUCTOS
    @GET("getAllProductos")
    fun getAllProductos(): Call<ProductJson>

    @POST("getProductoByID")
    fun getProductoByID(@Query("articulo") articulo: String?): Call<EmployeeJson>

    @POST("saveProducto")
    fun sendProducto(@Body param: ProductJson?): Call<ProductJson>

    // CLIENTES
    @GET("getAllClientes")
    fun getAllClientes(): Call<ClientJson>

    @POST("getClienteByID")
    fun getClienteByID(@Query("cuenta") cuenta: String?): Call<ClientJson>

    @POST("saveCliente")
    fun sendCliente(@Body param: ClientJson?): Call<ClientJson>

    // ROLES
    @GET("getAllRols")
    fun getAllRols(): Call<RolJson>

    @POST("saveRol")
    fun saveRoles(@Body param: RolJson?): Call<RolJson>

    // PRECIOS ESPECIALES
    @POST("savePrice")
    fun sendPrecios(@Body param: SpecialPriceJson?): Call<SpecialPriceJson>

    @GET("getAllPrices")
    fun getPricesEspecial(): Call<SpecialPriceJson>

    @POST("getPricesByDate")
    fun getPreciosByDate(@Body paramt: RequestPrices?): Call<SpecialPriceJson>

    @POST("getPricesByClient")
    fun getPreciosByClient(@Body paramt: RequestClients?): Call<SpecialPriceJson>

    // VISITAS
    @POST("saveVisita")
    fun sendVisita(@Body param: VisitJson?): Call<VisitJson>

    //COBRANZA
    @POST("saveCobranza")
    fun sendCobranza(@Body param: PaymentJson?): Call<PaymentJson>

    @POST("updateCobranza")
    fun updateCobranza(@Body param: PaymentJson?): Call<PaymentJson>

    @GET("getAllCobranza")
    fun getCobranza(): Call<PaymentJson>

    @POST("getAllByCliente")
    fun getCobranzaByCliente(@Body paramt: RequestCobranza?): Call<PaymentJson>

    //FILE
    @Multipart
    @POST("loadImage")
    fun postFile(
        @Part("cobranza") cobranza: RequestBody?,
        @Part imagen: MultipartBody.Part?
    ): Call<ResponseVenta>
}