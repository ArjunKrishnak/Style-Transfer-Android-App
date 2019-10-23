package com.example.styletransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_SOURCE = 1;
    private static final int CAPTURE_IMAGE_SOURCE = 2;
    private static final int PICK_IMAGE_STYLE = 3;
    private static final int CAPTURE_IMAGE_STYLE= 4;
    private String CaptureSourceImagePath;
    private String PickedSourceImagePath;
    private String CaptureStyleImagePath;
    private String PickedStyleImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
    }

    public void PickImageActivity(int request) {
        Intent PickImageIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(PickImageIntent, request);
    }

    public void CaptureImageActivity(int request) {
        Intent TakePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (TakePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(request);
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                TakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(TakePictureIntent, request);
            }
        }
    }

    public void DisplayCapturedImage(String Path,ImageView ImageV){
        File imgFile = new  File(Path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageV.setImageBitmap(myBitmap);
        }
    }

    private File createImageFile(int request) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        if(request == CAPTURE_IMAGE_SOURCE)
            CaptureSourceImagePath = image.getAbsolutePath();
        else if(request == CAPTURE_IMAGE_STYLE)
            CaptureStyleImagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_SOURCE:
            case PICK_IMAGE_STYLE:
                if (resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query( selectedImage,
                            filePathColumn, null, null, null );
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex( filePathColumn[0] );
                    if(requestCode == PICK_IMAGE_SOURCE) {
                        PickedSourceImagePath = cursor.getString( columnIndex );
                        ImageView ImageV = findViewById(R.id.SourceImageView);
                        DisplayCapturedImage( PickedSourceImagePath, ImageV);
                    }
                    else if(requestCode == PICK_IMAGE_STYLE) {
                        PickedStyleImagePath = cursor.getString( columnIndex );
                        ImageView ImageV = findViewById(R.id.StyleImageView);
                        DisplayCapturedImage( PickedStyleImagePath, ImageV);
                    }
                    cursor.close();
                    break;
                }
            case CAPTURE_IMAGE_SOURCE:
                if (resultCode == RESULT_OK) {
                    ImageView ImageV = findViewById( R.id.SourceImageView );
                    DisplayCapturedImage( CaptureSourceImagePath, ImageV );
                }
                break;
            case CAPTURE_IMAGE_STYLE:
                if (resultCode == RESULT_OK) {
                    ImageView ImageV = findViewById( R.id.StyleImageView );
                    DisplayCapturedImage( CaptureStyleImagePath, ImageV );
                }
                break;
        }
    }

    public void SourceImageCapture(View view){
        CaptureImageActivity(CAPTURE_IMAGE_SOURCE);
    }
    public void SourceImagePick(View view){
        PickImageActivity(PICK_IMAGE_SOURCE);
    }
    public void StyleImageCapture(View view){
        CaptureImageActivity(CAPTURE_IMAGE_STYLE);
    }
    public void StyleImagePick(View view){
        PickImageActivity(PICK_IMAGE_STYLE);
    }

}
