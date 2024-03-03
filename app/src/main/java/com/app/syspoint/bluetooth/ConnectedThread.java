package com.app.syspoint.bluetooth;

import static com.app.syspoint.utils.Utils.intToByteArray;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.PrinterCommands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private InputStream mInputStream = null;
    private final Handler mHandler;
    private static OutputStream outputStream;

    //Constructor
    public ConnectedThread (BluetoothSocket socket, Handler handler){
        mmSocket  = socket;
        mHandler = handler;

        try{
            mInputStream = socket.getInputStream();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


        int bytes;

        while (true){
            try{
                bytes = mInputStream.available();
                if (bytes != 0){
                    byte[] buffer = new byte[1024];
                    SystemClock.sleep(100);
                    bytes = mInputStream.available();
                    bytes = mInputStream.read(buffer, 0 , bytes);
                    mHandler.obtainMessage(2, bytes, -1, buffer)
                            .sendToTarget();
                }
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
        }

    }

    //Call from main activity
    public void write(String input){

        Thread t = new Thread() {
            public void run() {
                try {
                    //printCustom(input, 1,1);
                    OutputStream os = mmSocket.getOutputStream();
                    //byte[] printFormat = new byte[]{0x1B,0x21,0x03};
                    //os.write(printFormat);

                    os.write(input.getBytes());

                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));

                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }


    public void printTicketVisit(String concepto, String tipo_inventario, String empleado, String fecha, String hora ){

       // mConnectedThread.printTicketVisita(concepto_visita_seleccioando, tipo_inventario_seleccionado, vendedoresBean.getNombre(), Utils.fechaActual(), Utils.getHoraActual());


        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputStream = mmSocket.getOutputStream();
            byte[] printFormat = new byte[]{0x1B,0x21,0x03};
            outputStream.write(printFormat);


            String header = headerTicket();
            String body = bodyText();

            printCustom(header,1,1);
            printCustom(Constants.DIVIDER, 4,0);
            printNewLine();
            printCustom("AVISO", 4,1);
            printCustom("DE VISITA", 4,1);
            printNewLine();
            printCustom(Constants.DIVIDER, 4,0);
            printCustom(body, 1, 1);
            printCustom(Constants.DIVIDER, 4,0);
            printNewLine();
            printCustom("Resultado de la Visita", 1,1);
            printCustom(concepto, 4,1);
            printCustom(bodyText2(empleado, fecha, hora),1, 1 );
            printNewLine();
            printCustom(Constants.DIVIDER, 4,0);
            printNewLine();
            printCustom("Llamenos", 1, 1);
            printCustom("(667) 744-9350", 4, 1);
            printNewLine();
            printNewLine();
            printNewLine();
            printNewLine();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String headerTicket(){
        return  "     AGUA POINT S.A. DE C.V.    " + Constants.NEW_LINE +
                "     Calz. Aeropuerto 4912 A    " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "           APO170818QR6         " + Constants.NEW_LINE +
                "          (667) 774-9350        " + Constants.NEW_LINE +
                "        info@aguapoint.com      " + Constants.NEW_LINE +
                "         www.aguapoint.com      " + Constants.NEW_LINE;
    }

    private String bodyText(){
        return  "Le informamos que estuvimos en" + Constants.NEW_LINE +
                " su domicilio pero no pudimos  " + Constants.NEW_LINE +
                "   atenderle. Estamos a sus      " + Constants.NEW_LINE +
                " ordenes por favor cominiquese " + Constants.NEW_LINE +
                "           con nosotros                  ";
    }
    private String bodyText2(String empleado, String fecha, String hora){
        return  "" + fecha + " - " + hora + "" + Constants.NEW_LINE +
                "Vendedor: " + empleado + Constants.NEW_LINE;
    }

    private String footerText(){
        return  "     AGUA POINT S.A. DE C.V.    " + Constants.NEW_LINE +
                "     Calz. Aeropuerto 4912 A    " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "           APO170818QR6         " + Constants.NEW_LINE +
                "          (667) 774-9350        " + Constants.NEW_LINE +
                "        info@aguapoint.com      " + Constants.NEW_LINE +
                "         www.aguapoint.com      " + Constants.NEW_LINE;
    }

    public void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x30}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
                case 4:
                    outputStream.write(bb4);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        try {
            mmSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
