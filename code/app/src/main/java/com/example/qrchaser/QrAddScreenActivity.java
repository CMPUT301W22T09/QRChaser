package com.example.qrchaser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class QrAddScreenActivity extends AppCompatActivity {

    private Button scan, addPhoto, addLocation, confirm, cancel;
    private EditText nicknameET, commentET;
    String qrName, qrComment;
    String qrValue;

    FirebaseFirestore db;

    private FusedLocationProviderClient fusedLocationProviderClient;
    String latitude = "0";
    String longitude = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_add_screen);

        scan = findViewById(R.id.qr_scan_button);
        addPhoto = findViewById(R.id.qr_add_photo_button);
        addLocation = findViewById(R.id.qr_add_location_button);
        confirm = findViewById(R.id.qr_confirm_button);
        cancel = findViewById(R.id.qr_add_cancel_button);

        nicknameET = findViewById(R.id.qr_nickname_edit_text);
        commentET = findViewById(R.id.qr_comments_edit_text);

        ActivityResultLauncher<Intent> scannerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent scannerResult = result.getData();
                            qrValue = scannerResult.getStringExtra("qrValue");

                            //for testing the result
                            Toast.makeText(getApplicationContext(), qrValue, Toast.LENGTH_SHORT).show();
                        }
                    } // end onActivityResult
                }
        );

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrAddScreenActivity.this, CameraScannerActivity.class);
                scannerResultLauncher.launch(intent);
            } // end onClick
        }); // end scan.setOnClickListener

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022-03-13 Implement
            } // end onClick
        }); // end addPhoto.setOnClickListener

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = Double.toString(location.getLatitude());
                    longitude = Double.toString(location.getLongitude());
                }
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022-03-13 Implement


            } // end onClick
        }); // end addLocation.setOnClickListener

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean nameCheck = false;
                Boolean commentCheck = false;
                Boolean scanCheck = false;
                Boolean photoCheck = false;
                Boolean locationCheck = false;


                qrName = nicknameET.getText().toString();
                qrComment = commentET.getText().toString();
                // Check if qrValue nad name are not null, then check other optional values


                if (!qrName.isEmpty()) { nameCheck = true; }
                if (!qrComment.isEmpty()) { commentCheck = true; }
                if (qrValue != null) { scanCheck = true; }
                // Do other checks here

                if (nameCheck && scanCheck) {
                    // Call QRCode constructor here
                    QRCode scannedQR = new QRCode(qrValue, qrName);

                    // For testing
                    int score = scannedQR.getScore();
                    String scoreTestString = String.valueOf(score);
                    String hashTestString = scannedQR.getHash();
                    String testString = "score: " + scoreTestString + " hash: " + hashTestString;

                    // For Testing
                    Toast.makeText(getApplicationContext(), testString, Toast.LENGTH_SHORT).show();


                    //TODO: Need User object
//                    if(commentCheck) {
//                        QRComment(new User(), qrComment);
//                    }
                } else if(!nameCheck && !scanCheck) {
                    Toast.makeText(getApplicationContext(), "Please Scan and Enter nickname for this QRCode", Toast.LENGTH_SHORT).show();
                } else if(!scanCheck) {
                    Toast.makeText(getApplicationContext(), "Please Scan the QRCode", Toast.LENGTH_SHORT).show();
                } else if (!nameCheck) {
                    Toast.makeText(getApplicationContext(), "Please Enter nickname for this QRCode", Toast.LENGTH_SHORT).show();
                }
            } // end onClick
        }); // end confirm.setOnClickListener

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            } // end onClick
        }); // end cancel.setOnClickListener
    } // end onCreate
} // end QrAddScreenActivity Class
