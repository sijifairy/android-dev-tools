package com.lizhe.devtools.accessibility.intent;

import android.util.JsonReader;
import android.util.SparseArray;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.Utils;

import java.io.IOException;

public class IntentInfoLoader {

    private static final String PERMISSION_INTENT_INFO_DATA_JSON = "permission/intent_info_data.json";

    public static IntentInfoData loadData() {
        JsonReader jsonReader = Utils.readJson(DevToolsApplication.getContext(), PERMISSION_INTENT_INFO_DATA_JSON);
        if (jsonReader == null) return null;

        try {
            jsonReader.beginObject();
            IntentInfoData intentInfoData = new IntentInfoData();
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if ("version".equals(nextName)) {
                    intentInfoData.mVersion = jsonReader.nextInt();
                } else if ("intent_items".equals(nextName)) {
                    jsonReader.beginArray();
                    SparseArray<IntentItem> sparseArray = new SparseArray<>();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        IntentItem intentItem = new IntentItem();
                        while (jsonReader.hasNext()) {
                            String nextName2 = jsonReader.nextName();
                            if ("id".equals(nextName2)) {
                                intentItem.mId = jsonReader.nextInt();
                            } else if ("action".equals(nextName2)) {
                                intentItem.mAction = jsonReader.nextString();
                            } else if ("activity".equals(nextName2)) {
                                intentItem.mActivity = jsonReader.nextString();
                            } else if ("package".equals(nextName2)) {
                                intentItem.mPkgName = jsonReader.nextString();
                            } else if ("data".equals(nextName2)) {
                                intentItem.mData = jsonReader.nextString();
                            } else if ("extra".equals(nextName2)) {
                                intentItem.mExtra = jsonReader.nextString();
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        if (intentItem.mId >= 0 && intentItem.mPkgName != null && (intentItem.mAction != null || intentItem.mActivity != null)) {
                            sparseArray.put(intentItem.mId, intentItem);
                        }
                        jsonReader.endObject();
                    }
                    jsonReader.endArray();
                    intentInfoData.mIntentMap = sparseArray;
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
            if (intentInfoData.mVersion < 0 || intentInfoData.mIntentMap == null || intentInfoData.mIntentMap.size() == 0) {
                return null;
            }
            return intentInfoData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
