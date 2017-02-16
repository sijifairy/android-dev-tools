package com.lizhe.devtools.accessibility.rom;


import android.util.JsonReader;
import android.util.Log;
import android.util.SparseArray;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.Utils;

import java.io.IOException;

public class RomInfoLoader {

    private static final String PERMISSION_ROM_INFO_DATA_JSON = "permission/rom_info_data.json";
    private static final String TAG = RomInfoLoader.class.getSimpleName();

    public static RomInfoData loadData() {
        JsonReader jsonReader = Utils.readJson(DevToolsApplication.getContext(), PERMISSION_ROM_INFO_DATA_JSON);

        if (jsonReader == null) {
            return null;
        }

        try {
            jsonReader.beginObject();
            RomInfoData romInfoData = new RomInfoData();
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if ("version".equals(nextName)) {
                    romInfoData.setVersion(jsonReader.nextInt());
                } else if ("rom_items".equals(nextName)) {
                    jsonReader.beginArray();
                    SparseArray<RomItem> sparseArray = new SparseArray<>();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        RomItem romItem = new RomItem();
                        while (jsonReader.hasNext()) {
                            String nextName2 = jsonReader.nextName();
                            if ("rom_id".equals(nextName2)) {
                                romItem.setId(jsonReader.nextInt());
                            } else if ("rom_name".equals(nextName2)) {
                                romItem.setName(jsonReader.nextString());
                            } else if ("feature_items".equals(nextName2)) {
                                jsonReader.beginArray();
                                FeatureInfo featureInfo = new FeatureInfo();
                                while (jsonReader.hasNext()) {
                                    jsonReader.beginObject();
                                    FeatureItem featureItem = new FeatureItem();
                                    while (jsonReader.hasNext()) {
                                        String nextName3 = jsonReader.nextName();
                                        if ("key".equals(nextName3)) {
                                            featureItem.setKey(jsonReader.nextString());
                                        } else if ("value".equals(nextName3)) {
                                            featureItem.setValue(jsonReader.nextString());
                                        } else if ("condition".equals(nextName3)) {
                                            featureItem.setCondition(jsonReader.nextString());
                                        } else {
                                            Log.i(TAG, "loadData parse ROM_FEATURE_ITEMS invalid name=" + nextName3 + " value=" + jsonReader.nextString());
                                        }
                                    }
                                    featureInfo.addFeatureItem(featureItem);
                                    jsonReader.endObject();
                                }
                                romItem.setFeatureInfo(featureInfo);
                                jsonReader.endArray();
                            } else {
                                Log.i(TAG, "loadData parse ROM_ITEMS invalid name=" + nextName2 + " value=" + jsonReader.nextString());
                            }
                        }
                        sparseArray.put(romItem.getId(), romItem);
                        jsonReader.endObject();
                        Log.i(TAG, "loadData romItem=" + romItem);
                    }
                    romInfoData.setRomMap(sparseArray);
                    jsonReader.endArray();
                } else {
                    Log.i(TAG, "loadData parse ROM_INFO_CONFIG_NAME invalid name=" + nextName + " value=" + jsonReader.nextString());
                }
            }
            jsonReader.endObject();
            jsonReader.close();
            Log.i(TAG, "loadData mRomInfoData=" + romInfoData);
            return romInfoData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
