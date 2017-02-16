package com.lizhe.devtools.accessibility.intent;

import android.util.SparseArray;

public class IntentInfoData {
    public int mVersion = -1;
    public SparseArray<IntentItem> mIntentMap;

    public String toString() {
        return "{ IntentInfoData : version = " + this.mVersion + " map = " + this.mIntentMap + " }";
    }
}