package com.example.captureimagesp.CaptureImageGrid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.captureimagesp.MainActivity;
import com.example.captureimagesp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CptureImageActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button mCaptureBtn;
    private GridView mImageGrid;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpture_image);

        mCaptureBtn = findViewById(R.id.capture_btn);
        mImageGrid = findViewById(R.id.image_grid);
        mAdapter = new ImageAdapter(this);
        mImageGrid.setAdapter(mAdapter);

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                // Toast.makeText(MainActivity.this, " Allow the Storage Permission", Toast.LENGTH_LONG).show();
            }
        });

        mImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String imageUri = mAdapter.getItem(position);
                Intent intent = new Intent(CptureImageActivity.this, ImageActivity.class);
                intent.putExtra("imageuri", imageUri);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
       /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private String saveImageToSharedPreferences(Bitmap imageBitmap) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int nextImageIndex = sharedPreferences.getInt("nextImageIndex", 0);
        String imageUri = "image_" + nextImageIndex + ".png";

        try {
            FileOutputStream fos = openFileOutput(imageUri, Context.MODE_PRIVATE);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("nextImageIndex", nextImageIndex + 1);
        editor.apply();

        return imageUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            String imageUri = saveImageToSharedPreferences(imageBitmap);

            mAdapter.addImage(imageUri);
            mAdapter.notifyDataSetChanged();
        }
    }
    private static class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<String> mImageUris;

        public ImageAdapter(Context context) {
            mContext = context;
            mImageUris = new ArrayList<>();

            // Load existing image URIs from internal storage
            File[] files = context.getFilesDir().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().startsWith("image_")) {
                        mImageUris.add(file.getName());
                    }
                }
            }
        }
        public void addImage(String imageUri) {
            mImageUris.add(imageUri);
        }

        @Override
        public int getCount() {
            return mImageUris.size();
        }

        @Override
        public String getItem(int position) {
            return mImageUris.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                // If it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            // Decode the image file into a Bitmap object
            String imageUri = mImageUris.get(position);
            try {
                FileInputStream fis = mContext.openFileInput(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                imageView.setImageBitmap(bitmap);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imageView;
        }
    }
}