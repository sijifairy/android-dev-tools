package com.lizhe.devtools.accessibility.node;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OperationNodeInfo extends BaseNodeInfo {

    private static final Map<String, Integer> BEHAVIOR_MAP = new HashMap<>();

    static {
        BEHAVIOR_MAP.put("click", 16);
    }

    public int mBehaviorCode;
    private String mBehaviorName;

    @Override
    public boolean parseItem(String str, JsonReader jsonReader) {
        try {
            if ("behavior".equals(str)) {
                this.mBehaviorName = jsonReader.nextString();
                return true;
            } else {
                return super.parseItem(str, jsonReader);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterParse() {
        super.afterParse();

        if (BEHAVIOR_MAP.containsKey(this.mBehaviorName)) {
            this.mBehaviorCode = BEHAVIOR_MAP.get(this.mBehaviorName);
        } else {
            Log.d("OperationNodeInfo",getClass().getSimpleName() + " behavior 无法识别");
        }
    }
}