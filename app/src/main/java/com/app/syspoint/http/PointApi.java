package com.app.syspoint.http;

import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.CobranzaJson;
import com.app.syspoint.json.EmpleadoJson;
import com.app.syspoint.json.PrecioEspecialJson;
import com.app.syspoint.json.ProductoJson;
import com.app.syspoint.json.RequestClients;
import com.app.syspoint.json.RequestCobranza;
import com.app.syspoint.json.RequestPrices;
import com.app.syspoint.json.RolsJson;
import com.app.syspoint.json.VisitaJson;

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
    Call<EmpleadoJson> getAllEmpleados();

    @GET("getEmpleadoByID")
    Call<EmpleadoJson> getEmpleadoByID(@Query("identificador") String identificador);

    @POST("saveEmpleado")
    Call<EmpleadoJson> sendEmpleado(@Body EmpleadoJson param);

    //TODO PRODUCTOS
    @GET("getAllProductos")
    Call<ProductoJson> getAllProductos();

    @POST("getProductoByID")
    Call<EmpleadoJson> getProductoByID(@Query("articulo") String articulo);

    @POST("saveProducto")
    Call<ProductoJson> sendProducto(@Body ProductoJson param);

    //TODO CLIENTES
    @GET("getAllClientes")
    Call<ClienteJson> getAllClientes();

    @POST("getClienteByID")
    Call<EmpleadoJson> getClienteByID(@Query("cuenta") String cuenta);

    @POST("saveCliente")
    Call<ClienteJson> sendCliente(@Body ClienteJson param);
    
    //TODO ROLES
    @GET("getAllRols")
    Call<RolsJson> getAllRols();

    @POST("saveRol")
    Call<RolsJson> saveRoles(@Body RolsJson param);

    //TODO PRECIOS ESPECIALES
    @POST("savePrice")
    Call<PrecioEspecialJson> sendPrecios(@Body PrecioEspecialJson param);

    @GET("getAllPrices")
    Call<PrecioEspecialJson> getPricesEspecial();

    @POST("getPricesByDate")
    Call<PrecioEspecialJson> getPreciosByDate(@Body RequestPrices paramt);

    @POST("getPricesByClient")
    Call<PrecioEspecialJson>getPreciosByClient(@Body RequestClients paramt);

    @POST("saveVisita")
    Call<VisitaJson> sendVisita(@Body VisitaJson param);

    @POST("saveCobranza")
    Call<CobranzaJson> sendCobranza(@Body CobranzaJson param);

    @POST("updateCobranza")
    Call<CobranzaJson> updateCobranza(@Body CobranzaJson param);

    @GET("getAllCobranza")
    Call<CobranzaJson> getCobranza();

   @POST("getAllByCliente")
   Call<CobranzaJson> getCobranzaByCliente(@Body RequestCobranza paramt);


    @Multipart
    @POST("loadImage")
    Call<ResponseVenta> postFile(@Part("cobranza") RequestBody cobranza,
                            @Part MultipartBody.Part imagen);



}
