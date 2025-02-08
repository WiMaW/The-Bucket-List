package com.wioletamwrobel.thebucketlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThingsToDoActivity extends AppCompatActivity {

    private FloatingActionButton fabAddThing;
    private RecyclerView thingsToDoView;
    private BucketListItemAdapter adapter;
    private List<BucketListItem> things;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things_to_do);

        initViews();

        setUpRecyclerView();
//        setUpFabClickListener();
    }

    private void initViews() {
        thingsToDoView = findViewById(R.id.recycler_view_things_to_do);
        fabAddThing = findViewById(R.id.fab_add_things);
        things = new ArrayList<>();
    }

    private void setUpRecyclerView() {
        adapter = new BucketListItemAdapter(things);
        thingsToDoView.setAdapter(adapter);
    }

//    private void setUpFabClickListener() {
//        fabAddThing.setOnClickListener(view -> {
//            things.add(new BucketListItem(R.drawable.mountain_walk, R.string.thing_to_do2_title, 5.0f));
//            adapter.notifyItemInserted(things.size() - 1);
//            thingsToDoView.scrollToPosition(things.size() - 1);
//        });
//    }
}
