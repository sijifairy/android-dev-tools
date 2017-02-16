package com.lizhe.devtools.accessibility.node;

import android.content.pm.ApplicationInfo;
import android.util.JsonReader;
import android.util.Log;

import com.lizhe.devtools.accessibility.BaseJsonInfo;
import com.lizhe.devtools.DevToolsApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseNodeInfo extends BaseJsonInfo {

    public String mIdName;
    public List<String> mFindTextList;
    public String mClassName;

    @Override
    public boolean parseItem(String str, JsonReader jsonReader) {
        try {
            if ("id_name".equals(str)) {
                mIdName = jsonReader.nextString();
            } else if ("find_texts".equals(str)) {
                jsonReader.beginArray();
                List<String> arrayList = new ArrayList<>();
                while (jsonReader.hasNext()) {
                    String findText = jsonReader.nextString();
                    if ("AppName".equals(findText)) {
                        ApplicationInfo applicationInfo = DevToolsApplication.getContext().getApplicationInfo();
                        int stringId = applicationInfo.labelRes;
                        findText = (stringId == 0) ? applicationInfo.nonLocalizedLabel.toString()
                                : DevToolsApplication.getContext().getString(stringId);
                        Log.i("BaseNodeInfo", "AppName replaced with " + findText);
                    }
                    arrayList.add(findText);
                }
                mFindTextList = arrayList;
                jsonReader.endArray();
            } else if ("class_name".equals(str)) {
                mClassName = jsonReader.nextString();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String toString() {
        return "{ ActionItem : idName = " + mIdName + " findTextList = " + mFindTextList + " className = " + mClassName + " }";
    }
}
