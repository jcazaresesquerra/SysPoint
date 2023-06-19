package com.app.syspoint.repository.request

import com.app.syspoint.interactor.file.FileInteractor
import com.app.syspoint.models.ResponseVenta
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RequestFile {
    companion object: BaseRequest() {
        fun saveFile(image: File, sellId: String, onPostFileListener: FileInteractor.OnPostFileListener) {
            val employee = getEmployee()
            var imagen: MultipartBody.Part? = null
            val cobranza: RequestBody = RequestBody.create(MultipartBody.FORM, sellId)
            val clientId: RequestBody = RequestBody.create(MultipartBody.FORM, employee?.clientId?:"tenet")
            if (image != null) {
                val imagenBody = RequestBody.create(MediaType.parse("image/jpg"), image)
                imagen = MultipartBody.Part.createFormData("imagen", image.name, imagenBody)
            }
            val sendFile = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).postFile(cobranza, clientId, imagen)

            sendFile.enqueue(object: Callback<ResponseVenta> {
                override fun onResponse(
                    call: Call<ResponseVenta>,
                    response: Response<ResponseVenta>
                ) {
                    if (response.isSuccessful) {
                        onPostFileListener.onPostFileSuccess()
                    } else {
                        val error = response.errorBody()!!.string()
                        onPostFileListener.onPostFileError()
                    }
                }

                override fun onFailure(call: Call<ResponseVenta>, t: Throwable) {
                    onPostFileListener.onPostFileError()
                }

            })

        }
    }
}