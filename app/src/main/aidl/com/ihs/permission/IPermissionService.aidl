package com.ihs.permission;

import com.ihs.permission.IPermissionRequestListener;

interface IPermissionService {

    void startPermissionRequest(in List<String> permissionValues, in IPermissionRequestListener listener);

    void cancelPermissionRequest();
}
