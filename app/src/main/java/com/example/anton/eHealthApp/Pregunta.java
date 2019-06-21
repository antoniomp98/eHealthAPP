package com.example.anton.eHealthApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Pregunta extends AppCompatActivity {

    TextView emergency_text;
    TextView cuenta_atras;
    TextView id_ambulancia;
    boolean conexion;
    int pid;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        pid = getIntent().getIntExtra("pid", 0);

        emergency_text = findViewById(R.id.textView2);
        cuenta_atras = findViewById(R.id.textView3);
        Button botonSI = findViewById(R.id.button2);
        Button botonNO = findViewById(R.id.button3);

        emergency_text.setText("¿Está usted bien?");

        final CountDownTimer contador = new CountDownTimer(100000, 1000) {

            public void onTick(long millisUntilFinished) {
                cuenta_atras.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                cuenta_atras.setText("Emergency");
                finish();
            }
        }.start();


        botonSI.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                contador.cancel();
                cuenta_atras.setText("Alarma Cancelada");
                PararTimer pararTimer = new PararTimer();
                pararTimer.start();
                try {
                    pararTimer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pararTimer.interrupt();
                finish();
            }
        });
        botonNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador.cancel();
                cuenta_atras.setText("Emergency");
                PararTimer pararTimer = new PararTimer();
                pararTimer.start();
                try {
                    pararTimer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pararTimer.interrupt();
                finish();
            }
        });
    }

    public class PararTimer extends Thread{
        public void run() {
            try {
                String pidString = Integer.toString(pid);
                URL url = new URL("http://192.168.3.141/pararTimer.php?pid="+pidString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                String responseMessage = urlConnection.getResponseMessage();

                String TAG = "Reply";
                Log.d(TAG, responseMessage + "         " + responseCode);

                //InputStream in = urlConnection.getInputStream();
                conexion = true;
            }catch (Exception e) {
                conexion = false;
                System.out.println(e.getMessage());
            }
        }
        /*boolean getConexion(){
            return conexion;
        }*/
    }


}
