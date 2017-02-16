package com.lizhe.devtools.accessibility.process;

import android.util.SparseArray;


public class ProcessInfoData {
    int mVersion = -1;
    public SparseArray<ProcessItem> mProcessMap;

    public String toString() {
        return "{ ProcessInfoData : version = " + mVersion + " map = " + mProcessMap + " }";
    }
}