package com.app.syspoint.utils

class Constants {
    companion object {
        const val BASE_URL_TENET_PROD = "https://pruebas.tenet.host/public/api/"
        const val BASE_URL_TENET_QA = "https://apiqa.ocsistems.com/public/api/" // not used after migration of VPS
        const val permission_Read_data = 789
        var solictaRuta = false

        const val NEW_LINE = "\n"
        const val EMPTY_STRING = ""
        const val DIVIDER = "****************"
        const val DON_AQUI_FLAVOR_TAG = "donaqui"
        const val SYSPOINT_FLAVOR_TAG = "syspoint"

        const val TENET_CLIENT_ID = "syspoint"
        const val DON_AQUI_CLIENT_ID = "don_aqui"
        const val NUTRIRICA_CLIENT_ID = "nutririca"
        const val PRUEBAS_CLIENT_ID = "pruebas"

        const val CONTADO = "Contado"

        //Contiene la línea
        const val line = "================================"

        const val EXTRA_DEVICE_ADDRESS = "device_address"

        const val REQUEST_PERMISSION_CALL = 992
        const val REQUEST_PERMISSION_LOCATION = 991
    }
}