package com.lizhe.devtools.accessibility.rules;

import android.util.Log;

import com.lizhe.devtools.accessibility.PermissionItem;
import com.lizhe.devtools.accessibility.PermissionType;
import com.lizhe.devtools.accessibility.rom.RomInfoData;
import com.lizhe.devtools.accessibility.rom.RomInfoManager;

import java.util.ArrayList;
import java.util.List;

public class RuleManager {

    private static final String TAG = RuleManager.class.getSimpleName();

    public static ArrayList<PermissionItem> getPermissionItems(List<RuleItem> ruleItems,
                                                               RomInfoData romInfoData,
                                                               List<String> permissionValues) {
        if (ruleItems == null) {
            throw new NullPointerException("RuleItems can not be null!");
        } else if (romInfoData == null) {
            throw new NullPointerException("RomInfoData can not be null!");
        }

        ArrayList<PermissionItem> permissionItems = new ArrayList<>();
        // 按照适配文件找到对应的权限项
        for (RuleItem ruleItem : ruleItems) {
            Log.i(TAG, "match rule by romKey " + ruleItem.rom);
            if (ruleMatched(romInfoData, ruleItem.rom)) {
                Log.i(TAG, "match success!");
                if (permissionValues.contains(String.valueOf(ruleItem.type))) {
                    permissionValues.remove(String.valueOf(ruleItem.type));

                    PermissionItem permissionItem = new PermissionItem();
                    permissionItem.mTitle = ruleItem.title;
                    permissionItem.mProcessId = ruleItem.processId;
                    permissionItem.mIsEnabled = true;
                    permissionItem.mPermissionType = ruleItem.type;
                    permissionItem.mPriority = ruleItem.priority;
                    permissionItems.add(permissionItem);
                } else {
                    Log.i(TAG, "skip this permission!");
                }
            } else {
                Log.i(TAG, "match fail!");
            }
        }
        // 适配文件未找到，添加默认权限项
        if (permissionValues.size() > 0) {
            for (String type : permissionValues) {
                PermissionItem defaultItem = getDefaultPermissionItemByType(PermissionType.valueOf(Integer.parseInt(type)));
                if (defaultItem.mProcessId > 0) {
                    permissionItems.add(defaultItem);
                }
            }
        }
        return permissionItems;
    }

    private static boolean ruleMatched(RomInfoData romInfoData, int romKey) {
        if (romKey == 0) {
            Log.d(TAG, "ruleMatch romKey is invalid");
            return false;
        }

        return RomInfoManager.matchRomInfo(romInfoData, romKey);
    }

    private static PermissionItem getDefaultPermissionItemByType(PermissionType type) {
        PermissionItem permissionItem = new PermissionItem();
        switch (type) {
            case TYPE_LAYOUT_BOUNDS:
                permissionItem.mProcessId = 101;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_GPU_OVERDRAW_ON:
                permissionItem.mProcessId = 201;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_GPU_OVERDRAW_OFF:
                permissionItem.mProcessId = 202;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_FORCE_RTL:
                permissionItem.mProcessId = 401;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_GPU_PROFILING_BAR:
                permissionItem.mProcessId = 501;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_GPU_PROFILING_OFF:
                permissionItem.mProcessId = 502;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_STAY_AWAKE:
                permissionItem.mProcessId = 701;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_SHOW_CPU_USAGE:
                permissionItem.mProcessId = 801;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_KILL_ACTIVITY:
                permissionItem.mProcessId = 901;
                permissionItem.mIsEnabled = true;
                break;
            case TYPE_WAIT_DEBUGGER:
                permissionItem.mProcessId = 1001;
                permissionItem.mIsEnabled = true;
                break;
        }

        return permissionItem;
    }
}