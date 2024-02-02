package com.wioletamwrobel.thebucketlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class PlacesToVisitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_to_visit);

        setUpList();
    }

    private void setUpList() {
        RecyclerView placesToVisitView = findViewById(R.id.recycler_view_places_to_visit);

        BucketListItem[] places = {
                new BucketListItem(R.drawable.portugal, R.string.place_to_visit1_title, 5.0f),
                new BucketListItem(R.drawable.iceland, R.string.place_to_visit2_title, 4.5f),
                new BucketListItem(R.drawable.sri_lanka, R.string.place_to_visit3_title, 4.0f)
        };

        BucketListItemAdapter adapter = new BucketListItemAdapter(places);
        placesToVisitView.setAdapter(adapter);
    }
}