package com.example.a16019990.messageyourbuddy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etNumber;
    EditText etContent;
    Button btnSend;
    Button btnMessage;

    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        etNumber = findViewById(R.id.editTextNumber);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnMessage = findViewById(R.id.buttonMessage);

        IntentFilter intent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intent.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, intent);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                String number = etNumber.getText().toString();
                String message = etContent.getText().toString();

                if (number.contains(",")) {
                    String [] numbers = number.split(",");
                    for (String num : numbers) {
                        smsManager.sendTextMessage(num, null, message, null, null);
                    }
                } else {
                    smsManager.sendTextMessage(number, null, message, null, null);
                }
                Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                etContent.getText().clear();
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = etNumber.getText().toString();
                String message = etContent.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
                {
                    Uri uri = Uri.parse("smsto:" + to);
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    sendIntent.putExtra("sms_body", message);
                    startActivity(sendIntent);
                }
                else // For early versions, do what worked for you before.
                {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:" + to));
                    sendIntent.putExtra("sms_body", message);
                    startActivity(sendIntent);
                }
            }
        });
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
