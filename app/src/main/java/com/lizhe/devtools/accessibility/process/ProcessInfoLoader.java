package com.lizhe.devtools.accessibility.process;

import android.util.JsonReader;
import android.util.SparseArray;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessInfoLoader {

    private static final String PERMISSION_PROCESS_INFO_DATA_JSON = "permission/process_info_data.json";

    public static ProcessInfoData loadData() {
        JsonReader jsonReader = Utils.readJson(DevToolsApplication.getContext(), PERMISSION_PROCESS_INFO_DATA_JSON);
        String nextName;

        if (jsonReader == null) return null;

        try {
            jsonReader.beginObject();
            ProcessInfoData processInfoData = new ProcessInfoData();
            while (jsonReader.hasNext()) {
                nextName = jsonReader.nextName();
                if ("version".equals(nextName)) {
                    processInfoData.mVersion = jsonReader.nextInt();
                } else if ("process_items".equals(nextName)) {
                    jsonReader.beginArray();
                    SparseArray<ProcessItem> sparseArray = new SparseArray<>();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        ProcessItem processItem = new ProcessItem();
                        while (jsonReader.hasNext()) {
                            String nextName2 = jsonReader.nextName();
                            if ("id".equals(nextName2)) {
                                processItem.mId = jsonReader.nextInt();
                            } else if ("intent_id".equals(nextName2)) {
                                processItem.mIntentId = jsonReader.nextInt();
                            } else if ("action_id".equals(nextName2)) {
                                jsonReader.beginArray();
                                List<Integer> arrayList = new ArrayList<>(3);
                                while (jsonReader.hasNext()) {
                                    arrayList.add(jsonReader.nextInt());
                                }
                                jsonReader.endArray();
                                processItem.mActionIdList = arrayList;
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                        if (processItem.mId >= 0 && processItem.mIntentId >= 0) {
                            sparseArray.put(processItem.mId, processItem);
                        }
                    }
                    jsonReader.endArray();
                    processInfoData.mProcessMap = sparseArray;
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();

            if (processInfoData.mVersion < 0 || processInfoData.mProcessMap == null || processInfoData.mProcessMap.size() == 0) {
                return null;
            }
            return processInfoData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
