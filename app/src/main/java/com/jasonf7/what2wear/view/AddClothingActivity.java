package com.jasonf7.what2wear.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jasonf7.what2wear.R;

/**
 * Created by jasonf7 on 26/05/15.
 */
public class AddClothingActivity extends Activity {
    private Context context;

    private EditText nameEdit, descEdit;
    private ImageView clothingImageView;
    private SeekBar prefSeekBar;
    private TextView prefText;
    private Spinner clothingTypeSpinner;
    private Button addSubmitButton;

    private static final int GALLERY_PHOTO = 1;
    private static final int CAMERA_CAPTURE = 2;
    private static final int PICTURE_CROP = 3;

    private int lastIntentReceived;
    private Uri picUri;
    private Bitmap clothingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        nameEdit = (EditText) findViewById(R.id.nameEdit);
        descEdit = (EditText) findViewById(R.id.descEdit);
        clothingImageView = (ImageView) findViewById(R.id.clothingImage);
        clothingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.image_options_title)
                        .setItems(R.array.imageDialogOptions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // Choose existing photo
                                    try{
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, GALLERY_PHOTO);
                                    } catch (ActivityNotFoundException e) {
                                        String errorMessage = "Gallery not found on device!";
                                        Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                } else {
                                    // Take a new photo
                                    try{
                                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                                    } catch (ActivityNotFoundException e) {
                                        String errorMessage = "Camera capture not supported by this device!";
                                        Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            }
                        });
                builder.create().show();
            }
        });
        prefSeekBar = (SeekBar) findViewById(R.id.prefSeekBar);
        prefSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefText.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        prefText = (TextView) findViewById(R.id.prefText);
        clothingTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        addSubmitButton = (Button) findViewById(R.id.addSubmitButton);
        addSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString();

                if(name.equals("")) {
                    String errorMessage = "Error: Empty clothing name!";
                    Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                String description = nameEdit.getText().toString();
                int preference = prefSeekBar.getProgress();
                String type = clothingTypeSpinner.getSelectedItem().toString();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", requestCode + " " + resultCode);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CAPTURE:
                case GALLERY_PHOTO:
                    lastIntentReceived = requestCode;
                    picUri = data.getData();
                    performImageCrop();
                    break;
                case PICTURE_CROP:
                    Bundle extras = data.getExtras();
                    clothingImage = extras.getParcelable("data");
                    clothingImageView.setImageBitmap(clothingImage);
                    if(lastIntentReceived == CAMERA_CAPTURE)
                        getContentResolver().delete(picUri, null, null);
                    break;
            }
        }
    }

    private void performImageCrop() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(picUri, "image/*");

        cropIntent.putExtra("crop", "true");

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);

        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);

        cropIntent.putExtra("return-data", true);

        startActivityForResult(cropIntent, PICTURE_CROP);
    }

}
