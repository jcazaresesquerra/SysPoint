package com.app.syspoint.repository.request


import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class RawJsonConverterFactory(private val delegateFactory: Converter.Factory) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegateConverter =
            delegateFactory.responseBodyConverter(type, annotations, retrofit)

        return Converter<ResponseBody, Any> {
            val rawJson = it.string()
            // Do something with the raw JSON string before parsing
            delegateConverter?.convert(ResponseBody.create(it.contentType(), rawJson))
        }
    }
}