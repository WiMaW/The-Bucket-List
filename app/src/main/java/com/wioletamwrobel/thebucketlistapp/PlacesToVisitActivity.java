package com.wioletamwrobel.thebucketlistapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlacesToVisitActivity extends AppCompatActivity {

    private FloatingActionButton fabAddPlace;
    private RecyclerView placesToVisitView;
    private BucketListItemAdapter adapter;
    private List<BucketListItem> places;

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
   private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageView imagePreview;
    private ImageView changedImagePreview;
    private int selectedPhotoPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_to_visit);

        initViews();

        setUpRecyclerView();
        loadSavedPlaces();
        setUpFabClickListener();

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                    }
                    if(imagePreview != null && imagePreview.isAttachedToWindow()) imagePreview.setImageURI(imageUri);
                    if(changedImagePreview != null && changedImagePreview.isAttachedToWindow()) changedImagePreview.setImageURI(imageUri);
                }
        );
    }

    private void initViews() {
        placesToVisitView = findViewById(R.id.recycler_view_places_to_visit);
        fabAddPlace = findViewById(R.id.fab_add_place);
        places = new ArrayList<>();

    }

    private void setUpRecyclerView() {
        placesToVisitView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BucketListItemAdapter(places, position -> showEditDeleteDialog(position));
        placesToVisitView.setAdapter(adapter);
    }

    private void showEditDeleteDialog(int position) {
        BucketListItem bucketListItem = places.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_edit_item_dialog, null);
        builder.setView(dialogView);

        EditText changeTitle = dialogView.findViewById(R.id.change_title_edit_text);
        RatingBar changeRatingBar = dialogView.findViewById(R.id.change_rating_bar);
        ImageButton btnDelete = dialogView.findViewById(R.id.delete_item_button);
        changedImagePreview = dialogView.findViewById(R.id.changed_image_preview);
        ImageButton changeImage = dialogView.findViewById(R.id.change_image_button);

        changeTitle.setText(bucketListItem.getItemTitle());
        changeRatingBar.setRating(bucketListItem.getItemRating());

        File imgFile = new File(bucketListItem.getItemImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            changedImagePreview.setImageBitmap(bitmap);
        }

        changeImage.setOnClickListener(v -> {
            selectedPhotoPosition = position;
            openGallery();
        });


        builder.setPositiveButton("Save", (dialog, which) -> {
            // Update item in list
            bucketListItem.itemTitle = changeTitle.getText().toString();
            bucketListItem.itemRating = changeRatingBar.getRating();

            // Save updated image and list to SharedPreferences
            saveNewImage(selectedPhotoPosition, imageUri);

            // Refresh RecyclerView
            adapter.notifyItemChanged(position);
            Toast.makeText(this, "Your goal was updated!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Handle Delete button
        btnDelete.setOnClickListener(v -> {
            places.remove(position);
            saveUpdatedListToPrefs();
            adapter.notifyItemRemoved(position);
            dialog.dismiss();
            Toast.makeText(this, "Your goal was deleted!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void saveUpdatedListToPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("BucketListApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(places);

        editor.putString("places_list", json);
        editor.apply();
    }

    private void setUpFabClickListener() {
        fabAddPlace.setOnClickListener(view -> {
            showAddBucketItemDialog();
            placesToVisitView.scrollToPosition(places.size() - 1);
        });
    }

    private void showAddBucketItemDialog() {

        //alert dialog title create ...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_bucket_list_item_dialog, null);
        builder.setView(dialogView);

        imagePreview = dialogView.findViewById(R.id.image_preview);
        ImageButton btnAddImage = dialogView.findViewById(R.id.add_image_button);
        EditText addTitle = dialogView.findViewById(R.id.add_title_edit_text);
        RatingBar addRating = dialogView.findViewById(R.id.add_rating_bar);

        btnAddImage.setOnClickListener(v -> {
                    openGallery();
                }
        );

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = addTitle.getText().toString();
            float rating = addRating.getRating();

            if (imageUri != null && !title.isEmpty()) {
                saveBucketListItemToStorage(imageUri, title, rating);
            } else {
                Toast.makeText(this, "Please select an image and enter a title.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveBucketListItemToStorage(Uri imageUri, String title, float rating) {

        String imagePath = saveSelectedImage(imageUri);

        SharedPreferences sharedPreferences = getSharedPreferences("BucketListApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        places.add(new BucketListItem(imagePath, title, rating));
        String json = gson.toJson(places);
        editor.putString("places_list", json);
        editor.apply();

        adapter.notifyItemInserted(places.size() - 1);

        Toast.makeText(this, "Goal created", Toast.LENGTH_SHORT).show();
    }

    private String saveSelectedImage(Uri imageUri) {
        String imagePath = "";
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            File directory = new File(getFilesDir(), "SavedPlaces");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            imagePath = file.getAbsolutePath();
        } catch (IOException e) {

            // to implement



        }
        return imagePath;
    }

    private void saveNewImage(int position, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Delete old image
            File oldFile = new File(places.get(position).getItemImagePath());
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // Save new image
            File directory = new File(getFilesDir(), "SavedImages");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Update item with new image path
            places.get(position).itemImage = file.getAbsolutePath();

            // Save changes
            saveUpdatedListToPrefs();

            // Refresh RecyclerView
            adapter.notifyItemChanged(position);

            Toast.makeText(this, "Image Updated!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to update image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void updatePreview(ImageView preview) {
        preview.setImageURI(imageUri);
    }

    private void loadSavedPlaces() {
        SharedPreferences sharedPreferences = getSharedPreferences("BucketListApp", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("places_list", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<BucketListItem>>() {
            }.getType();
            places.clear();
            places.addAll(gson.fromJson(json, type));
            adapter.notifyDataSetChanged();
        }
    }
}