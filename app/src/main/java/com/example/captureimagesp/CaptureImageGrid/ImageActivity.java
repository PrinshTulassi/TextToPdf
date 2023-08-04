package com.example.captureimagesp.CaptureImageGrid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.captureimagesp.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mImageView = findViewById(R.id.image_view);

        // Get the URI of the image to display from the Intent
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("imageuri");

        // Decode the image file into a Bitmap object
        try {
            FileInputStream fis = openFileInput(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bitmap);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}