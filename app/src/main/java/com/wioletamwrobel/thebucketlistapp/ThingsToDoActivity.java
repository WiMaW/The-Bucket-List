package com.wioletamwrobel.thebucketlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ThingsToDoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things_to_do);

        setUpList();
    }

    private void setUpList() {
        RecyclerView thingsToDoView = findViewById(R.id.recycler_view_things_to_do);

        BucketListItem[] thingsToDo = {
                new BucketListItem(R.drawable.mobile_app_developer, R.string.thing_to_do1_title, 5.0f),
                new BucketListItem(R.drawable.mountain_walk, R.string.thing_to_do2_title, 5.0f),
                new BucketListItem(R.drawable.home, R.string.thing_to_do3_title, 4.5f),
                new BucketListItem(R.drawable.oil_painting, R.string.thing_to_do4_title, 4.0f),
        };

        BucketListItemAdapter adapter = new BucketListItemAdapter(thingsToDo);
        thingsToDoView.setAdapter(adapter);
    }
}