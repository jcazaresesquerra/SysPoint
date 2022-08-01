package com.app.syspoint;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.app.syspoint.db.bean.ClientesRutaBean;
import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.NetworkStateTask;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ClientesRutaBean> mData;
    public static String apikey;
    private NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;
    public static boolean isItemRuta = false;
    Toolbar toolbar;

  @Override
 protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);

     Toolbar toolbar = findViewById(R.id.toolbar);
     setSupportActionBar(toolbar);

     DrawerLayout drawer = findViewById(R.id.drawer_layout);
     NavigationView navigationView = findViewById(R.id.nav_view);

     mAppBarConfiguration = new AppBarConfiguration.Builder(
             R.id.nav_home,
             R.id.nav_ruta,
             R.id.nav_empleado,
             R.id.nav_producto,
             R.id.nav_cliente,
             R.id.nav_historial)
             .setDrawerLayout(drawer)
             .build();

     NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

      navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
          @Override
          public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

              ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
              progressDialog.setMessage("Espere un momento");
              progressDialog.setCancelable(false);
              progressDialog.show();
              new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                  progressDialog.dismiss();
                  if (!connected) showDialogNotInternet();
              }, MainActivity.this).execute(), 100);

              if (destination.getId() == R.id.nav_ruta){
                  Constants.solictaRuta = true;
              }

              if (destination.getId() == R.id.nav_home){
                  Constants.solictaRuta = false;
              }

          }
      });

     NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
     NavigationUI.setupWithNavController(navigationView, navController);
     apikey = getString(R.string.google_maps_key);
 }
//
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.main, menu);
       return true;
   }
//
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void showDialogNotInternet() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.no_internet_dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}