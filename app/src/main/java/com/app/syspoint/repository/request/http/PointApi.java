package com.app.syspoint.repository.request.http;

import com.app.syspoint.models.Data;
import com.app.syspoint.models.ResponseVenta;
import com.app.syspoint.models.json.ClientJson;
import com.app.syspoint.models.json.EmployeeJson;
import com.app.syspoint.models.json.PaymentJson;
import com.app.syspoint.models.json.ProductJson;
import com.app.syspoint.models.json.RequestClients;
import com.app.syspoint.models.json.RequestCobranza;
import com.app.syspoint.models.json.RequestPrices;
import com.app.syspoint.models.json.RolJson;
import com.app.syspoint.models.json.SpecialPriceJson;
import com.app.syspoint.models.json.VisitJson;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PointApi {


    //TODO GET AL DATA RESONSE
    @GET("getAllData")
    Call<Data> getAllData();

    @GET("getAllDataByDate")
    Call<Data> getAllDataByDate();

    //Todo Empleados
    @GET("getAllEmpleados")
    Call<EmployeeJson> getAllEmpleados();

    @GET("getEmpleadoByID")
    Call<EmployeeJson> getEmpleadoByID(@Query("identificador") String identificador);

    @POST("saveEmpleado")
    Call<EmployeeJson> sendEmpleado(@Body EmployeeJson param);

    //TODO PRODUCTOS
    @GET("getAllProductos")
    Call<ProductJson> getAllProductos();

    @POST("getProductoByID")
    Call<EmployeeJson> getProductoByID(@Query("articulo") String articulo);

    @POST("saveProducto")
    Call<ProductJson> sendProducto(@Body ProductJson param);

    //TODO CLIENTES
    @GET("getAllClientes")
    Call<ClientJson> getAllClientes();

    @POST("getClienteByID")
    Call<EmployeeJson> getClienteByID(@Query("cuenta") String cuenta);

    @POST("saveCliente")
    Call<ClientJson> sendCliente(@Body ClientJson param);
    
    //TODO ROLES
    @GET("getAllRols")
    Call<RolJson> getAllRols();

    @POST("saveRol")
    Call<RolJson> saveRoles(@Body RolJson param);

    //TODO PRECIOS ESPECIALES
    @POST("savePrice")
    Call<SpecialPriceJson> sendPrecios(@Body SpecialPriceJson param);

    @GET("getAllPrices")
    Call<SpecialPriceJson> getPricesEspecial();

    @POST("getPricesByDate")
    Call<SpecialPriceJson> getPreciosByDate(@Body RequestPrices paramt);

    @POST("getPricesByClient")
    Call<SpecialPriceJson>getPreciosByClient(@Body RequestClients paramt);

    @POST("saveVisita")
    Call<VisitJson> sendVisita(@Body VisitJson param);

    @POST("saveCobranza")
    Call<PaymentJson> sendCobranza(@Body PaymentJson param);

    @POST("updateCobranza")
    Call<PaymentJson> updateCobranza(@Body PaymentJson param);

    @GET("getAllCobranza")
    Call<PaymentJson> getCobranza();

   @POST("getAllByCliente")
   Call<PaymentJson> getCobranzaByCliente(@Body RequestCobranza paramt);


    @Multipart
    @POST("loadImage")
    Call<ResponseVenta> postFile(@Part("cobranza") RequestBody cobranza,
                                 @Part MultipartBody.Part imagen);



}
