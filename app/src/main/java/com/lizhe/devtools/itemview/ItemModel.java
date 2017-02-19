package com.lizhe.devtools.itemview;

import com.lizhe.devtools.accessibility.PermissionType;

/**
 * Created by lz on 2/18/17.
 */

public class ItemModel {
    int resIcon;
    int resTitle;
    boolean hasIcon;
    PermissionType mainType;
    PermissionType onType;
    PermissionType offType;

    public ItemModel(int resIcon, int resTitle, PermissionType mainType) {
        this.resIcon = resIcon;
        this.resTitle = resTitle;
        hasIcon = false;
        this.mainType = mainType;
    }

    public ItemModel(int resIcon, int resTitle, PermissionType onType, PermissionType offType) {
        this.resIcon = resIcon;
        this.resTitle = resTitle;
        hasIcon = true;
        this.onType = onType;
        this.offType = offType;
    }
}
