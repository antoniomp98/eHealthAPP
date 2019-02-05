package com.example.anton.httprequest;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;



public class Pregunta extends AppCompatActivity {

    private static final String TAG = "Reply";
    private Button botonSI;
    private Button botonNO;
    TextView emergency_text;
    TextView cuenta_atras;
    TextView id_ambulancia;
    TextView tiempo_ambulancia;
    String respuestaPHP;
    private boolean falsa_alarma;
    private boolean emergencia;
    JSONParser parser = new JSONParser();
    Object obj;
    JSONObject array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        emergency_text = findViewById(R.id.textView2);
        cuenta_atras = findViewById(R.id.textView3);
        botonSI = findViewById(R.id.button2);
        botonNO = findViewById(R.id.button3);

        emergency_text.setText("¿Está usted bien?");

        new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                cuenta_atras.setText("seconds remaining: " + millisUntilFinished / 1000);
                botonSI.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        falsa_alarma = true;
                        cancel();
                        cuenta_atras.setText("Alarma Cancelada");
                    }
                });
                botonNO.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        falsa_alarma = true;
                        cancel();
                        onFinish();
                    }
                });
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish()
            {
                cuenta_atras.setText("Enviando señal de emergencia");
                try {
                    if(startThreadEmergency() == 1){
                        cuenta_atras.setText("ERROR AL CONECTAR CON EL SERVIDOR");
                        return;
                    }
                    setContentView(R.layout.respuesta);
                    id_ambulancia = findViewById(R.id.id_ambulancia);
                    tiempo_ambulancia = findViewById(R.id.tiempo);
                    id_ambulancia.setText("Ambulancia: "+ array.get("objectno"));
                    tiempo_ambulancia.setText("Tiempo para su llegada: "+ array.get("routetime")+" segundos");


                } catch (InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void onDestroy()
    {
        super.onDestroy();
    }

    protected int startThreadEmergency() throws InterruptedException, ParseException {

        ThreadEmergency emergency = new ThreadEmergency();

        emergency.start();
        emergency.join();

        if(emergency.getResponse().equals("")) return 1;

        respuestaPHP = emergency.getResponse();

        obj = parser.parse(respuestaPHP);
        array = (JSONObject)obj;
        Log.d(TAG, "The id is "+ array.get("objectno"));

        return 0;

    }

    private class ThreadEmergency extends Thread{

        private String b = "";

        public void run() {

            String urlString = "http://163.117.140.34/index.php"; // URL to call

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                int responseCode = urlConnection.getResponseCode();


                String responseMessage = urlConnection.getResponseMessage();

                Log.d(TAG, responseMessage + "         " + responseCode);

                InputStream in = urlConnection.getInputStream();
                b = getStringFromInputStream(in);
                Log.d(TAG, b);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        private String getResponse(){
            return b;
        }
    }




    private static String getStringFromInputStream(InputStream is) {

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
}
