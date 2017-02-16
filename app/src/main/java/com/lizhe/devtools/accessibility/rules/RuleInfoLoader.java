package com.lizhe.devtools.accessibility.rules;

import android.util.JsonReader;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RuleInfoLoader {

    private static final String PERMISSION_RULES_CONFIG_JSON = "permission/rules_config.json";

    public static List<RuleItem> loadData() {
        JsonReader jsonReader = Utils.readJson(DevToolsApplication.getContext(), PERMISSION_RULES_CONFIG_JSON);
        List<RuleItem> ruleItems = new ArrayList<>();
        if (jsonReader == null) {
            return null;
        }
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if ("version".equals(nextName)) {
                    jsonReader.nextInt();
                } else if ("rule_items".equals(nextName)) {
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        RuleItem ruleItem = new RuleItem();
                        while (jsonReader.hasNext()) {
                            String nextName2 = jsonReader.nextName();
                            if ("rom".equals(nextName2)) {
                                ruleItem.rom = jsonReader.nextInt();
                            } else if ("app".equals(nextName2)) {
                                ruleItem.app = jsonReader.nextInt();
                            } else if ("process_id".equals(nextName2)) {
                                ruleItem.processId = jsonReader.nextInt();
                            } else if ("title".equals(nextName2)) {
                                ruleItem.title = jsonReader.nextString();
                            } else if ("type".equals(nextName2)) {
                                ruleItem.type = jsonReader.nextInt();
                            } else if ("priority".equals(nextName2)) {
                                ruleItem.priority = jsonReader.nextInt();
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                        ruleItems.add(ruleItem);
                    }
                    jsonReader.endArray();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
            return ruleItems;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
