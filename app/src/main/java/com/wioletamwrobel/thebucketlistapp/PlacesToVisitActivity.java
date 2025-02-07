package com.wioletamwrobel.thebucketlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlacesToVisitActivity extends AppCompatActivity {

    private FloatingActionButton fabAddPlace;
    private RecyclerView placesToVisitView;
    private BucketListItemAdapter adapter;
    private List<BucketListItem> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_to_visit);

        initViews();

        setUpRecyclerView();
        setUpFabClickListener();
        updateFabVisibility();
    }

    private void initViews() {
        placesToVisitView = findViewById(R.id.recycler_view_places_to_visit);
        fabAddPlace = findViewById(R.id.fab_add_place);
        places = new ArrayList<>();
    }

    private void setUpRecyclerView() {
        placesToVisitView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BucketListItemAdapter(places);
        placesToVisitView.setAdapter(adapter);
    }

    private void setUpFabClickListener() {
        fabAddPlace.setOnClickListener(view -> {
            places.add(new BucketListItem(R.drawable.portugal, R.string.place_to_visit1_title, 5.0f));
            adapter.notifyDataSetChanged();
            updateFabVisibility();
        });
    }

    private void updateFabVisibility() {
        if(places.isEmpty()) {
            fabAddPlace.setVisibility(View.VISIBLE);
        } else {
            fabAddPlace.setVisibility(View.GONE);
        }
    }
}