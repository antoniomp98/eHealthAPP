/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.example.anton.eHealthApp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.ICalculatedRrIntervalReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IHeartRateDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IPage4AddtDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.RrFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IRssiReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IVersionAndModelReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.juang.jplot.PlotPlanitoXY;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;


/**
 * Base class to connects to Heart Rate Plugin and display all the event data.
 */
public abstract class Activity_HeartRateDisplayBase extends AppCompatActivity {
    protected abstract void requestAccessToPcc();

    PlotPlanitoXY plot;
    LinearLayout grafica;
    TextView tv_computedHeartRate;
    long heartBeatCounter;
    private boolean conexion;
    private boolean dialog = false;
    Intent intent;
    boolean noSigas = false;
    Context context;
    boolean zerosent = false;

    float[] x = new float[100];
    float[] y = new float[100];

    AntPlusHeartRatePcc hrPcc = null;
    protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;
    protected PostJSON postJSON = new PostJSON();

    int latitude;
    int longitude;


   /* TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_rssi;

    TextView tv_heartBeatEventTime;

    TextView tv_manufacturerSpecificByte;
    TextView tv_previousHeartBeatEventTime;

    TextView tv_calculatedRrInterval;

    TextView tv_cumulativeOperatingTime;

    TextView tv_manufacturerID;
    TextView tv_serialNumber;

    TextView tv_hardwareVersion;
    TextView tv_softwareVersion;
    TextView tv_modelNumber;

    TextView tv_dataStatus;
    TextView tv_rrFlag;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = (int) Math.round(location.getLatitude() * 1000000);
                            longitude = (int) Math.round(location.getLongitude() * 1000000);
                        }
                    }
                });

        context = this;

        for (int j = 0; j < 100; j++) {
            x[j] = j;
            y[j] = 0;
        }

        handleReset();
    }


    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    protected void handleReset()
    {
        //Release the old access if it exists
        if(releaseHandle != null)
        {
            releaseHandle.close();
        }

        requestAccessToPcc();
    }

    protected void showDataDisplay()
    {
        setContentView(R.layout.activity_heart_rate);
        tv_computedHeartRate = findViewById(R.id.heartRate);
        grafica= findViewById(R.id.grafica);
        /*
        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_rssi = (TextView)findViewById(R.id.textView_Rssi);

        tv_heartBeatCounter = (TextView)findViewById(R.id.textView_HeartBeatCounter);
        tv_heartBeatEventTime = (TextView)findViewById(R.id.textView_HeartBeatEventTime);

        tv_manufacturerSpecificByte = (TextView)findViewById(R.id.textView_ManufacturerSpecificByte);
        tv_previousHeartBeatEventTime = (TextView)findViewById(R.id.textView_PreviousHeartBeatEventTime);

        tv_calculatedRrInterval = (TextView)findViewById(R.id.textView_CalculatedRrInterval);

        tv_cumulativeOperatingTime = (TextView)findViewById(R.id.textView_CumulativeOperatingTime);

        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        tv_hardwareVersion = (TextView)findViewById(R.id.textView_HardwareVersion);
        tv_softwareVersion = (TextView)findViewById(R.id.textView_SoftwareVersion);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_dataStatus = (TextView)findViewById(R.id.textView_DataStatus);
        tv_rrFlag = (TextView)findViewById(R.id.textView_rRFlag);

        */
        //Reset the text display
        tv_computedHeartRate.setText("---");

