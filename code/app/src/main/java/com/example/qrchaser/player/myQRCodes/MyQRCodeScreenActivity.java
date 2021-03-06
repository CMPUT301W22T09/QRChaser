package com.example.qrchaser.player.myQRCodes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.qrchaser.oop.QRCode;
import com.example.qrchaser.general.QRCodeAdapter;
import com.example.qrchaser.oop.QRCodeScoreComparator1;
import com.example.qrchaser.oop.QRCodeScoreComparator2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.qrchaser.R;
import com.example.qrchaser.general.SaveANDLoad;
import com.example.qrchaser.player.browse.BrowseQRActivity;
import com.example.qrchaser.player.map.MapActivity;
import com.example.qrchaser.player.profile.PlayerProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This Activity Class allows the Player to see their own scanned QR Codes, and contains a button that allows user to scan new qrcode
 */
public class MyQRCodeScreenActivity extends SaveANDLoad {
    // UI
    private BottomNavigationView bottomNavigationView;
    private ImageButton highToLowButton, lowToHighButton;
    private FloatingActionButton addQR;
    private ArrayList<QRCode> qrCodes = new ArrayList<>();
    private ListView myQRCodeListView;
    private ArrayAdapter<QRCode> qrCodeAdapter;
    // Database
    private final String TAG = "Error";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode_screen);

        // Setup UI
        myQRCodeListView = findViewById(R.id.listViewQRCode);
        addQR = findViewById(R.id.floatingActionButton);
        highToLowButton = findViewById(R.id.highToLow_button);
        lowToHighButton = findViewById(R.id.lowToHigh_button);


        highToLowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(qrCodes, new QRCodeScoreComparator1());
                qrCodeAdapter.notifyDataSetChanged();
            } // end onClick
        }); // end highToLowButton.setOnClickListener

        lowToHighButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(qrCodes, new QRCodeScoreComparator2());
                qrCodeAdapter.notifyDataSetChanged();
            } // end onClick
        }); // end lowToHighButton.setOnClickListener

        // Click on the Name to see details about the QR Code
        myQRCodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QRCode selectedQrCode = qrCodeAdapter.getItem(position);
                Intent intent = new Intent(MyQRCodeScreenActivity.this, EditQRCodeScreenActivity.class);
                intent.putExtra("qrHash", selectedQrCode.getHash());
                startActivity(intent);
            } // end onItemClick
        }); // end myQRCodeListView.setOnItemClickListener

        // ************************** Page Selection ****************************************
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Head to My QR Code Screen
        bottomNavigationView.setSelectedItemId(R.id.my_qr_code);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_qr_code:
                        return true;
                    case R.id.browse_qr:
                        startActivity(new Intent(getApplicationContext(), BrowseQRActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.map:
                        if(checkCoarseLocationPermission() && checkFineLocationPermission() && checkInternetPermission() && checkWritePermission()) {
                            launchMap();
                        } else {
                            requestMapPermissions();
                        }
                        return true;
                    case R.id.self_profile:
                        startActivity(new Intent(getApplicationContext(),PlayerProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            } //end onNavigationItemSelected
        });

        // Head to QRAddScreen
        addQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyQRCodeScreenActivity.this, QrAddScreenActivity.class);
                startActivity(intent);
            } // end onClick
        }); // end addQR.setOnClickListener
    } // end onCreate

    @Override
    public void onResume(){
        super.onResume();
        // Get the player email in order for the query
        String playerID = loadData(getApplicationContext(), "uniqueID");

        // Find all the QR codes that belong to this player, then add the name and score
        // to array lists.
        db = FirebaseFirestore.getInstance();
        CollectionReference QRCodesReference = db.collection("QRCodes");
        QRCodesReference.whereArrayContains("owners", playerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            qrCodes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QRCode qrCode = document.toObject(QRCode.class);
                                qrCodes.add(qrCode);
                            }// Populate the listview

                            Collections.sort(qrCodes);

                            qrCodeAdapter = new QRCodeAdapter(MyQRCodeScreenActivity.this,qrCodes);
                            myQRCodeListView.setAdapter(qrCodeAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    } // end onComplete
                });
    }


    /**
     * For testing purpose: Get the ArrayList of QRCode currently present on MyQRCodeScreen
     * @return qrCodes
     */
    public ArrayList<QRCode> getQrCodes() {
        return qrCodes;
    } // end getQrCodes

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean checkCoarseLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    } // end checkCoarseLocationPermission

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean checkFineLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    } // end checkFineLocationPermission

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean checkWritePermission() {
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    } // end checkWritePermission

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean checkInternetPermission() {
        return checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    } // end checkInternetPermission

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMapPermissions() {
        requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
    } // end requestMapPermissions

    private void launchMap() {
        startActivity(new Intent(getApplicationContext(),MapActivity.class));
        overridePendingTransition(0,0);
    } // end launchMap

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(checkCoarseLocationPermission() && checkFineLocationPermission() && checkWritePermission()) {
            launchMap();
        }
    } // end onRequestPermissionsResult
} // end MyQRCodeScreenActivity Class
