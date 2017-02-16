package com.ihs.permission;

import android.content.Intent;

interface IPermissionRequestListener {

    void onCancelled(int succeedCount, int totalCount);

    void onFinished(int succeedCount, int totalCount);

    void onSinglePermissionStarted(int index);

    void onSinglePermissionFinished(int index, boolean isSucceed);

    void onDeviceAdminAction(inout Intent intent);
}