        /*
        tv_status.setText(status);

        tv_estTimestamp.setText("---");

        tv_rssi.setText("---");

        tv_heartBeatCounter.setText("---");
        tv_heartBeatEventTime.setText("---");

        tv_manufacturerSpecificByte.setText("---");
        tv_previousHeartBeatEventTime.setText("---");

        tv_calculatedRrInterval.setText("---");

        tv_cumulativeOperatingTime.setText("---");

        tv_manufacturerID.setText("---");
        tv_serialNumber.setText("---");

        tv_hardwareVersion.setText("---");
        tv_softwareVersion.setText("---");
        tv_modelNumber.setText("---");
        tv_dataStatus.setText("---");
        tv_rrFlag.setText("---");
        */
    }

    /**
     * Switches the active view to the data display and subscribes to all the data events
     */
    public void subscribeToHrEvents()
    {
        hrPcc.subscribeHeartRateDataEvent(new IHeartRateDataReceiver()
        {
            @Override
            public void onNewHeartRateData(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                final int computedHeartRate, final long heartBeatCount,
                final BigDecimal heartBeatEventTime, final DataState dataState)
            {

                // Mark heart rate with asterisk if zero detected
                final String textHeartRate = ((DataState.ZERO_DETECTED.equals(dataState)) ? "0" : String.valueOf(computedHeartRate));

                // Mark heart beat count and heart beat event time with asterisk if initial value
                /*final String textHeartBeatCount = String.valueOf(heartBeatCount)
                    + ((DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");
                final String textHeartBeatEventTime = String.valueOf(heartBeatEventTime)
                    + ((DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");*/
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_computedHeartRate.setText(textHeartRate);

                        if(((heartBeatCount != heartBeatCounter && !noSigas) ||(DataState.ZERO_DETECTED.equals(dataState)&& !zerosent)) &&
                                !(heartBeatCount == 1 && (DataState.ZERO_DETECTED.equals(dataState)))){
                            heartBeatCounter = heartBeatCount;
                            try {
                                Date date = Calendar.getInstance().getTime();
                                long ms = date.getTime();
                                if(DataState.ZERO_DETECTED.equals(dataState)) {
                                    conexion = postJSON.startRequestEmergency(0, ms, latitude, longitude);
                                    zerosent=true;
                                }
                                else
                                    conexion = postJSON.startRequestEmergency(computedHeartRate, ms, latitude, longitude);
                               if (postJSON.isTimerActive()) noSigas = true;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!conexion && intent == null && !dialog) {
                                dialog = true;
                                System.out.println("ERROR");
                                 AlertDialog.Builder builder =
                                            new AlertDialog.Builder(Activity_HeartRateDisplayBase.this);
                                 builder.setMessage("No se ha podido establecer conexión con el servidor")
                                         .setTitle("CONECTION ERROR")
                                         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                             public void onClick(DialogInterface dialog, int id) {
                                                 dialog.cancel();
                                                 intent = new Intent(Activity_HeartRateDisplayBase.this, Intro.class);
                                                 startActivity(intent);
                                                 finish();
                                             }
                                         });
                                 AlertDialog alert = builder.create();
                                 alert.show();
                            }
                            if(conexion && intent == null && postJSON.isTimerActive()){
                                intent = new Intent(Activity_HeartRateDisplayBase.this, Pregunta.class);
                                intent.putExtra("pid", postJSON.getPid());
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);
                                startActivity(intent);
                                finish();
                            }

                                grafica.removeAllViews();
                                System.arraycopy(y, 1, y, 0, 99);
                                y[99] = computedHeartRate;//datos
                                plot = new PlotPlanitoXY(context, "HEART RATE", "x", "valor pulsación");
                                plot.SetSerie1(x, y, "heartbeat", 0, true);
                                plot.SetHD(true);
                                plot.SetTouch(false);
                                plot.SetEscalaY1(35, 200);
                                grafica.addView(plot);

                        }

/*
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                         tv_heartBeatCounter.setText(textHeartBeatCount);
                        tv_heartBeatEventTime.setText(textHeartBeatEventTime);

                        tv_dataStatus.setText(dataState.toString());

 */
                    }
                });
            }
        });

        hrPcc.subscribePage4AddtDataEvent(new IPage4AddtDataReceiver()
        {
            @Override
            public void onNewPage4AddtData(final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                final int manufacturerSpecificByte,
                final BigDecimal previousHeartBeatEventTime)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        //tv_manufacturerSpecificByte.setText(String.format("0x%02X", manufacturerSpecificByte));
                        //tv_previousHeartBeatEventTime.setText(String.valueOf(previousHeartBeatEventTime));

                    }
                });
            }
        });

        hrPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver()
        {
            @Override
            public void onNewCumulativeOperatingTime(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                       // tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        //tv_cumulativeOperatingTime.setText(String.valueOf(cumulativeOperatingTime));
                    }
                });
            }
        });

        hrPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver()
        {
            @Override
            public void onNewManufacturerAndSerial(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int manufacturerID,
                final int serialNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                       // tv_estTimestamp.setText(String.valueOf(estTimestamp));

                       // tv_manufacturerID.setText(String.valueOf(manufacturerID));
                       // tv_serialNumber.setText(String.valueOf(serialNumber));
                    }
                });
            }
        });

        hrPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver()
        {
            @Override
            public void onNewVersionAndModel(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int hardwareVersion,
                final int softwareVersion, final int modelNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                      //  tv_estTimestamp.setText(String.valueOf(estTimestamp));

                      //  tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                      //  tv_softwareVersion.setText(String.valueOf(softwareVersion));
                      //  tv_modelNumber.setText(String.valueOf(modelNumber));
                    }
                });
            }
        });

        hrPcc.subscribeCalculatedRrIntervalEvent(new ICalculatedRrIntervalReceiver()
        {
            @Override
            public void onNewCalculatedRrInterval(final long estTimestamp,
                EnumSet<EventFlag> eventFlags, final BigDecimal rrInterval, final RrFlag flag)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                       // tv_estTimestamp.setText(String.valueOf(estTimestamp));
                       // tv_rrFlag.setText(flag.toString());

                        // Mark RR with asterisk if source is not cached or page 4
                       // if (flag.equals(RrFlag.DATA_SOURCE_CACHED)
                        //    || flag.equals(RrFlag.DATA_SOURCE_PAGE_4))
                         //   tv_calculatedRrInterval.setText(String.valueOf(rrInterval));
                       // else
                         //   tv_calculatedRrInterval.setText(String.valueOf(rrInterval) + "*");
                    }
                });
            }
        });

        hrPcc.subscribeRssiEvent(new IRssiReceiver() {
            @Override
            public void onRssiData(final long estTimestamp, final EnumSet<EventFlag> evtFlags, final int rssi) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  tv_estTimestamp.setText(String.valueOf(estTimestamp));
                       // tv_rssi.setText(String.valueOf(rssi) + " dBm");
                    }
                });
            }
        });
    }

    protected IPluginAccessResultReceiver<AntPlusHeartRatePcc> base_IPluginAccessResultReceiver =
        new IPluginAccessResultReceiver<AntPlusHeartRatePcc>()
        {
        //Handle the result, connecting to events on success or reporting failure to user.
        @Override
        public void onResultReceived(AntPlusHeartRatePcc result, RequestAccessResult resultCode,
            DeviceState initialDeviceState)
        {
            showDataDisplay();
            switch(resultCode)
            {
                case SUCCESS:
                    hrPcc = result;
                    //tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToHrEvents();

                  //  if(!result.supportsRssi()) tv_rssi.setText("N/A");
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                   // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_SHORT).show();
                   // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    //Note: Since we compose all the params ourself, we should never see this result
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Bad request parameters.", Toast.LENGTH_SHORT).show();
                   // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                   // tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                   // tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_HeartRateDisplayBase.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr.setMessage("The required service\n\"" + AntPlusHeartRatePcc.getMissingDependencyName() +
                            "\"\n was not found. You need to install the ANT+ Plugins service or you " +
                            "may need to update your existing version if you already have it. Do you want" +
                            " to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore;
                            startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusHeartRatePcc.getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_HeartRateDisplayBase.this.startActivity(startStore);
                        }
                    });
                    adlgBldr.setNegativeButton("Cancel", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog waitDialog = adlgBldr.create();
                    waitDialog.show();
                    break;
                case USER_CANCELLED:
                  //  tv_status.setText("Cancelled. Do Menu->Reset.");
                    break;
                case UNRECOGNIZED:
                    Toast.makeText(Activity_HeartRateDisplayBase.this,
                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                 //   tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_HeartRateDisplayBase.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                 //   tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }
        };

        //Receives state changes and shows it on the status display line
        protected  IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
            new IDeviceStateChangeReceiver()
        {
            @Override
            public void onDeviceStateChange(final DeviceState newDeviceState)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                  //      tv_status.setText(hrPcc.getDeviceName() + ": " + newDeviceState);
                    }
                });


            }
        };

        @Override
        protected void onDestroy()
        {
            if(releaseHandle != null)
            {
                releaseHandle.close();
            }
            super.onDestroy();
        }
}
