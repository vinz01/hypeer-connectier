package com.example.quasars.beaconsimulation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.service.RangedBeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "MainActivity";

    private Button startBtn;
    private Button stopBtn;

    private BeaconManager beaconManager = null;
    private Region beaconRegion = null;

    private BackgroundPowerSaver backgroundPowerSaver;

    View main;
    //List<Region> regionList = new ArrayList<Region>();

    static int currClue = 1;

    static int flag = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start);
        stopBtn = findViewById(R.id.stop);
        main = findViewById(R.id.mainActivity);

        //clueNum.setText("Clue Num " + currClue);

        /*regionList.add(new Region("Beacon1", Identifier.parse("B0702880-A295-A8AB-F734-031A98A512DE"),
                Identifier.parse("4"), Identifier.parse("200")));        //UUID , Major, Minor

        regionList.add(new Region("Beacon2", Identifier.parse("B0702880-A295-A8AB-F734-031A98A512DE"),
                Identifier.parse("4"), Identifier.parse("210")));

        regionList.add(new Region("Beacon3", Identifier.parse("B0702880-A295-A8AB-F734-031A98A512DE"),
                Identifier.parse("4"), Identifier.parse("220")));

        regionList.add(new Region("Beacon4", Identifier.parse("B0702880-A295-A8AB-F734-031A98A512DE"),
                Identifier.parse("4"), Identifier.parse("230")));*/

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBeaconMonitoring();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBeaconMonitoring();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

        RangedBeacon.setSampleExpirationMilliseconds(5000);
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        beaconManager.bind(this);
    }

    private void stopBeaconMonitoring() {

        Log.d(TAG, "stopBeaconMonitoring called. ");
        Toast.makeText(this, "Stopped Monitoring", Toast.LENGTH_SHORT).show();

        try {
            beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void startBeaconMonitoring() {

        Log.d(TAG, "startBeaconMonitoring called. ");
        Toast.makeText(this, "Started Monitoring", Toast.LENGTH_SHORT).show();

        try {

            //Log.d(TAG, "startBeaconMonitoring: "+.toString());
            /*for (Region i : regionList) {
                beaconRegion = i;*/

            beaconRegion = new Region("Beacon1", Identifier.parse("B0702880-A295-A8AB-F734-031A98A512DE"),
                    Identifier.parse("4"), Identifier.parse("200"));
            //Toast.makeText(this, "" + regionList.get(currClue - 1).toString(), Toast.LENGTH_SHORT).show();

            beaconManager.startMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
            /*
            beaconManager.startMonitoringBeaconsInRegion(regionList.get(currClue-1));
            beaconManager.startRangingBeaconsInRegion(regionList.get(currClue-1));*/
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeaconServiceConnect() {

        Log.d(TAG, "onBeaconServiceConnect called. ");

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                Toast.makeText(MainActivity.this, "Region = " + region.toString(), Toast.LENGTH_SHORT).show();

                beaconManager.addRangeNotifier(new RangeNotifier() {
                    @Override
                    public void didRangeBeaconsInRegion(Collection<Beacon> collection, final Region region) {

                        for (Beacon b : collection) {
                            Log.d(TAG, "didRangeBeaconsInRegion: Going INside");
                            if (b.getDistance() < 2.5) {

                                main.setBackgroundColor(Color.rgb(0, 255, 0));

                                Toast.makeText(MainActivity.this, "Beacon = " + b.toString() + " Distance = " + b.getDistance(), Toast.LENGTH_SHORT).show();


                            } else if (b.getDistance() < 10) {
                                main.setBackgroundColor(Color.rgb(0, 0, 255));
                                Toast.makeText(MainActivity.this, "" + b.getDistance(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Beacon = " + b.toString() + " Distance = " + b.getDistance(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });

            }

            @Override
            public void didExitRegion(Region region) {

                main.setBackgroundColor(Color.rgb(255, 0, 0));

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
}
