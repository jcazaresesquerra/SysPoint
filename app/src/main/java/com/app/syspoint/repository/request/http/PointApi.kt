package com.app.syspoint.repository.request.http

import com.app.syspoint.models.Data
import com.app.syspoint.models.RequestChargeByRute
import com.app.syspoint.models.RequestClientsByRute
import com.app.syspoint.models.ResponseVenta
import com.app.syspoint.models.json.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PointApi {

    // GET AL DATA RESPONSE
    @POST("getAllData")
    fun getAllDataV2(@Body baseBodyJson: BaseBodyJson?): Call<Data>

    @POST("getAllDataByDate")
    fun getAllDataByDate(@Body baseBodyJson: BaseBodyJson?): Call<Data>

    // Empleados
    @POST("getAllEmpleados")
    fun getAllEmpleados(@Body baseBodyJson: BaseBodyJson?): Call<EmployeeJson>

    // Unique GET method
    @GET("getAllEmployees")
    fun getAllEmployees(): Call<EmployeeJson>

    @GET("getEmpleadoByID")
    fun getEmpleadoByID(@Query("identificador") identificador: String?): Call<EmployeeJson>

    @POST("saveEmpleado")
    fun sendEmpleado(@Body param: EmployeeJson?): Call<EmployeeJson>

    // PRODUCTOS
    @POST("getAllProductos")
    fun getAllProductos(@Body baseBodyJson: BaseBodyJson?): Call<ProductJson>

    @POST("getProductoByID")
    fun getProductoByID(@Query("articulo") articulo: String?): Call<EmployeeJson>

    @POST("saveProducto")
    fun sendProducto(@Body param: ProductJson?): Call<ProductJson>

    // CLIENTES
    @GET("getAllClientes")
    fun getAllClientes(): Call<ClientJson>

    // CLIENTES
    @POST("getLasClientAccount")
    fun getLastClient(@Body baseBodyJson: BaseBodyJson?): Call<ClientJson>

    // CLIENTES
    @POST("getAllClientesByRute")
    fun getAllClientesByRute(@Body clientsByRute: RequestClientsByRute?): Call<ClientJson>

    // CLIENTES
    @POST("getAllClientsAndLastSellByRute")
    fun getAllClientsAndLastSellByRute(@Body clientsByRute: RequestClientsByRute?): Call<ClientJson>

    @POST("getClienteByID")
    fun getClienteByID(@Body clientByIdBodyJson: ClientByIdBodyJson): Call<ClientJson>

    @POST("saveCliente")
    fun sendCliente(@Body param: ClientJson?): Call<ClientJson>

    @POST("findClient")
    fun findClient(@Body findClientBodyJson: FindClientBodyJson): Call<ClientJson>

    @POST("getClientInfo")
    fun getClientInfo(@Body clientInfoBodyJson: ClientInfoBodyJson): Call<Data>

    // ROLES
    @POST("getAllRols")
    fun getAllRols(@Body baseBodyJson: BaseBodyJson?): Call<RolJson>

    @POST("saveRol")
    fun saveRoles(@Body param: RolJson?): Call<RolJson>

    // PRECIOS ESPECIALES
    @POST("savePrice")
    fun sendPrecios(@Body param: SpecialPriceJson?): Call<SpecialPriceJson>

    @POST("getAllPrices")
    fun getPricesEspecial(@Body baseBodyJson: BaseBodyJson?): Call<SpecialPriceJson>

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

    @POST("getAllCobranza")
    fun getCobranza(@Body baseBodyJson: BaseBodyJson?): Call<PaymentJson>

    @POST("getAllCobranzaByEmployee")
    fun getAllCobranzaByEmployee(@Body chargeByRute: RequestChargeByRute?): Call<PaymentJson>

    @POST("getAllByCliente")
    fun getCobranzaByCliente(@Body paramt: RequestCobranza?): Call<PaymentJson>

    //FILE
    @Multipart
    @POST("loadImage")
    fun postFile(
        @Part("cobranza") cobranza: RequestBody?,
        @Part("clientId") clientId: RequestBody?,
        @Part imagen: MultipartBody.Part?
    ): Call<ResponseVenta>

    @POST("getToken")
    fun getToken(@Body param: RequestTokenBody?): Call<TokenJson>

}