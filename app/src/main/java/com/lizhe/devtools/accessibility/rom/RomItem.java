package com.lizhe.devtools.accessibility.rom;

class RomItem {

    private int mId;
    private String mName;
    private FeatureInfo mFeatureInfo;

    int getId() {
        return mId;
    }

    void setId(int id) {
        mId = id;
    }

    String getName() {
        return mName;
    }

    void setName(String name) {
        mName = name;
    }

    FeatureInfo getFeatureInfo() {
        return mFeatureInfo;
    }

    void setFeatureInfo(FeatureInfo featureInfo) {
        mFeatureInfo = featureInfo;
    }

    @Override
    public String toString() {
        return "{ RomItem : mId = " + mId + " mName = " + mName + " mFeatureInfo = " + mFeatureInfo + " }";
    }
}