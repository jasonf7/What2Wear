package com.jasonf7.what2wear.view.clothing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonf7.what2wear.MainActivity;
import com.jasonf7.what2wear.R;
import com.jasonf7.what2wear.database.Clothing;
import com.jasonf7.what2wear.database.ClothingContract;
import com.jasonf7.what2wear.database.ClothingList;
import com.jasonf7.what2wear.database.DBManager;
import com.jasonf7.what2wear.view.AddClothingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by jasonf7 on 28/05/15.
 */
public class ClothingListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private LayoutInflater inflater;

    private HashMap<String, List<Clothing>> clothingMap;
    private List<String> positiontoType;
    private List<Clothing> clothingList;

    private int sortType;

    public ClothingListAdapter(Activity mContext, ClothingList mList, int mSortType){
        context = mContext;
        inflater = LayoutInflater.from(context);

        positiontoType = new ArrayList<>();

        sortType = mSortType;
        clothingList = mList.getList();

        switch (sortType){
            case Clothing.SORT_BY_TYPE:
                clothingMap = new HashMap<>();
                for(Clothing clothing : clothingList) {
                    String type = clothing.getType();
                    if(clothingMap.get(type) == null) {
                        List<Clothing> cList = new ArrayList<>();
                        clothingMap.put(type, cList);
                        positiontoType.add(type);
                    }
                    clothingMap.get(type).add(clothing);
                }
                break;
        }
    }

    @Override
    public int getGroupCount() {
        return clothingMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return clothingMap.get(positiontoType.get(groupPosition)).size();
    }

    @Override
    public List<Clothing> getGroup(int groupPosition) {
        return clothingMap.get(positiontoType.get(groupPosition));
    }

    @Override
    public Clothing getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.clothing_header, parent, false);

        int childCount = getChildrenCount(groupPosition);

        TextView headerText = (TextView) convertView.findViewById(R.id.headerText);
        headerText.setText(positiontoType.get(groupPosition) + " (" + childCount + " items)");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.clothing_cell, parent, false);

        final Clothing clothing = getChild(groupPosition, childPosition);

        TextView cellPreference = (TextView) convertView.findViewById(R.id.cellPreference);
        cellPreference.setText(clothing.getPreference() + "%");

        ImageView cellImage = (ImageView) convertView.findViewById(R.id.cellImage);
        cellImage.setImageBitmap(clothing.getImage());

        TextView cellName = (TextView) convertView.findViewById(R.id.cellName);
        cellName.setText(clothing.getName());

        View subMenu = convertView.findViewById(R.id.subMenuLayout);

        if(subMenu.getVisibility() == View.VISIBLE) {
            TextView descriptionText = (TextView) subMenu.findViewById(R.id.descriptionText);
            descriptionText.setText(clothing.getDescription());

            ImageButton editButton = (ImageButton) subMenu.findViewById(R.id.editClothingButton);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddClothingActivity.class);
                    intent.putExtra("sender", MainActivity.EDIT_CLOTHING);
                    intent.putExtra("clothing", clothing);
                    context.startActivityForResult(intent, MainActivity.EDIT_CLOTHING);
                }
            });

            ImageButton deleteButton = (ImageButton) subMenu.findViewById(R.id.deleteClothingButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = DBManager.getWriteDB();

                    String selection = ClothingContract.ClothingEntry._ID + " LIKE ?";
                    String[] selectionArgs = { String.valueOf(clothing.getID()) };

                    db.delete(ClothingContract.ClothingEntry.TABLE_NAME, selection, selectionArgs);

                    Intent intent = new Intent(context, DummyActivity.class);
                    intent.putExtra("clothing", clothing);
                    context.startActivityForResult(intent, MainActivity.DELETE_CLOTHING);
                }
            });

            subMenu.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
