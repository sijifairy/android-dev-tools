package com.lizhe.devtools.accessibility;

import android.content.Intent;

public interface IExecuteCallback {

    void onFailed(int failCode);

    void onSucceeded();

    void onDeviceAdminAction(Intent intent);
}