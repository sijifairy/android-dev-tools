package com.lizhe.devtools.accessibility.action;

import android.util.JsonReader;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.Utils;

public class ActionInfoLoader {

    private static final String PERMISSION_ACTION_INFO_DATA_JSON = "permission/action_info_data.json";

    public static ActionInfoData loadData() {
        JsonReader jsonReader = Utils.readJson(DevToolsApplication.getContext(), PERMISSION_ACTION_INFO_DATA_JSON);
        ActionInfoData actionInfoData = new ActionInfoData();
        actionInfoData.startJsonParse(jsonReader);
        return actionInfoData;
    }
}
