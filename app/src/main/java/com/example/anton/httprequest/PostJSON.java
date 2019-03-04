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

public class PostJSON extends Thread{

    private Activity_HeartRateDisplayBase heartRateDisplayBase;
    private String texto = "";
    private String base64json;
    private double latitude;
    private double longitude;

    PostJSON(Activity_HeartRateDisplayBase heartRateDisplayBase){
        this.heartRateDisplayBase = heartRateDisplayBase;
    }


    @SuppressWarnings("unchecked")
    void startRequestEmergency(int valorHR) throws InterruptedException {
        JSONObject json = new JSONObject();
        json.put("valor", valorHR); //VALOR QUE MANDAMOS AL SERVIDOR.
        json.put("latitude", latitude);
        json.put("longitude", longitude);

        String jsonString = json.toString();
        Base64.Encoder encoder = Base64.getEncoder();
        base64json = encoder.encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

        run();
        join();

        if (texto.equals("todo_correcto")){
            Log.d("resultado", "Todo correcto");
        }

        Log.d("resultado", texto);
    }

    @SuppressLint("SetTextI18n")
    void actualizarUbi() {

        //LocationManager locationManager1 = (LocationManager) heartRateDisplayBase.getSystemService(Context.LOCATION_SERVICE);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) heartRateDisplayBase.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude() * 10000000;
                longitude = location.getLongitude() * 10000000;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        //  ContextCompat.checkSelfPermission(Pregunta.this,
        //         Manifest.permission.ACCESS_FINE_LOCATION);
        // Register the listener with the Location Manager to receive location updates
        assert locationManager != null;
        if (ActivityCompat.checkSelfPermission(heartRateDisplayBase, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(heartRateDisplayBase,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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

    public void run(){
        try {
            URL url = new URL("http://163.117.140.34/cercana.php?json="+base64json);
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
}

