package com.example.permissionsexc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText edtName;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        findViews();
        initViews();
    }

    private void initViews() {
    btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        if (edtName.getText().toString().isEmpty())
            Toast.makeText(LoginActivity.this,"Please enter your name",Toast.LENGTH_SHORT).show();
        else
            checkCondition(LoginActivity.this);
        }
    });
    }

    private void findViews() {
        edtName = findViewById(R.id.login_activity_ETName);
        btnLogin = findViewById(R.id.login_activity_btnLogin);
    }

    private void checkCondition (Context context){
        if (namePrcentageBattery(context) &&
                checkNFC(context) &&
                IsLowestBrightness() &&
                HasDarkMode (context))
            Toast.makeText(LoginActivity.this,"Login successful!",Toast.LENGTH_SHORT).show();

    }

    private boolean checkNFC(Context context) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = manager.getDefaultAdapter();
        if (nfcAdapter == null) {
            Toast.makeText(LoginActivity.this,"NFC is not available for device",Toast.LENGTH_SHORT).show();
            return false;
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(LoginActivity.this,"NFC is available for device but not enabled",Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
        }

        private boolean namePrcentageBattery (Context context){
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) (level * 100 / (float)scale);
            if (!edtName.getText().toString().endsWith(String.valueOf(batteryPct))) {
                Toast.makeText(LoginActivity.this, "Your name should end with battery percentage!", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
    }

        private boolean IsLowestBrightness (){
        try {
            float curBrightnessValue=android.provider.Settings.System.getInt(
                        getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            if (curBrightnessValue>1) {
                Toast.makeText(LoginActivity.this, "The screen brightness must be most lowest!", Toast.LENGTH_SHORT).show();
                return false;
            }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        return true;
        }

    private boolean HasDarkMode(Context context){
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                Toast.makeText(LoginActivity.this, "The screen must be in night mode", Toast.LENGTH_SHORT).show();
                return false;
        }
            return false;
    }
    }