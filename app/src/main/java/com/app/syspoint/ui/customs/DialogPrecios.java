package com.app.syspoint.ui.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.app.syspoint.R;


public class DialogPrecios extends Dialog {


    private DialogoListener mDialogoListener;


    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogo_precios);

        editText = findViewById(R.id.edittext_precio_especial);

        Button button = findViewById(R.id.bt_aplicar_precio);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String precio = editText.getText().toString();
                mDialogoListener.ready(precio);
                //DialogPrecios.this.dismiss();
            }
        });
    }


    public DialogPrecios(@NonNull Context context,  DialogoListener mDialogoListener) {
        super(context);
        this.mDialogoListener = mDialogoListener;
    }

    public interface DialogoListener {
        public void ready(String precio);
        public void cancelled();

    }
}
