package com.wioletamwrobel.thebucketlistapp;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageOperations {

    static final String APP_NAME = "BucketListApp";

    public void loadSavedBucketList(Context context, String fileName, List<BucketListItem> list, RecyclerView.Adapter adapter) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(fileName, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<BucketListItem>>() {
            }.getType();
            list.clear();
            list.addAll(gson.fromJson(json, type));
            adapter.notifyDataSetChanged();
        }
    }

    public String saveSelectedImage(Context context, Uri imageUri, String directoryName, String filePrefix, int quality) {
        String imagePath = "";
        try {
            // Get the bitmap from the image URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            // Create the directory inside internal storage
            File directory = new File(context.getFilesDir(), directoryName);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique filename
            String fileName = filePrefix + "_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            // Save the image
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();

            // Return the saved image path
            imagePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.image_not_saved_toast, Toast.LENGTH_SHORT).show();
        }
        return imagePath;
    }

    public void updateImage(Context context, int position, Uri imageUri, List<BucketListItem> list, RecyclerView.Adapter adapter) {
        try {
            // Get the bitmap from the image URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            // Delete old image if it exists
            File oldFile = new File(list.get(position).getItemImagePath());
            if (oldFile.exists()) {
                oldFile.delete();
            }

            // Create a directory to store new images
            File directory = new File(context.getFilesDir(), "SavedImages");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a new unique filename
            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            // Save the new image
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Update the item with the new image path
            list.get(position).itemImage = file.getAbsolutePath();

            // Save the updated list (Assuming `saveUpdatedListToPrefs` is inside another helper)
            saveUpdatedListToPrefs(context, list, fileName);

            // Refresh RecyclerView
            adapter.notifyItemChanged(position);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.failed_to_update_image_toast), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUpdatedListToPrefs(Context context, List<BucketListItem> list, String fileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor.putString(fileName, json);
        editor.apply();
    }

    public void saveBucketListItemToPrefs(Context context, Uri imageUri, String directoryName, String title, float rating, List<BucketListItem> list, String fileName, RecyclerView.Adapter adapter) {
        String imagePath = saveSelectedImage(context, imageUri, directoryName, "IMG_", 100);

        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        list.add(new BucketListItem(imagePath, title, rating));
        String json = gson.toJson(list);
        editor.putString(fileName, json);
        editor.apply();

        adapter.notifyItemInserted(list.size() - 1);

        Toast.makeText(context, context.getString(R.string.goal_created_toast), Toast.LENGTH_SHORT).show();
    }

    public void showSnackbarWithImageStorageInfo(View view) {
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_storage_info, Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", view1 -> snackbar.dismiss());

        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackbarText.setMaxLines(5);
        snackbarText.setTextSize(12);

        snackbar.show();
    }
}
