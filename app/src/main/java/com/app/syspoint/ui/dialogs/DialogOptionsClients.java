package com.app.syspoint.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.syspoint.R;

public class DialogOptionsClients extends Dialog {

    private final static int CALL_CLIENT = 1;
    private final static int NEW_SALE = 2;
    private final static int GO_MAPS = 3;
    private boolean call_client;
    private boolean new_sale;
    private boolean go_maos;
    private ImageButton buttonCall, buttonSale;
    private ImageButton buttonClose;
    private ImageButton buttonGoMaps;
    private Runnable runnableCall, runnableSale, runnableGoMaps;

    public DialogOptionsClients(@NonNull Context context) {
        super(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_options_maps);
        this.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);

        buttonClose = findViewById(R.id.closeButtonDialog);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonCall = findViewById(R.id.img_button_llamar);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runnableCall != null){
                    runnableCall.run();
                }
            }
        });
        buttonGoMaps = findViewById(R.id.btn_button_ir);
        buttonGoMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (runnableGoMaps != null){
                    runnableGoMaps.run();
                }
            }
        });
        buttonSale = findViewById(R.id.img_button_venta);
        buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runnableSale != null){
                    runnableSale.run();
                }
            }
        });

    }

    public void setTitleMessage(String message){
        TextView tvMessage = this.findViewById(R.id.tv_direccion_cliente);
        tvMessage.setMovementMethod(new ScrollingMovementMethod());
        tvMessage.setText(message);
    }

    public void setCallClient(boolean call){
        this.call_client  = call;
    }

    public void setNewSale(boolean sale){
        this.new_sale = sale;
    }

    public void setGoMaps(boolean gomaps){
        this.go_maos = gomaps;
    }


    public void setRunnableCall(Runnable runnableCall) {
        this.runnableCall = runnableCall;
    }

    public void  setRunnableSale(Runnable runnableSale){
        this.runnableSale = runnableSale;
    }

    public void setRunnableGoMaps(Runnable runnableGoMaps){
        this.runnableGoMaps = runnableGoMaps;
    }

    @Override
    public void show() {
        super.show();
    }
}
