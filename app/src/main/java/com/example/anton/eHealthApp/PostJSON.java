package com.example.anton.eHealthApp;

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

class PostJSON {

    private Activity_HeartRateDisplayBase heartRateDisplayBase;
    private double latitude;
    private double longitude;
    Request request;

    PostJSON(Activity_HeartRateDisplayBase heartRateDisplayBase) {
        this.heartRateDisplayBase = heartRateDisplayBase;
    }


    @SuppressWarnings("unchecked")
    boolean startRequestEmergency(int valorHR) throws InterruptedException {
        JSONObject json = new JSONObject();
        json.put("valor", valorHR); //VALOR QUE MANDAMOS AL SERVIDOR.
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        request = new Request(json);
        Log.d("latitud", Double.toString(latitude));
        Log.d("longitud", Double.toString(longitude));
        request.start();
        request.join();
        request.interrupt();

        if (request.getConexion()){
            Log.d("conexion", "conexion correcta");
            return true;
        }
        else{
            Log.d("conexion", "conexion fallida");
            return false;
        }

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
}

