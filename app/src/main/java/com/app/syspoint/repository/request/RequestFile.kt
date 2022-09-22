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
    companion object {
        fun saveFile(image: File, sellId: String, onPostFileListener: FileInteractor.OnPostFileListener) {
            var imagen: MultipartBody.Part? = null
            val cobranza: RequestBody = RequestBody.create(MultipartBody.FORM, sellId)
            if (image != null) {
                val imagenBody = RequestBody.create(MediaType.parse("image/jpg"), image)
                imagen = MultipartBody.Part.createFormData("imagen", image.name, imagenBody)
            }
            val sendFile = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).postFile(cobranza, imagen)

            sendFile.enqueue(object: Callback<ResponseVenta> {
                override fun onResponse(
                    call: Call<ResponseVenta>,
                    response: Response<ResponseVenta>
                ) {
                    if (response.isSuccessful) {
                        onPostFileListener.onPostFileSuccess()
                    } else {
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