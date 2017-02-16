package com.lizhe.devtools.accessibility.intent;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

public class IntentItem {

    public int mId = -1;
    public String mAction;
    public String mActivity;
    public String mPkgName;
    public String mData;
    public String mExtra;

    public Intent getIntent() {
        Intent intent = new Intent(mAction);
        if (mActivity != null) {
            intent.setComponent(new ComponentName(mPkgName, mActivity));
        }
        intent.setPackage(mPkgName);
        if (mData != null) {
            intent.setData(Uri.parse(mData));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        putExtra(intent);
        return intent;
    }

    private void putExtra(Intent intent) {
        if (mExtra != null && !mExtra.isEmpty()) {
            String key = getKey();
            String value = getValue();
            if (!key.isEmpty() && !value.isEmpty()) {
                intent.putExtra(key, value);
            }
        }
    }

    private String getKey() {
        try {
            return mExtra.substring(0, mExtra.indexOf("="));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getValue() {
        try {
            return mExtra.substring(mExtra.indexOf("=") + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return "{ IntentItem : id = " + mId + " action = " + mAction + " activity = "
                + mActivity + " pkgName = " + mPkgName + " data = " + mData + " extra = " + mExtra + " }";
    }
}