package com.lizhe.devtools.accessibility;

import android.support.annotation.NonNull;

public class PermissionItem implements Comparable<PermissionItem> {

    public String mTitle;
    public int mProcessId;
    public boolean mIsEnabled;
    public int mPermissionType;
    public int mPriority;

    @Override
    public int compareTo(@NonNull PermissionItem permissionItem) {
        return permissionItem.mPriority - mPriority;
    }

    @Override
    public String toString() {
        return "PermissionItem{actionTitle='" + mTitle + '\'' + ", processId=" + mProcessId
                + ", isEnabled=" + mIsEnabled + ", permissionType=" + mPermissionType + ", priority=" + mPriority + '}';
    }
}