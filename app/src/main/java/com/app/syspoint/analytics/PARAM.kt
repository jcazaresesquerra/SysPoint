package com.app.syspoint.analytics

enum class PARAM(val value: String) {
    BUTTON_FINISH_SELL_CLICK("click_boton_finaliza_venta"),
    BUTTON_EXIT_SELL_CLICK("click_boton_salir_venta"),
    BUTTON_CONFIRM_FINISH_SELL_CLICK("click_boton_confirmar__venta"),
    BUTTON_FINISH_VISIT_CLICK("click_boton_finaliza_visita"),
    BUTTON_ADD_PRODUCT_CLICK("click_boton_agregar_producto"),
    DELETE_PRODUCT_CLICK("click_eliminar_articulo"),
    DELETE_PRODUCT_SUCCESS_CLICK("articulo_eliminado"),
    ADD_PRODUCT_SUCCESS_CLICK("articulo_añadido"),
    BUTTON_SUBMIT_SCHEDULE("click_boton_confirmar_recordatorio")
}