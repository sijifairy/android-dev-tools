package com.lizhe.devtools.accessibility.rom;

class FeatureItem {

    private String mKey;
    private String mValue;
    private String mCondition;

    String getKey() {
        return this.mKey;
    }

    String getValue() {
        return this.mValue;
    }

    String getCondition() {
        return this.mCondition;
    }

    void setKey(String str) {
        this.mKey = str;
    }

    void setValue(String str) {
        this.mValue = str;
    }

    void setCondition(String str) {
        this.mCondition = str;
    }

    @Override
    public String toString() {
        return "{ FeatureItem : mKey = " + this.mKey + "; mValue = " + this.mValue + " ;mCondition = " + this.mCondition + " }";
    }
}