package com.example.mycamera2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private Uri imageUri;
    private Bitmap bmp;
    ImageView imageView;
    private String fname_text;
    File saved_text_file;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView =findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }

    public void clickPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"photo_" +
//                String.valueOf(System.currentTimeMillis()) + ".jpg"));
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                Bundle extras = data.getExtras();
                bmp = (Bitmap) extras.get("data");
                imageView.setImageBitmap(bmp);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            }
        }
    }


        public void savePhoto(View view) {
            String root_path = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root_path + "/saved_images");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String fname = "Image-"+ String.valueOf(System.currentTimeMillis()) +".jpg";
            File file = new File (myDir, fname);
            if (file.exists ())
                file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Photo Saved ..")
                        .setMessage(fname)
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    public void savePhotoToText(View view) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        String root_path = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root_path + "/saved_images_as_text");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        fname_text  = "Image-Text"+ String.valueOf(System.currentTimeMillis()) +".txt";
        saved_text_file = new File (myDir, fname_text);
        if (saved_text_file.exists ())
            saved_text_file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(saved_text_file);
            out.write(byteArray);
            out.close();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Photo Saved ..")
                    .setMessage(fname_text)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showPhotoAsText(View view) throws Exception {

        FileOutputStream os = null;
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(saved_text_file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (FileNotFoundException e) {
            //You'll need to add proper error handling here
        }
        textView.setText(text);

    }
}