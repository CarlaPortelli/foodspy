package com.example.foodspy;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.google.android.gms.common.GoogleApiAvailability;

import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String TRUE_CODE = "123456";
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private Object View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        //attach lock view with dot indicator
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        //set lock code length
        mPinLockView.setPinLength(6);

        //set listener for lock code change
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d(TAG, "lock code: " + pin);

                //User input true code
                if (pin.equals(TRUE_CODE)) {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("code", pin);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Failed code, try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "lock code is empty!");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                int d = Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });
    }

    public void openMap(View v) {
        String estType = v.getTag().toString();
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("estType", estType);
        GPSTracker gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (!gps.canGetLocation())
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        i.putExtra("latitude", gps.getLatitude());
        i.putExtra("longitude", gps.getLongitude());
        startActivity(i);
    }
}