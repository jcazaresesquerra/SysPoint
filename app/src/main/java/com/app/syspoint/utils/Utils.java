package com.app.syspoint.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.app.syspoint.BuildConfig;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.SessionDao;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.SessionBox;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    //Contiene la pila de actividades anteriores
    protected static List<Activity> listaActividades = new ArrayList<>();

    static public String FDinero(final double cantidad){
        //Establece el símbolo separador
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        //Devuelve ya con formato
        return new DecimalFormat("$##,##0.00", otherSymbols).format(cantidad);
    }

    static public String formatMoneyMX(final double cantidad){

        //Establece el símbolo separador
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        //Devuelve ya con formato
        return new DecimalFormat("##,##0.00", otherSymbols).format(cantidad);
    }

    public static String getHoraActual(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final String hora = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return  hora;
    }

    public  static  String getFechaRandom(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return  fecha;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Convierte un array de strings en una lista de strings
    public static List<String> convertArrayStringListString(final String[] arrayString){

        //Contiene la lista
        List<String> lista = new ArrayList<>();

        //Recorre el array de strings
        Collections.addAll(lista, arrayString);

        //Devuelve el resultado
        return lista;
    }

    public static String fechaActual(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return  fecha;
    }

    public static Date getCurrentDayHMS(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);

        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        try {
            return format.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date getDataFromString(String date){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String fechaActualHMS(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return  fecha;
    }

    public static Date fechaActualHMS_(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        try {
            return format.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String fechaActualHMSStartDay(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return fecha + " 00:00:00";
    }

    public static String fechaActualHMSEndDay(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return fecha + " 23:59:59";
    }

    public static Date fechaActualHMSStartDay_(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat formatHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String fecha = format.format(calendarFechaFin.getTime());
        String stringFecha =  fecha + " 00:00:00";
        TimeZone.setDefault(backup);
        try {
            return formatHMS.parse(stringFecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date fechaActualHMSEndDay_(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat formatHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String fecha = format.format(calendarFechaFin.getTime());
        String stringFecha =  fecha + " 23:59:59";
        TimeZone.setDefault(backup);
        try {
            return formatHMS.parse(stringFecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String fechaActualPicker(){
        TimeZone backup = TimeZone.getDefault();
        String timezoneS = "GMT-7";
        TimeZone tz = TimeZone.getTimeZone(timezoneS);
        TimeZone.setDefault(tz);
        final Calendar calendarFechaFin = Calendar.getInstance(tz);
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        final String fecha = format.format(calendarFechaFin.getTime());
        TimeZone.setDefault(backup);
        return  fecha;
    }


    //Método para agregar una actividad a la pila
     public static void addActivity2Stack(final Activity activity){

        //Si no existe la actividad entonces agregala
        if(!Utils.listaActividades.contains(activity))
            Utils.listaActividades.add(activity);
    }

    //Termina todas las actividades en la pila y vacia memoria
    public static void finishActivitiesFromStack(){

        for(Activity activity: Utils.listaActividades)
            activity.finish();

        Utils.listaActividades.clear();
    }

    private static final String FILE_NAME_PATTERN = "yyyyMMdd_HHmmss";

    public static String formatDateForFileName(Date date) {
        SimpleDateFormat sdf
                = new SimpleDateFormat(FILE_NAME_PATTERN, Locale.getDefault());
        return sdf.format(date);
    }

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }
        return b[3];
    }

    public static String getUpdateURL(String baseUpdateUrl, String versionToDownload) {
        String flavor = BuildConfig.FLAVOR;
        String buildType = BuildConfig.BUILD_TYPE;
        String fileName = flavor + "_" + buildType + "_" + versionToDownload + ".apk";
        String url = baseUpdateUrl + flavor + "/" + buildType + "/" + fileName;
        return url;
    }

    private EmployeeBox getEmployee() {
        EmployeeBox vendedoresBean = AppBundle.getUserBox();
        if (vendedoresBean == null) {
            SessionBox sessionBox = new SessionDao().getUserSession();
            if (sessionBox != null) {
                vendedoresBean = new EmployeeDao().getEmployeeByID(sessionBox.getEmpleadoId());
            } else {
                vendedoresBean = new CacheInteractor().getSeller();
            }
        }
        return vendedoresBean;
    }

}
