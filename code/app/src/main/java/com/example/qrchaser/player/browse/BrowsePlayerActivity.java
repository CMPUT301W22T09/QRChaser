package com.example.qrchaser.player.browse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qrchaser.R;
import com.example.qrchaser.general.PlayerAdapter1;
import com.example.qrchaser.general.PlayerAdapter2;
import com.example.qrchaser.general.PlayerAdapter3;
import com.example.qrchaser.oop.Player;
import com.example.qrchaser.oop.PlayerNumQRComparator;
import com.example.qrchaser.oop.PlayerSingleScoreComparator;
import com.example.qrchaser.oop.PlayerTotalScoreComparator;
import com.example.qrchaser.player.map.MapActivity;
import com.example.qrchaser.player.myQRCodes.MyQRCodeScreenActivity;
import com.example.qrchaser.player.profile.PlayerProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class BrowsePlayerActivity extends AppCompatActivity {

    BottomNavigationView topNavigationView,bottomNavigationView;
    ImageButton numButton, totalButton, singleButton;
    TextView scoreType;
    final String TAG = "Sample";
    FirebaseFirestore db;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayAdapter<Player> playersAdapter1, playersAdapter2, playersAdapter3;
    ListView playersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_player);
        numButton = findViewById(R.id.num_button);
        totalButton = findViewById(R.id.total_button);
        singleButton = findViewById(R.id.single_button);
        scoreType = findViewById(R.id.score_text);
        playersListView = findViewById(R.id.listViewPlayer);

        db = FirebaseFirestore.getInstance();
        CollectionReference accountsRef = db.collection("Accounts");

        accountsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Player player = document.toObject(Player.class);
                                players.add(player);
                            }// Populate the listview

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        scoreType.setText("Number of QR");
                        playersAdapter1 = new PlayerAdapter1(BrowsePlayerActivity.this, players);
                        playersAdapter2 = new PlayerAdapter2(BrowsePlayerActivity.this, players);
                        playersAdapter3 = new PlayerAdapter3(BrowsePlayerActivity.this, players);
                        Collections.sort(players, new PlayerNumQRComparator());
                        playersAdapter1.notifyDataSetChanged();
                        playersListView.setAdapter(playersAdapter1);

                    }
                });

        numButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreType.setText("Number of QR");
                Collections.sort(players, new PlayerNumQRComparator());
                playersAdapter1.notifyDataSetChanged();
                playersListView.setAdapter(playersAdapter1);
            }
        });

        totalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreType.setText("All QR Sum");
                Collections.sort(players, new PlayerTotalScoreComparator());
                playersAdapter1.notifyDataSetChanged();
                playersListView.setAdapter(playersAdapter2);
            }
        });

        singleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreType.setText("Single Highest");
                Collections.sort(players, new PlayerSingleScoreComparator());
                playersAdapter1.notifyDataSetChanged();
                playersListView.setAdapter(playersAdapter3);
            }
        });


        //bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.browse_qr);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_qr_code:
                        startActivity(new Intent(getApplicationContext(), MyQRCodeScreenActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.browse_qr:
                        return true;
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.self_profile:
                        startActivity(new Intent(getApplicationContext(), PlayerProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //top Navigation bar
        topNavigationView = findViewById(R.id.top_navigation);
        topNavigationView.setSelectedItemId(R.id.browse_other_players);
        topNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.browse_qr_code:
                        startActivity(new Intent(getApplicationContext(), BrowseQRActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.browse_other_players:
                        return true;
                }
                return false;
            }
        });
    }
}