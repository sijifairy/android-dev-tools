package com.lizhe.devtools.accessibility;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.action.ActionItem;
import com.lizhe.devtools.accessibility.node.CheckNodeInfo;
import com.lizhe.devtools.accessibility.node.LocateNodeInfo;
import com.lizhe.devtools.accessibility.node.ScrollNodeInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Utils {

    private static final String TAG = ActionExecutor.class.getSimpleName();

    private static final int MSG_CHECK = 1;
    private static Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CHECK) {
                sHandler.removeCallbacksAndMessages(null);
                if (isAccessibilityGranted()) {
                    broughtAppToFront(false);
                    sHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (sRunAfter != null) {
                                sRunAfter.run();
                                sRunAfter = null;
                            }
                        }
                    }, 1000);
                    return;
                }
                sHandler.sendEmptyMessageDelayed(1000, MSG_CHECK);
            }
        }
    };

    private static Runnable sRunAfter;

    public static void broughtAppToFront(boolean isAnimatable) {
        Intent intent = DevToolsApplication.getContext().getPackageManager().
                getLaunchIntentForPackage(DevToolsApplication.getContext()
                        .getPackageName());
        if (intent == null) {
            return;
        }

        if (!isAnimatable) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setPackage(null);//neccessary
        DevToolsApplication.getContext().startActivity(intent);
    }

    public static void doThingsWithAccessibilityCheck(Runnable runAfter) {
        if (isAccessibilityGranted()) {
            runAfter.run();
            return;
        }

        gotoAccessibility();
        sHandler.sendEmptyMessageDelayed(1000, MSG_CHECK);
        sRunAfter = runAfter;
    }

    private static void gotoAccessibility() {
        if (!isAccessibilityGranted()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }

            DevToolsApplication.getContext().startActivity(intent);
        }
    }

    public static JsonReader readJson(Context context, String str) {
        try {
            return new JsonReader(new InputStreamReader(context.getResources().getAssets().open(str), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Handler getValidHandler(Handler callBackHandler) {
        if (callBackHandler == null) {
            callBackHandler = null == Looper.myLooper() ? new Handler(Looper.getMainLooper()) : new Handler(Looper.myLooper());
        }

        return callBackHandler;
    }

    public static boolean isAccessibilityGranted() {
        boolean isGranted = false;
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(DevToolsApplication.getContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(DevToolsApplication.getContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                isGranted = services.toLowerCase().contains(DevToolsApplication.getContext().getPackageName().toLowerCase());
            }
        }
        return isGranted;
    }

    public static boolean isUsageAccessGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        boolean granted = false;
        try {
            PackageManager packageManager = DevToolsApplication.getContext().getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            granted = list.isEmpty();
            if (!granted) {
                AppOpsManager appOps = (AppOpsManager) DevToolsApplication.getContext().getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps
                        .checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), DevToolsApplication.getContext().getPackageName());
                granted = mode == AppOpsManager.MODE_ALLOWED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return granted;
    }

    public static boolean isNotificationListeningGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return true;
        }

        String listenerString = Settings.Secure.getString(DevToolsApplication.getContext().getContentResolver(), "enabled_notification_listeners");
        if (TextUtils.isEmpty(listenerString)) {
            return false;
        }
        final String[] listeners = listenerString.split(":");
        for (String listener : listeners) {
            if (listener.contains(DevToolsApplication.getContext().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static AccessibilityNodeInfo findNodeByLocateInfo(AccessibilityNodeInfo rootNode, LocateNodeInfo locateNodeInfo) {
        AccessibilityNodeInfo node = findNodeByTextInScreen(rootNode, locateNodeInfo.mFindTextList);
        if (node == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            node = findNodeByIdAndClass(rootNode, locateNodeInfo.mIdName, locateNodeInfo.mClassName);
        }
        return node;
    }

    public static AccessibilityNodeInfo findNodeByScrollInfo(AccessibilityNodeInfo rootNode, ScrollNodeInfo scrollNodeInfo) {
        AccessibilityNodeInfo scrollNode = null;
        if (scrollNodeInfo.mClassName != null) {
            LinkedList<AccessibilityNodeInfo> linkedList = new LinkedList<>();
            linkedList.addLast(rootNode);
            while (linkedList.size() > 0) {
                AccessibilityNodeInfo currentRoot = linkedList.removeFirst();
                if (currentRoot != null) {
                    int i;
                    if (currentRoot.getClassName().equals(scrollNodeInfo.mClassName)) {
                        if (!isAccessibilityNodeInScreen(currentRoot)) {
                            if (currentRoot.getChildCount() != 0) {
                                for (i = 0; i < currentRoot.getChildCount(); i++) {
                                    linkedList.addLast(currentRoot.getChild(i));
                                }
                                currentRoot = scrollNode;
                            }
                        }
                        scrollNode = currentRoot;
                    } else if (currentRoot.getChildCount() != 0) {
                        for (i = 0; i < currentRoot.getChildCount(); i++) {
                            linkedList.addLast(currentRoot.getChild(i));
                        }
                    }
                }
            }
            if (scrollNode == null) {
                Log.i(TAG, "scrollNode == null");
            }
        }
        return scrollNode;
    }

    public static AccessibilityNodeInfo findNodeByCheckInfo(AccessibilityNodeInfo locateNode, CheckNodeInfo checkNodeInfo) {
        AccessibilityNodeInfo checkNode = null;
        AccessibilityNodeInfo parent = locateNode.getParent();
        if (parent == null) {
            Log.i(TAG, "getCheckNode error-1");
            return null;
        }
        String str = checkNodeInfo.mCorrectText;
        int i = checkNodeInfo.mCorrectTextIndex;
        if (str == null || i < 0) {
            List<AccessibilityNodeInfo> linkedList = new LinkedList<>();
            for (int i2 = 0; i2 < parent.getChildCount(); i2++) {
                AccessibilityNodeInfo child = parent.getChild(i2);
                if (child != null) {
                    if (checkNodeInfo.mClassName != null && child.getClassName().equals(checkNodeInfo.mClassName)) {
                        checkNode = child;
                        break;
                    } else if (child.isCheckable()) {
                        linkedList.add(child);
                    }
                }
            }
            if (checkNode != null || linkedList.size() != 1) {
                return checkNode;
            }
            Log.i(TAG, "permission_executor getCheckNode 兼容方案");
            return linkedList.get(0);
        }
        try {
            checkNode = parent.getChild(i);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "correctNode=" + checkNode);
        return checkNode;
    }

    public static boolean isCheckNodeCorrect(AccessibilityNodeInfo checkNode, CheckNodeInfo checkNodeInfo) {
        if (checkNode.isCheckable()) {
            return checkNode.isChecked() == checkNodeInfo.mCorrectStatus;
        } else if (checkNodeInfo.mCorrectText != null && checkNodeInfo.mCorrectTextIndex >= 0) {
            return checkNodeInfo.mCorrectText.equals(checkNode.getText());
        } else {
            Log.i(TAG, "checkNode is not checkable");
            return false;
        }
    }

    public static AccessibilityNodeInfo findBehaviorNode(AccessibilityNodeInfo accessibilityNodeInfo, ActionItem actionItem) {
        for (AccessibilityNodeInfo node = accessibilityNodeInfo; node != null; node = node.getParent()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child.isClickable() && findNodeByTextInScreen(child, actionItem.mLocateNodeInfo.mFindTextList) != null) {
                    return child;
                }
            }
            if (node.isClickable()) {
                return node;
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByTextInScreen(AccessibilityNodeInfo rootNode, List<String> findTextList) {
        if (findTextList == null) {
            return null;
        }
        for (String findText : findTextList) {
            List<AccessibilityNodeInfo> childInfoList = rootNode.findAccessibilityNodeInfosByText(findText);
            if (childInfoList != null && childInfoList.size() > 0) {
                for (AccessibilityNodeInfo nodeInfo : childInfoList) {
                    if (isAccessibilityNodeInScreen(nodeInfo)) {
                        return nodeInfo;
                    }
                }
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo findNodeByIdAndClass(AccessibilityNodeInfo rootNode, String idName, String className) {
        if (idName == null) {
            return null;
        }
        List<AccessibilityNodeInfo> childInfoList = rootNode.findAccessibilityNodeInfosByViewId(idName);
        if (childInfoList != null && childInfoList.size() > 0) {
            for (AccessibilityNodeInfo nodeInfo : childInfoList) {
                if (isAccessibilityNodeInScreen(nodeInfo) && nodeInfo.getClassName().equals(className)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public static boolean isAccessibilityNodeInScreen(AccessibilityNodeInfo accessibilityNodeInfo) {
        Rect rect = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(rect);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) DevToolsApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect screenRect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
        return screenRect.intersect(rect);
    }
}