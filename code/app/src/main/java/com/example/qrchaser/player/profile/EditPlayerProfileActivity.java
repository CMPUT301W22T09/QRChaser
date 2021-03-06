package com.example.qrchaser.player.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qrchaser.R;
import com.example.qrchaser.general.SaveANDLoad;
import com.example.qrchaser.logIn.WelcomeActivity;
import com.example.qrchaser.oop.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

/**
 * This Activity Class allows the user to see and change their password, nickname and phone number
 */
public class EditPlayerProfileActivity extends SaveANDLoad {
    // UI
    private EditText emailET, nicknameET, phoneNumberET;
    private Button buttonConfirm, buttonSignOut, buttonGenerateLoginQRCode, buttonGenerateInfoQRCode;
    // General Data
    private Player currentPlayer;
    ArrayList<String> nickNameList = new ArrayList<>();
    // Database
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_profile);

        // Setup UI
        emailET = findViewById(R.id.editTextEmail);
        nicknameET = findViewById(R.id.editTextNickname);
        phoneNumberET = findViewById(R.id.editTextPhone);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonGenerateLoginQRCode = findViewById(R.id.ButtonGenerateLoginQRCode);
        buttonGenerateInfoQRCode = findViewById(R.id.ButtonGenerateInfoQRCode);

        // Get the player id in order to load the data from the database
        String playerID = loadData(getApplicationContext(),"uniqueID");

        // Get Player info from the database
        db = FirebaseFirestore.getInstance();
        CollectionReference accountsRef = db.collection("Accounts");
        DocumentReference myAccount = accountsRef.document(playerID);

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.CACHE;

        // Get the document, forcing the SDK to use the offline cache
        myAccount.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    currentPlayer = document.toObject(Player.class);

                    // Initialize
                    emailET.setText(currentPlayer.getEmail());
                    nicknameET.setText(currentPlayer.getNickname());
                    phoneNumberET.setText(currentPlayer.getPhoneNumber());
                } else {
                    Toast.makeText(getApplicationContext(),"Load Failed",Toast.LENGTH_LONG).show();
                }
            }
        }); // end addOnCompleteListener

        // Confirm all changes made (Push the changes to the database)
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // format check
                currentPlayer.setEmail(emailET.getText().toString());
                currentPlayer.setPhoneNumber(phoneNumberET.getText().toString());
                nickNameList = new ArrayList<>();
                String nickname = nicknameET.getText().toString();
                final String TAG = "Error";

                accountsRef
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Player player = document.toObject(Player.class);
                                        nickNameList.add(player.getNickname());
                                    }// Populate the listview

                                    if (nickNameList.contains(nickname)){
                                        Toast.makeText(getApplicationContext(),"Name already exists",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        currentPlayer.setNickname(nickname);
                                        currentPlayer.updateDatabase();
                                        finish();
                                    }

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            } // end onComplete
                        }); // end accountsRef.get().addOnCompleteListener

            } // end onClick
        }); // end buttonConfirm.setOnClickListener

        // Sign Out
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditPlayerProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.signout_dialog, null);
                Button confirm = (Button) mView.findViewById(R.id.button_confirm);
                Button cancel = (Button) mView.findViewById(R.id.button_cancel);
                dialogBuilder.setView(mView);

                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        saveData(getApplicationContext(),"uniqueID", "");
                        Intent intent = new Intent(EditPlayerProfileActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    } // end onClick
                }); // end confirm.setOnClickListener(new View.OnClickListener()

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    } // end onClick
                }); // end confirm.setOnClickListener(new View.OnClickListener()

            } // end onClick
        }); // end buttonSignOut.setOnClickListener

        // Generate Login QRCode
        buttonGenerateLoginQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPlayerProfileActivity.this, GeneratedQRCodeActivity.class);
                intent.putExtra("qrData","QRCHASERLOGIN," + currentPlayer.getUniqueID());
                intent.putExtra("qrTitle","Player Login Code");
                startActivity(intent);
            } // end onClick
        }); // end buttonGenerateLoginQRCode.setOnClickListener

        // Generate Info QRCode
        buttonGenerateInfoQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPlayerProfileActivity.this, GeneratedQRCodeActivity.class);
                intent.putExtra("qrData","QRCHASERINFO," + currentPlayer.getUniqueID());
                intent.putExtra("qrTitle","Player Info Code");
                startActivity(intent);
            } // end onClick
        }); // end buttonGenerateInfoQRCode.setOnClickListener
    } // end onCreate

} // end PlayerProfileInfoActivity Class
