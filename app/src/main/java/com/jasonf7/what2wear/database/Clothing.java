package com.jasonf7.what2wear.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

/**
 * Created by jasonf7 on 26/05/15.
 */
public class Clothing implements Parcelable {
    private long id;

    private String name;
    private String description;
    private int preference;
    private String type;
    private Bitmap image;

    public Clothing(long mID, String mName, String mDesc, int mPref, String mType, Bitmap mImage) {
        id = mID;
        name = mName;
        description = mDesc;
        preference = mPref;
        type = mType;
        image = mImage;
    }

    public long getID() {
        return id;
    }

    public void setID(long mID) {
        id = mID;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String mDesc) {
        description = mDesc;
    }

    public int getPreference() {
        return preference;
    }

    public void setPreference(int mPref) {
        preference = mPref;
    }

    public String getType() {
        return type;
    }

    public void setType(String mType) {
        type = mType;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap mImage) {
        image = mImage;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public void fromByteArray(byte[] image) {
        setImage(BitmapFactory.decodeByteArray(image, 0, image.length));
    }

    public Clothing(Parcel source) {
        String[] data = new String[3];
        source.readStringArray(data);

        name = data[0];
        description = data[1];
        type = data[2];

        preference = source.readInt();

        byte[] imageByteArr = new byte[source.readInt()];
        source.readByteArray(imageByteArr);
        fromByteArray(imageByteArr);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.name,
                this.description,
                this.type});
        dest.writeInt(this.preference);

        byte[] imageBytes = toByteArray();
        dest.writeInt(imageBytes.length);
        dest.writeByteArray(imageBytes);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new Clothing(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Clothing[size];
        }
    };
}
