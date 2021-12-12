package com.lab.battery_checker;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {

    private TextView textView,batteryActionStatus;
    private Button stopButton;
    private MediaPlayer mediaPlayer;
    WaveLoadingView waveLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryActionStatus = findViewById(R.id.batteryActionStatus);
        stopButton = findViewById(R.id.stopButton);
        textView = findViewById(R.id.batteryActionStatus);
        waveLoadingView = findViewById(R.id.waveLoadingView);



        batteryActionStatus.setText(isPhonePluggedIn(MainActivity.this) == true ?  "Charging " : "Not Charging");
      /*if (isPhonePluggedIn(this)){
            batteryActionStatus.setText("Charging");
        }else {
            batteryActionStatus.setText("Not Charging");
        }*/



        BroadcastReceiver broadcastReceiverBatteryCheck = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                batteryActionStatus.setText(isPhonePluggedIn(MainActivity.this) == true ?  "Charging " : "Not Charging");

                Integer integerBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                waveLoadingView.setProgressValue(integerBatteryLevel);
                waveLoadingView.setCenterTitle(integerBatteryLevel.toString());

                    if (integerBatteryLevel<15 && !isPhonePluggedIn(MainActivity.this)){
                        stopButton.setVisibility(View.VISIBLE);
                        playBatteryMusic();
                        batteryActionStatus.setText("Alarming");
                    }else {
                        stopButton.setVisibility(View.GONE);

                        batteryActionStatus.setText(isPhonePluggedIn(MainActivity.this) == true ?  "Charging " : "Battery Health is Okay");
                    }



            }
        };

        registerReceiver(broadcastReceiverBatteryCheck, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mediaPlayer!=null){

                   mediaPlayer.stop();
                   mediaPlayer.release();
                   mediaPlayer = null;
                   batteryActionStatus.setText("Alarm Disabled" );
                   Toast.makeText(MainActivity.this, "Alarm Stopped", Toast.LENGTH_SHORT).show();
               }
            }
        }
        );


    }


    @Override
    protected void onStart() {
        super.onStart();
        batteryActionStatus.setText(isPhonePluggedIn(MainActivity.this)==true? "Charging" : "Not Charging");


    }

/*    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer!=null){

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){

            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


    public  void  playBatteryMusic(){
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.battery_check);
        mediaPlayer.start();

    }

    public static boolean isPhonePluggedIn(Context context){
        boolean charging = false;

        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;

        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (batteryCharge) charging=true;
        if (usbCharge) charging=true;
        if (acCharge) charging=true;

        return charging;
    }
}