package com.example.qrchaser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyQRCodeScreenActivity extends AppCompatActivity {
    private Button button2,button3,button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode_screen);
        // ************************** Still need to add actual activity functionality ****************************************


        // ************************** Page Selection ****************************************
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        // Head to Browse Players
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRCodeScreenActivity.this, BrowseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Head to Map Screen
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRCodeScreenActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Head to Player Profile
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRCodeScreenActivity.this, PlayerProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

    } // end onCreate
} // end MyQRCodeScreenActivity Class