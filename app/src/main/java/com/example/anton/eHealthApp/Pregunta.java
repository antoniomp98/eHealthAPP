package com.example.anton.eHealthApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Pregunta extends AppCompatActivity {

    TextView emergency_text;
    TextView cuenta_atras;
    TextView id_ambulancia;
    boolean conexion;
    int pid;
    int option = 0;
    double latitude = 0;
    double longitude = 0;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        pid = getIntent().getIntExtra("pid", 0);
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        Log.d("pid", String.valueOf(pid));
        emergency_text = findViewById(R.id.textView2);
        cuenta_atras = findViewById(R.id.textView3);
        Button botonSI = findViewById(R.id.button2);
        Button botonNO = findViewById(R.id.button3);

        emergency_text.setText("¿Está usted bien?");
/*
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        assert locMan != null;
        Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = location.getLatitude() * 10000000;
        longitude = location.getLongitude() * 10000000;
*/
        final CountDownTimer contador = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                cuenta_atras.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                cuenta_atras.setText("Emergency");
                fin();
            }
        }.start();


        botonSI.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                contador.cancel();
                cuenta_atras.setText("Alarma Cancelada");
                option = 0;
                PararTimer pararTimer = new PararTimer();
                pararTimer.start();
                try {
                    pararTimer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pararTimer.interrupt();
                fin();
            }
        });
        botonNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador.cancel();
                cuenta_atras.setText("Emergency");
                option = 1;
                PararTimer pararTimer = new PararTimer();
                pararTimer.start();
                try {
                    pararTimer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pararTimer.interrupt();
                fin();
            }
        });
    }

    private void fin() {
        Intent i = new Intent(Pregunta.this, Activity_SearchUiHeartRateSampler.class);
        startActivity(i);
        finish();
        finish();
    }

    public class PararTimer extends Thread {
        public void run() {
            try {
                String pidString = Integer.toString(pid);
                Log.d("option", String.valueOf(option));
                URL url = new URL("http://192.168.3.141/pararTimer.php?pid="+pidString+
                        "&option="+option+"&latitude="+latitude+"&longitude="+longitude);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                String responseMessage = urlConnection.getResponseMessage();

                String TAG = "Reply";
                Log.d(TAG, responseMessage + "         " + responseCode);

                InputStream in = urlConnection.getInputStream();
                String texto = getStringFromInputStream(in);
                Log.d(TAG, texto);

                conexion = true;
            }catch (Exception e) {
                conexion = false;
                System.out.println(e.getMessage());
            }
        }

        private String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();

        }
        /*boolean getConexion(){
            return conexion;
        }*/
    }


}
