package com.lizhe.devtools.accessibility;

import android.util.JsonReader;

import java.io.IOException;

public abstract class BaseJsonInfo {

    public final <E extends BaseJsonInfo> E startJsonParse(JsonReader jsonReader) {
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                if (!parseItem(jsonReader.nextName(), jsonReader)) {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

            afterParse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (E) this;
    }

    public abstract boolean parseItem(String str, JsonReader jsonReader);

    public void afterParse() {
    }
}