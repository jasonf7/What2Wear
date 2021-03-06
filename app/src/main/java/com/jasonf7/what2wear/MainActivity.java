package com.jasonf7.what2wear;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonf7.what2wear.database.Clothing;
import com.jasonf7.what2wear.database.ClothingContract;
import com.jasonf7.what2wear.database.ClothingList;
import com.jasonf7.what2wear.database.DBManager;
import com.jasonf7.what2wear.view.AddClothingActivity;
import com.jasonf7.what2wear.view.clothing.ClothingFragment;
import com.jasonf7.what2wear.view.WeatherFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {
    private Context context;

    public static final int ADD_CLOTHING = 1;
    public static final int EDIT_CLOTHING = 2;
    public static final int DELETE_CLOTHING = 3;

    private double latitude, longitude;

    private ClothingList clothingList;

    private FragmentStatePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG", "TEST");

        context = this;

        clothingList = new ClothingList();

        DBManager.initDB(context, new DBManager.OnInitializedListener() {
            @Override
            public void onInitialized() {
                getClothing();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pagerAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

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

        pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getItemPosition(Object object){
                return POSITION_NONE;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return WeatherFragment.newInstance(latitude, longitude, clothingList);
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
            intent.putExtra("sender", ADD_CLOTHING);
            startActivityForResult(intent, ADD_CLOTHING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_CLOTHING) {
                clothingList.getList().add((Clothing) data.getParcelableExtra("newClothing"));
                pagerAdapter.notifyDataSetChanged();
            } else if(requestCode == EDIT_CLOTHING) {
                Clothing clothing = data.getParcelableExtra("newClothing");
                List<Clothing> tempList = clothingList.getList();
                for(int i=0; i < tempList.size(); i++){
                    if(tempList.get(i).getID() == clothing.getID()){
                        tempList.set(i, clothing);
                        break;
                    }
                }
                clothingList.setList(tempList);
                pagerAdapter.notifyDataSetChanged();
            } else if(requestCode == DELETE_CLOTHING) {
                Clothing clothing = data.getParcelableExtra("newClothing");
                List<Clothing> tempList = clothingList.getList();
                int delIndex = 0;
                for(int i=0; i < tempList.size(); i++){
                    if(tempList.get(i).getID() == clothing.getID()){
                        delIndex = i;
                        break;
                    }
                }
                tempList.remove(delIndex);
                clothingList.setList(tempList);
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getClothing() {
        List<Clothing> tempList = new ArrayList<>();

        SQLiteDatabase db = DBManager.getReadDB();

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
            Log.d("DEBUG", name + ": " + id);
            clothing.fromByteArray(imageBytes);

            tempList.add(clothing);
        }
        c.close();

        clothingList.setList(tempList);
    }
}
