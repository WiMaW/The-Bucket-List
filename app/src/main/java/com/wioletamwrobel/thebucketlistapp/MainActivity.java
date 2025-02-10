package com.wioletamwrobel.thebucketlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        setUpClickListenrs();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void setUpClickListenrs() {
        CardView thingsCardView = findViewById(R.id.card_view_things_to_do);
        CardView placesToVisit = findViewById(R.id.card_view_places_to_visit);

        thingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent thingsIntent = new Intent(MainActivity.this, ThingsToDoActivity.class);
                startActivity(thingsIntent);
            }
        });

        placesToVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent thingsIntent = new Intent(MainActivity.this, PlacesToVisitActivity.class);
                startActivity(thingsIntent);
            }
        });
    }
}