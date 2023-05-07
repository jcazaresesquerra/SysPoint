package com.app.syspoint.repository.request.http;

import static com.app.syspoint.utils.Constants.BASE_URL_DONAQUI;
import static com.app.syspoint.utils.Constants.BASE_URL_SYSPOINT;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.syspoint.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.RequestBody;

public class Servicio {

    protected String host;
    protected String servicio;
    protected JSONObject jsonObject = new JSONObject();
    protected ResponseOnSuccess onSuccess;
    protected ResponseOnError onError;

    public Servicio (final String servicio){
        this.servicio = servicio;
        host = getBaseURL();
    }

    final public void postObject(){

        final String finalURL = this.host  + this.servicio;

        ANRequest req = AndroidNetworking.post(finalURL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build();
        Headers headers = req.getHeaders();
        RequestBody body = req.getRequestBody();

        req.getAsJSONObject(new JSONObjectRequestListener() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            if(response.getString("result").compareTo("error")==0){
                                if(onError!=null){
                                    try {
                                        final String error = response.getString("error");
                                        onError.onError(error);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else{
                                if(onSuccess!=null){
                                    try {
                                        onSuccess.onSuccessObject(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if(onError!=null){
                            onError.onError(error);
                        }
                    }
                });
    }

    public void setOnSuccess(ResponseOnSuccess onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnError(ResponseOnError onError) {
        this.onError = onError;
    }

    /*Clases para respusta*/
    public abstract static class ResponseOnError implements Runnable{

        @Override
        public void run() {

        }

        public abstract void onError(ANError error);

        public abstract void onError(String error);
    }
    public abstract static class ResponseOnSuccess implements Runnable{

        @Override
        public void run() {

        }

        public abstract void onSuccess(JSONArray response) throws JSONException;

        public abstract void onSuccessObject(JSONObject response) throws Exception;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private String getBaseURL() {
        if (BuildConfig.FLAVOR.equals("donaqui")) {
            return BASE_URL_DONAQUI;
        } else {
            return BASE_URL_SYSPOINT;
        }
    }
}
