package com.jasonf7.what2wear.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonf7 on 28/05/15.
 */
public class ClothingList implements Parcelable {
    private List<Clothing> clothingList;

    public ClothingList() {
        clothingList = new ArrayList<>();
    }

    public List<Clothing> getList() {
        return clothingList;
    }

    public void setList(List<Clothing> mList) {
        clothingList = mList;
    }

    public ClothingList(Parcel in) {
        in.readTypedList(clothingList, Clothing.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(clothingList);
    }

    public static final Parcelable.Creator<ClothingList> CREATOR = new Parcelable.Creator<ClothingList>() {

        @Override
        public ClothingList createFromParcel(Parcel in) {
            return new ClothingList(in);
        }

        @Override
        public ClothingList[] newArray(int size) {
            return new ClothingList[size];
        }
    };
}
