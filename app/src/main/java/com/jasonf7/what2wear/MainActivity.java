package com.jasonf7.what2wear;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonf7.what2wear.database.Clothing;
import com.jasonf7.what2wear.database.ClothingContract;
import com.jasonf7.what2wear.database.DBManager;
import com.jasonf7.what2wear.view.AddClothingActivity;
import com.jasonf7.what2wear.view.ClothingFragment;
import com.jasonf7.what2wear.view.WeatherFragment;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    private Context context;

    private static final int ADD_CLOTHING = 1;

    private double latitude, longitude;

    private ArrayList<Clothing> clothingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG", "TEST");

        context = this;

        DBManager.initDB(context);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            Log.d("DEBUG", "Got cache location");
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } else {
            longitude = -1;
            latitude = -1;
        }

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return WeatherFragment.newInstance(latitude, longitude);
                    case 1:
                        return ClothingFragment.newInstance(clothingList);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        ViewPager mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mViewPager.setAdapter(pagerAdapter);

        getClothing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_add_clothing) {
            Intent intent = new Intent(this, AddClothingActivity.class);
            startActivityForResult(intent, ADD_CLOTHING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_CLOTHING) {
                clothingList.add((Clothing)data.getParcelableExtra("newClothing"));
            }
        }
    }

    private void getClothing() {
        clothingList = new ArrayList<>();

        SQLiteDatabase db = DBManager.getWriteDB();

        String[] projection = {
                ClothingContract.ClothingEntry._ID,
                ClothingContract.ClothingEntry.COLUMN_NAME_NAME,
                ClothingContract.ClothingEntry.COLUMN_NAME_DESCRIPTION,
                ClothingContract.ClothingEntry.COLUMN_NAME_TYPE,
                ClothingContract.ClothingEntry.COLUMN_NAME_PREFERENCE,
                ClothingContract.ClothingEntry.COLUMN_NAME_IMAGE
        };

        String sortOrder = ClothingContract.ClothingEntry.COLUMN_NAME_NAME + " COLLATE NOCASE ASC";

        Cursor c = db.query(
                ClothingContract.ClothingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while(c.moveToNext()){
            long id = c.getLong(c.getColumnIndexOrThrow(ClothingContract.ClothingEntry._ID));
            String name = c.getString(c.getColumnIndexOrThrow(ClothingContract.ClothingEntry.COLUMN_NAME_NAME));
            String desc = c.getString(c.getColumnIndexOrThrow(ClothingContract.ClothingEntry.COLUMN_NAME_DESCRIPTION));
            String type = c.getString(c.getColumnIndexOrThrow(ClothingContract.ClothingEntry.COLUMN_NAME_TYPE));
            int preference = c.getInt(c.getColumnIndexOrThrow(ClothingContract.ClothingEntry.COLUMN_NAME_PREFERENCE));
            byte[] imageBytes = c.getBlob(c.getColumnIndexOrThrow(ClothingContract.ClothingEntry.COLUMN_NAME_IMAGE));

            Clothing clothing = new Clothing(id, name, desc, preference, type, null);
            clothing.fromByteArray(imageBytes);

            clothingList.add(clothing);
        }
        c.close();
    }
}
