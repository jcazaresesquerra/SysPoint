package com.app.syspoint.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.syspoint.R;

public class Dialogo extends Dialog {

    //Para saber que botón presiono el usuario
    public final static int ACEPTAR = 1;
    public final static int CANCELAR = 2;

    //Contiene el activity
    private Activity activity;

    //Para saber si va a ser de aceptar
    private boolean aceptar;

    //Para saber si va a ser de cancelar
    private boolean cancelar;

    //Contiene los botones de aceptar y cancelar
    private Button buttonAceptar, buttonCancelar;

    //Para saber si presiono aceptar o cancelar
    private int respuesta;

    //Para saber si tiene que cerrar el dilogo en aceptar o cancelar
    private boolean OnAceptarDissmis, OnCancelarDissmis;

    //Contiene el runnable que tiene que hacer cuando se presione Aceptar y Cancelar
    private Runnable runnableAceptar, runnableCancelar;


    public Dialogo(Context context) {

        //Construye el padre
        super(context);

        //Esconde el action bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Establece el view
        this.setContentView(R.layout.dialog_basic_view);

        //No es cancelable
        this.setCancelable(false);

        //Establece las acciones de los botones
        this.buttonCancelar = this.findViewById(R.id.dialog_basic_view_button_cancelar);
        this.buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //La respuesta es cancelar
                respuesta = Dialogo.CANCELAR;

                //Si tiene que cerrar entonces
                if(OnCancelarDissmis)
                    dismiss();

                //Si tiene que correr algo entonces
                if(runnableCancelar!=null)
                    runnableCancelar.run();
            }
        });
        this.buttonAceptar = this.findViewById(R.id.dialog_basic_view_button_aceptar);
        this.buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Si tiene que cerrar entonces
                if (OnAceptarDissmis)
                    dismiss();

                //La respuesta es aceptar
                respuesta = Dialogo.ACEPTAR;

                //Si tiene que correr algo entonces
                if(runnableAceptar!=null)
                    runnableAceptar.run();
            }
        });
    }



    public void setMensaje(String mensaje) {

        //Establece el mensaje
        TextView textView = this.findViewById(R.id.dialog_basic_textview_mensaje);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(mensaje);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setAceptar(boolean aceptar) {
        this.aceptar = aceptar;
    }

    public void setCancelar(boolean cancelar) {
        this.cancelar = cancelar;
    }

    public void setOnAceptarDissmis(boolean onAceptarDissmis) {
        OnAceptarDissmis = onAceptarDissmis;
    }

    public void setOnCancelarDissmis(boolean onCancelarDissmis) {
        OnCancelarDissmis = onCancelarDissmis;
    }

    public void setRunnableAceptar(Runnable runnableAceptar) {
        this.runnableAceptar = runnableAceptar;
    }

    public void setRunnableCancelar(Runnable runnableCancelar) {
        this.runnableCancelar = runnableCancelar;
    }


    public int getRespuesta() {
        return respuesta;
    }


    //No es clonable
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void show() {

        //Muestra los dos botones
        this.buttonAceptar.setVisibility(View.VISIBLE);
        this.buttonCancelar.setVisibility(View.VISIBLE);

        //Valida que botón se debe de esconder
        if(!this.aceptar)
            this.buttonAceptar.setVisibility(View.GONE);
        if(!this.cancelar)
            this.buttonCancelar.setVisibility(View.GONE);

        //Construye al padre
        super.show();
    }

    //Establece el nombre del botón de cancelar
    final public void setNombreBotonCancelar(final String nombre){
        getButton(R.id.dialog_basic_view_button_cancelar).setText(nombre);
    }


    //Establece el nombre del botón de aceptar
    final public void setNombreBotonAceptar(final String nombre){
        getButton(R.id.dialog_basic_view_button_aceptar).setText(nombre);
    }


    //Obtiene el botón específicado
    private Button getButton(final int id){
        return (Button)this.findViewById(id);
    }

    public void setTitulo(String mensaje){
        //Establece el Titulo
        TextView textView = this.findViewById(R.id.title);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(mensaje);
    }
}
