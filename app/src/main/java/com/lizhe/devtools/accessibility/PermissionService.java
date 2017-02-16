package com.lizhe.devtools.accessibility;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PermissionService extends Service {
    PermissionServiceImpl permissionService;

    @Override
    public void onCreate() {
        super.onCreate();
        permissionService = new PermissionServiceImpl();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionService = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return permissionService;
    }
}