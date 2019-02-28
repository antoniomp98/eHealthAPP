package com.example.anton.httprequest;

import android.location.Location;

public class GPS extends Thread{

    private Location location;
    private double latitud;
    private double longitud;
    private boolean desconocida;

    public double getLatitud(){
        return this.latitud;
    }
    public double getLongitud(){
        return this.longitud;
    }

    public void run(){
        while (true) {
            if (location != null) {
                this.latitud = location.getLatitude();
                this.longitud = location.getLongitude();
                this.desconocida = false;
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{

            }
        }
    }



}
