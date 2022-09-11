package com.example.myapplication.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SportsData implements Parcelable {
    public int dataId;
    public String userId;
    public int kcal;
    public float speed;
    public float count;
    public String courseName;
    public String startTime;
    public int duration;
    public SportsType sportsType;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(dataId);
        parcel.writeString(userId);
        parcel.writeInt(kcal);
        parcel.writeFloat(speed);
        parcel.writeFloat(count);
        parcel.writeString(courseName);
        parcel.writeString(startTime);
        parcel.writeInt(duration);
        //由于不需要读取SportsType，因此略过
    }

    public static final Parcelable.Creator<SportsData> CREATOR = new Parcelable.Creator<SportsData>(){

        @Override
        public SportsData createFromParcel(Parcel source){
            SportsData sportsData = new SportsData();
            sportsData.dataId = source.readInt();
            sportsData.userId = source.readString();
            sportsData.kcal = source.readInt();
            sportsData.speed = source.readFloat();
            sportsData.count = source.readFloat();
            sportsData.courseName = source.readString();
            sportsData.startTime = source.readString();
            sportsData.duration = source.readInt();
            return sportsData;
        }

        @Override
        public SportsData[] newArray(int size){
            return new SportsData[size];
        }

    };
}

