package com.example.anton.httprequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONObject;


public class Pregunta extends AppCompatActivity {

    TextView emergency_text;
    TextView cuenta_atras;
    TextView id_ambulancia;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        emergency_text = findViewById(R.id.textView2);
        cuenta_atras = findViewById(R.id.textView3);
        Button botonSI = findViewById(R.id.button2);
        Button botonNO = findViewById(R.id.button3);

        emergency_text.setText("¿Está usted bien?");

        botonSI.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                cuenta_atras.setText("Alarma Cancelada");

            }
        });
        botonNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onFinish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Se activa al finalizar la cuenta atrás o al pulsar un botón
    @SuppressLint("SetTextI18n")
    public void onFinish() throws InterruptedException {

    }

}
