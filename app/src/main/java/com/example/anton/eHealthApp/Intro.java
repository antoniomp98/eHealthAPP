package com.example.anton.eHealthApp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class Intro extends AppCompatActivity {

    Button connect;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        /*
        FirebaseAnalytics.getInstance(this);

       Log.d("NO", "ASDFASDFASDF");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("token",token);
        Log.d("NO", "ASDFASDFASDF");
        */

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

    }

    protected void onStart() {
        super.onStart();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER))
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("La localización debe estar activada para el funcionamiento de esta aplicación")
                    .setCancelable(false)
                    .setPositiveButton("ACTIVAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    protected void onResume() {
        super.onResume();
        connect = findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intro.this, Activity_SearchUiHeartRateSampler.class);
                startActivity(i);
                finish();
            }
        });
    }
}