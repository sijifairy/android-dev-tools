package com.lizhe.devtools.accessibility.action;

import android.util.JsonReader;
import android.util.SparseArray;

import com.lizhe.devtools.accessibility.BaseJsonInfo;

import java.io.IOException;

public class ActionInfoData extends BaseJsonInfo {

    public int mVersion = -1;
    public SparseArray<ActionItem> mActionMap;

    @Override
    public boolean parseItem(String str, JsonReader jsonReader) {
        try {
            if ("version".equals(str)) {
                mVersion = jsonReader.nextInt();
            } else if ("action_items".equals(str)) {
                jsonReader.beginArray();
                SparseArray<ActionItem> sparseArray = new SparseArray<>();
                while (jsonReader.hasNext()) {
                    ActionItem actionItem = new ActionItem().startJsonParse(jsonReader);
                    sparseArray.put(actionItem.mId, actionItem);
                }
                mActionMap = sparseArray;
                jsonReader.endArray();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return "{ AccessibilityInfo : version = " + this.mVersion + " map = " + this.mActionMap + " }";
    }
}