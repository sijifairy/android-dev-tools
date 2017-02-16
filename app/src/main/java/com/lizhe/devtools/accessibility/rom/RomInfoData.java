package com.lizhe.devtools.accessibility.rom;

import android.util.SparseArray;

public class RomInfoData {

    private int mVersion;
    private SparseArray<RomItem> mRomMap;

    void setVersion(int version) {
        mVersion = version;
    }

    void setRomMap(SparseArray<RomItem> romMap) {
        mRomMap = romMap;
    }

    int getVersion() {
        return mVersion;
    }

    SparseArray<RomItem> getRomMap() {
        return mRomMap;
    }

    @Override
    public String toString() {
        return "{ FeatureInfo : mVersion = " + mVersion + " mRomMap = " + mRomMap + " }";
    }
}