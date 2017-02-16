package com.lizhe.devtools.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lz on 17/1/14.
 */

public class CommonAccessibilityService extends AccessibilityService {

    interface IAccCallback {
        void onEvent(AccessibilityEvent event);
    }

    private List<IAccCallback> callbackList = new ArrayList<>();

    private static class SingletonHolder {
        private static CommonAccessibilityService INSTANCE;
    }

    public static CommonAccessibilityService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void registerEvent(IAccCallback callback) {
        callbackList.add(callback);
    }

    public void unregisterEvent(IAccCallback callback) {
        callbackList.remove(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SingletonHolder.INSTANCE = this;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        SingletonHolder.INSTANCE = this;
        try {
            AccessibilityEvent accessibilityEvent = AccessibilityEvent.obtain(event);
            for (IAccCallback accCallback : callbackList) {
                accCallback.onEvent(accessibilityEvent);
            }
        } catch (Exception e) {
            Log.e("CommonAcc", "err:" + e.getMessage());
        }
    }

    @Override
    public void onInterrupt() {

    }
}
