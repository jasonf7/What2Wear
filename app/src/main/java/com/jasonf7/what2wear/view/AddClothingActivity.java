package com.jasonf7.what2wear.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jasonf7.what2wear.MainActivity;
import com.jasonf7.what2wear.R;
import com.jasonf7.what2wear.database.Clothing;
import com.jasonf7.what2wear.database.ClothingContract;
import com.jasonf7.what2wear.database.ClothingDbHelper;
import com.jasonf7.what2wear.database.DBManager;

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

    private int sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        sender = getIntent().getIntExtra("sender", MainActivity.ADD_CLOTHING);

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

                Clothing clothing = new Clothing(-1, name, description, preference, type, clothingImage);

                ContentValues values = new ContentValues();
                values.put(ClothingContract.ClothingEntry.COLUMN_NAME_NAME, clothing.getName());
                values.put(ClothingContract.ClothingEntry.COLUMN_NAME_DESCRIPTION, clothing.getDescription());
                values.put(ClothingContract.ClothingEntry.COLUMN_NAME_PREFERENCE, clothing.getPreference());
                values.put(ClothingContract.ClothingEntry.COLUMN_NAME_TYPE, clothing.getType());
                values.put(ClothingContract.ClothingEntry.COLUMN_NAME_IMAGE, clothing.toByteArray());

                if(sender == MainActivity.ADD_CLOTHING) {
                    SQLiteDatabase db = DBManager.getWriteDB();

                    long newRowID = db.insert(ClothingContract.ClothingEntry.TABLE_NAME, "null", values);
                    clothing.setID(newRowID);
                } else if(sender == MainActivity.EDIT_CLOTHING) {
                    Clothing oldClothing = getIntent().getParcelableExtra("clothing");
                    SQLiteDatabase db = DBManager.getReadDB();

                    String selection = ClothingContract.ClothingEntry._ID + " LIKE ?";
                    String[] selectionArgs = { String.valueOf(oldClothing.getID()) };

                    int count = db.update(ClothingContract.ClothingEntry.TABLE_NAME, values, selection, selectionArgs);
                    Log.d("DEBUG", "Update " + oldClothing.getID() + ": " + count);

                    clothing.setID(oldClothing.getID());
                }

                Intent intent = new Intent();
                intent.putExtra("newClothing", clothing);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if(sender == MainActivity.EDIT_CLOTHING){
            Clothing clothing = getIntent().getParcelableExtra("clothing");

            nameEdit.setText(clothing.getName());
            clothingImage = clothing.getImage();
            clothingImageView.setImageBitmap(clothing.getImage());
            descEdit.setText(clothing.getDescription());
            prefSeekBar.setProgress(clothing.getPreference());
            clothingTypeSpinner.setSelection(((ArrayAdapter)clothingTypeSpinner.getAdapter()).getPosition(clothing.getType()));
        }
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
