package com.example.stalker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION_SMS = 1546;
    private String[] permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS};
    private TextView txtView;
    private static final Pattern sPattern
            = Pattern.compile("^[+]?[0-9]{10,12}$");

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final String PHONE_NUMBER = "phone_number";
    private static final String MSG_TO_SEND = "message to send";
    private static final int PHONE = 0;
    private static final int MSG = 1;
    private static final int GOOD = 1;
    private static final int BAD = 0;
    private boolean validPhone = false;
    private boolean validMsg = true;
    private boolean isValid(CharSequence s) {
        return sPattern.matcher(s).matches();
    }

    final private MyBroadcaster broadcast = new MyBroadcaster();
    String currText;
    String currNumb;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = findViewById(R.id.information);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        editor.putString(MSG_TO_SEND, currText);
        editor.apply();
        if(!arePermissionsEnabled()) {
            requestMultiplePermissions();
        }

        String infoMsg = "Please enter a valid phone number and a text message";
        txtView.setText(infoMsg);
        AppCompatEditText phoneInput = findViewById(R.id.phone_number);
        phoneInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                 Log.d("phoneinput", "in func");
                if (isValid(phoneInput.getText()))
                {
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.GREEN);
                    phoneInput.setSupportBackgroundTintList(colorStateList);
                    currNumb = phoneInput.getText().toString();
                    updateText(PHONE, GOOD);
                }
                else{
                    ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
                    phoneInput.setSupportBackgroundTintList(colorStateList);
                    updateText(PHONE, BAD);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }


        });
        EditText msgInput = findViewById(R.id.message_content);
        msgInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!msgInput.getText().toString().equals(""))
                {
                    currText = msgInput.getText().toString();
                    updateText(MSG, GOOD);
                }
                else{
                    updateText(MSG, BAD);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }


        });



    }

    private void updateText(int phone, int goodbit) {
        String infoMsg = "";
        if (phone == PHONE)
        {
            validPhone = GOOD == goodbit;
            if (validPhone) {
                editor.putString(PHONE_NUMBER, currNumb);
                editor.apply();
            }
        }
        else
        {
            validMsg = GOOD == goodbit;
            if (validMsg) {
                editor.putString(MSG_TO_SEND, currText);
                editor.apply();
            }
        }
        if (validPhone && validMsg)
        {
            infoMsg = "The app is ready to send SMS messages";
        }else if (validPhone)
        {
            infoMsg = "Please enter a text message";
        } else if(validMsg)
        {
            infoMsg = "Please enter a valid phone number";
        }
        else {
            infoMsg = "Please enter a valid phone number and a text message";
        }
        txtView.setText(infoMsg);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions)
        {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
            {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled() {
        for (String permission : permissions)
        {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
            {
                return false;            }
        }
        return true;
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        new AlertDialog.Builder(this)
                                .setMessage("Cannot continue without your consent")
                                .setPositiveButton("Allow", (dialog, which) -> requestMultiplePermissions())
                                .setNegativeButton("Cancel", (dialog, which) -> finish())
                                .create()
                                .show();
                    }
                    return;
                }
            }

        }
    }


}