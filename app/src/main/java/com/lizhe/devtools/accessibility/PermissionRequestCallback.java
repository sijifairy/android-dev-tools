package com.lizhe.devtools.accessibility;

import android.content.Intent;

public interface PermissionRequestCallback {

    void onStarted();

    // 第index个权限获取开始执行
    void onSinglePermissionStarted(int index);

    // 第index个权限获取执行完毕，是否成功
    void onSinglePermissionFinished(int index, boolean isSucceed);

    // 如果需要自动获取设备管理器权限，需要把自己定义的DeviceAdminReceiver
    // 设置到intent的extra里，然后startActivity。详见demo
    void onDeviceAdminAction(Intent intent);

    void onFinished(int succeedCount, int totalCount);

    void onCancelled();

    //存根类，HSPermissionRequestCallback的空实现
    class Stub implements PermissionRequestCallback {

        @Override
        public void onStarted() {

        }

        @Override
        public void onSinglePermissionStarted(int index) {

        }

        @Override
        public void onSinglePermissionFinished(int index, boolean isSucceed) {

        }

        @Override
        public void onDeviceAdminAction(Intent intent) {

        }

        @Override
        public void onFinished(int succeedCount, int totalCount) {

        }

        @Override
        public void onCancelled() {

        }
    }
}
