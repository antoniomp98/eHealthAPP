package com.example.anton.httprequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONObject;


public class Pregunta extends AppCompatActivity {

    TextView emergency_text;
    TextView cuenta_atras;
    TextView id_ambulancia;
    String respuestaPHP;
    JSONObject array; //Json recibido
    PostJSON emergency;
    LocationManager locationManager;
    double latitude;
    double longitude;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        emergency_text = findViewById(R.id.textView2);
        cuenta_atras = findViewById(R.id.textView3);
        Button botonSI = findViewById(R.id.button2);
        Button botonNO = findViewById(R.id.button3);

        actualizarUbi();

        botonSI.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                cuenta_atras.setText("Alarma Cancelada");

            }});
        botonNO.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    onFinish();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

            //Se activa al finalizar la cuenta atrás o al pulsar un botón
            @SuppressLint("SetTextI18n")
            public void onFinish() throws InterruptedException {
                cuenta_atras.setText("Enviando señal de emergencia");
                    if(startRequestEmergency() == 1 ){
                        cuenta_atras.setText("Todo Correcto");
                    }
                    else if(startRequestEmergency() == 2 ) {
                        cuenta_atras.setText("ERROR AL CONECTAR CON EL SERVIDOR");
                    }
                    else if(startRequestEmergency() == 0 ){
                        cuenta_atras.setText(respuestaPHP);
                    }
            }

    @SuppressWarnings("unchecked")
    protected int startRequestEmergency() throws InterruptedException {
        JSONObject json = new JSONObject();
        json.put("valor", 151); //VALOR QUE MANDAMOS AL SERVIDOR.
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        emergency = new PostJSON(json);

        emergency.start();
        emergency.join();

        if (emergency.getResponse().equals("todo_correcto")) return 1;

        else if(emergency.getResponse().equals("todo_correcto")) return 2;

        respuestaPHP = emergency.getResponse();

        return 0;
    }

    @SuppressLint("SetTextI18n")
    protected void actualizarUbi(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        emergency_text.setText("¿Está usted bien?");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) Pregunta.this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude()*10000000;
                longitude = location.getLongitude()*10000000;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        ContextCompat.checkSelfPermission(Pregunta.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        // Register the listener with the Location Manager to receive location updates
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
}
