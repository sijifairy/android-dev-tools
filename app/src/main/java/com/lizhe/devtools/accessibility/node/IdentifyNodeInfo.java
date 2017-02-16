package com.lizhe.devtools.accessibility.node;

import android.util.JsonReader;

import java.io.IOException;

public class IdentifyNodeInfo extends BaseNodeInfo {

    public boolean mAllowSkip;

    @Override
    public boolean parseItem(String str, JsonReader jsonReader) {
        try {
            if ("allow_skip".equals(str)) {
                this.mAllowSkip = jsonReader.nextBoolean();
                return true;
            } else {
                return super.parseItem(str, jsonReader);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}