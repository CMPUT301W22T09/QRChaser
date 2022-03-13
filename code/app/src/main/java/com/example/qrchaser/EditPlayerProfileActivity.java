package com.example.qrchaser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditPlayerProfileActivity extends AppCompatActivity {
    private EditText emailET, passwordET, nicknameET, phoneNumberET;
    private Button buttonConfirm, buttonSignOut, buttonGenerateLoginQRCode, buttonGenerateInfoQRCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Using a dummy player for now
        // TODO: 2022-03-12 Pass In Actual Players
        Player currentPlayer = new Player("TestPlayer@gmail.com", "TestPassword", "TestPlayer" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_profile);

        // Initialize
        emailET = findViewById(R.id.editTextEmailAddress);
        emailET.setText(currentPlayer.getEmail());
        passwordET = findViewById(R.id.editTextPassword);
        passwordET.setText(currentPlayer.getPassword());
        nicknameET = findViewById(R.id.editTextNickname);
        nicknameET.setText(currentPlayer.getNickname());
        phoneNumberET = findViewById(R.id.editTextPhone);
        phoneNumberET.setText(currentPlayer.getPhoneNumber());
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonGenerateLoginQRCode = findViewById(R.id.ButtonGenerateLoginQRCode);
        buttonGenerateInfoQRCode = findViewById(R.id.ButtonGenerateInfoQRCode);

        //Confirm all changes made
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(emailET.getText().toString())) {
                    currentPlayer.setEmail(emailET.getText().toString());
                }
                if(!TextUtils.isEmpty(passwordET.getText().toString())) {
                    currentPlayer.setPassword(passwordET.getText().toString());
                }
                if(!TextUtils.isEmpty(nicknameET.getText().toString())) {
                    currentPlayer.setNickname(nicknameET.getText().toString());
                }
                if(!TextUtils.isEmpty(phoneNumberET.getText().toString())) {
                    currentPlayer.setPhoneNumber(phoneNumberET.getText().toString());
                }
            } // end onClick
        });// end buttonConfirm.setOnClickListener

        // Sign Out
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2022-03-12 Implement
            } // end onClick
        });// end buttonSignOut.setOnClickListener

        // Generate Login QRCode
        buttonGenerateLoginQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2022-03-12 pass data (Username, password and special Code?) to the new activity
                Intent intent = new Intent(EditPlayerProfileActivity.this, GeneratedQRCodeActivity.class);
                startActivity(intent);
            } // end onClick
        });// end buttonGenerateLoginQRCode.setOnClickListener

        // Generate Info QRCode
        buttonGenerateInfoQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2022-03-12 pass data (Username and special Code?) to the new activity
                Intent intent = new Intent(EditPlayerProfileActivity.this, GeneratedQRCodeActivity.class);
                startActivity(intent);
            } // end onClick
        });// end buttonGenerateInfoQRCode.setOnClickListener

    } // end onCreate
} // end PlayerProfileInfoActivity Class