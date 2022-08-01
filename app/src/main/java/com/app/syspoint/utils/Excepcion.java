package com.app.syspoint.utils;


import android.app.Activity;
import android.util.Log;

import com.app.syspoint.ui.dialogs.Dialogo;

public class Excepcion extends Throwable {

    //Contiene el singleton
    private static Excepcion excepcion;

    //Contiene la excepción
    private static Exception exception;

    //Constructor privado
    private Excepcion()
    {
    }

    //Obtiene el singleton
    final public static Excepcion getSingleton(final Exception excepcion)
    {
        //Si no hay instancia entonces crea una
        if(Excepcion.excepcion == null)
            Excepcion.excepcion = new Excepcion();

        //Guarda la excepción
        Excepcion.exception = excepcion;

        //Devuelve la instancia única
        return Excepcion.excepcion;
    }


    //Procesa la excepción
    final public void procesaExcepcion(final Activity activity)
    {
        //Obtiene el mensaje
        String mensaje = Excepcion.exception.getMessage();
        if(mensaje==null)
            mensaje = "NullPointerException";

        Log.e("Error", "exception", Excepcion.exception);

        //Imprime la pila de llamadas
        Excepcion.exception.printStackTrace();

        //Mensajea
        Dialogo dialogo = new Dialogo(activity);
        dialogo.setAceptar(true);
        dialogo.setOnAceptarDissmis(true);
        dialogo.setMensaje("Error" + ": " + mensaje);

        if(activity!=null)
            dialogo.show();
    }
}
