package com.lizhe.devtools.accessibility.node;

import android.util.JsonReader;

import java.io.IOException;

public class CheckNodeInfo extends BaseNodeInfo {

    public boolean mCorrectStatus = true;
    public int mCorrectTextIndex;
    public String mCorrectText;

    @Override
    public boolean parseItem(String str, JsonReader jsonReader) {
        try {
            if ("correct_status".equals(str)) {
                mCorrectStatus = jsonReader.nextBoolean();
                return true;
            } else if ("correct_text".equals(str)) {
                mCorrectText = jsonReader.nextString();
                return true;
            } else if ("correct_text_index".equals(str)) {
                mCorrectTextIndex = jsonReader.nextInt();
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