package com.wioletamwrobel.thebucketlistapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThingsToDoActivity extends AppCompatActivity {

    private FloatingActionButton fabAddThing;
    private RecyclerView thingsToDoView;
    private BucketListItemAdapter adapter;
    private List<BucketListItem> things;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageView imagePreview;
    private ImageView changedImagePreview;
    private int selectedPhotoPosition = -1;
    private static final String FILE_NAME = "things_list";
    private static final String DIRECTORY_NAME = "SavedThings";

    private StorageOperations storageOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things_to_do);

        initViews();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.things_to_do);
        }

        storageOperations = new StorageOperations();

        setUpRecyclerView();
        setUpFabClickListener();

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                    }
                    if (imagePreview != null && imagePreview.isAttachedToWindow())
                        imagePreview.setImageURI(imageUri);
                    if (changedImagePreview != null && changedImagePreview.isAttachedToWindow())
                        changedImagePreview.setImageURI(imageUri);
                }
        );
    }

    private void initViews() {
        thingsToDoView = findViewById(R.id.recycler_view_things_to_do);
        fabAddThing = findViewById(R.id.fab_add_things);
        things = new ArrayList<>();
    }

    private void setUpRecyclerView() {
        adapter = new BucketListItemAdapter(things, position -> showEditDeleteDialog(position));
        thingsToDoView.setAdapter(adapter);
    }

    private void setUpFabClickListener() {
        fabAddThing.setOnClickListener(view -> {
            showAddBucketItemDialog();
          //  adapter.notifyItemInserted(things.size() - 1);
            thingsToDoView.scrollToPosition(things.size() - 1);
        });
    }

    private void showAddBucketItemDialog() {

        //alert dialog title create ...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(ThingsToDoActivity.this);
        ScrollView dialogView = (ScrollView) inflater.inflate(R.layout.add_bucket_list_item_dialog, null);
        builder.setView(dialogView);

        imagePreview = dialogView.findViewById(R.id.image_preview);
        ImageButton btnAddImage = dialogView.findViewById(R.id.add_image_button);
        EditText addTitle = dialogView.findViewById(R.id.add_title_edit_text);
        RatingBar addRating = dialogView.findViewById(R.id.add_rating_bar);


        btnAddImage.setOnClickListener(v -> {
                    openGallery();
                }
        );
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String title = addTitle.getText().toString();
            float rating = addRating.getRating();

            if (imageUri != null && !title.isEmpty()) {
                storageOperations.saveBucketListItemToStorage(this, imageUri, DIRECTORY_NAME, title, rating, things, FILE_NAME, adapter);
            } else {
                Toast.makeText(this, getString(R.string.toast_select_image_title), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditDeleteDialog(int position) {
        BucketListItem bucketListItem = things.get(position);

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

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            // Update item in list
            bucketListItem.itemTitle = changeTitle.getText().toString();
            bucketListItem.itemRating = changeRatingBar.getRating();

            // Save updated image and list to SharedPreferences
            storageOperations.updateImage(this, selectedPhotoPosition, imageUri, things, adapter);

            // Refresh RecyclerView
            adapter.notifyItemChanged(position);
            Toast.makeText(this, getString(R.string.your_goal_was_updated_toast), Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Handle Delete button
        btnDelete.setOnClickListener(v -> {
            things.remove(position);
            storageOperations.saveUpdatedListToPrefs(this, things, FILE_NAME);
            adapter.notifyItemRemoved(position);
            dialog.dismiss();
            Toast.makeText(this, getString(R.string.your_goal_was_deleted_toast), Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
}
