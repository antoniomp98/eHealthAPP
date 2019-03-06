package com.example.anton.eHealthApp;

import android.util.Log;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Request extends Thread{

    private String texto;
    private String base64json;


    Request(JSONObject json){
        String jsonString = json.toString();
        Base64.Encoder encoder = Base64.getEncoder();
        base64json = encoder.encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
    }

    public void run() {

        try {
            URL url = new URL("http://163.117.140.34/comprobar.php?json="+base64json);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            String responseMessage = urlConnection.getResponseMessage();

            String TAG = "Reply";
            Log.d(TAG, responseMessage + "         " + responseCode);

            InputStream in = urlConnection.getInputStream();
            texto = getStringFromInputStream(in);
            Log.d(TAG, texto);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    String getResponse(){
        return texto;
    }

    //Convierte el stream de datos a un String
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

