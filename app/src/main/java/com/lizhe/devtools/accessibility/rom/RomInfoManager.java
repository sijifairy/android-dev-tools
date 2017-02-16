package com.lizhe.devtools.accessibility.rom;

import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class RomInfoManager {

    private static final String TAG = "RomInfoManager";
    private static final String EQUAL = "equal";
    private static final String GE = "ge";
    private static final String LE = "le";
    private static final String GREATER = "greater";
    private static final String NE = "ne";
    private static final String LESS = "less";
    private static final String CONTAIN = "contain";
    private static final String LFM = "lfm";
    private static final String RFM = "rfm";
    private static final String ID = "ID";
    private static final String DISPLAY = "DISPLAY";
    private static final String PRODUCT = "PRODUCT";
    private static final String DEVICE = "DEVICE";
    private static final String MANUFACTURER = "MANUFACTURER";
    private static final String BRAND = "BRAND";
    private static final String RELEASE = "RELEASE";
    private static final String SDK_INT = "SDK_INT";
    private static final String RO = "ro.";

    public static boolean matchRomInfo(RomInfoData romInfoData, int romId) {
        RomItem romItem = getRomInfoFromConfigFileById(romInfoData, romId);

        if (romItem == null) {
            Log.i(TAG, "matchRomInfo romItem  is null");
            return false;
        }
        FeatureInfo featureInfo = romItem.getFeatureInfo();
        if (featureInfo == null) {
            Log.i(TAG, "matchRomInfo featureInfo  is null");
            return false;
        }
        List<FeatureItem> featureItems = featureInfo.getFeatureItems();
        if (featureItems == null) {
            Log.i(TAG, "matchRomInfo feature items list is null");
            return false;
        }

        for (FeatureItem featureItem : featureItems) {
            if (!compareFeatureItem(featureItem)) {
                return false;
            }
        }
        Log.i(TAG, "matchRomInfo match success id=" + romId);
        return true;
    }

    private static boolean compareFeatureItem(FeatureItem featureItem) {
        if (featureItem == null) {
            Log.i(TAG, "compareFeatureItem featureItem  is null");
            return false;
        }
        if (featureItem.getKey().startsWith(RO)) {
            String systemProperties = getSystemPropertiesByKey(featureItem.getKey());
            if (compareFeatureKey(featureItem.getValue(), systemProperties, featureItem.getCondition())) {
                return true;
            }
            Log.i(TAG, "compareFeatureItem unmatch key=" + featureItem.getKey() + " configfile value=" + featureItem.getValue() + " configfile condition=" + featureItem.getCondition() + " system value=" + systemProperties);
            return false;
        }
        if (featureItem.getKey().equals(SDK_INT)) {
            int sdkInt = VERSION.SDK_INT;
            try {
                if (compareFeatureKey(Integer.parseInt(featureItem.getValue()), sdkInt, featureItem.getCondition())) {
                    return true;
                }
                Log.i(TAG, "compareFeatureItem unmatch key=" + featureItem.getKey() + " configfile value=" + featureItem.getValue() + " configfile conditioin=" + featureItem.getCondition() + " system value=" + sdkInt);
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            String systemBuildInfo = getSystemBuildInfo(featureItem.getKey());
            if (compareFeatureKey(featureItem.getValue(), systemBuildInfo, featureItem.getCondition())) {
                return true;
            }
            Log.i(TAG, "compareFeatureItem unmatch key=" + featureItem.getKey() + " configfile value=" + featureItem.getValue() + " configfile conditioin=" + featureItem.getCondition() + " system value=" + systemBuildInfo);
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private static boolean compareFeatureKey(String featureItemValue, String systemBuildInfo, String featureItemCondition) {
        if (TextUtils.isEmpty(featureItemValue) || TextUtils.isEmpty(systemBuildInfo) || TextUtils.isEmpty(featureItemCondition)) {
            return false;
        }
        if (featureItemCondition.equalsIgnoreCase(CONTAIN)) {
            return systemBuildInfo.contains(featureItemValue);
        }
        if (featureItemCondition.equalsIgnoreCase(EQUAL)) {
            return systemBuildInfo.equalsIgnoreCase(featureItemValue);
        }
        if (featureItemCondition.equalsIgnoreCase(LFM)) {
            return systemBuildInfo.contains(featureItemValue);
        }
        if (featureItemCondition.equalsIgnoreCase(NE)) {
            return !systemBuildInfo.contains(featureItemValue);
        }
        if (featureItemCondition.equalsIgnoreCase(RFM)) {
            return systemBuildInfo.lastIndexOf(featureItemValue) >= 0;
        }
        if (featureItemCondition.equalsIgnoreCase(GE)) {
            try {
                return Float.parseFloat(systemBuildInfo) >= Float.parseFloat(featureItemValue);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (featureItemCondition.equalsIgnoreCase(LE)) {
            try {
                return Float.parseFloat(systemBuildInfo) <= Float.parseFloat(featureItemValue);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        Log.i(TAG, "compareFeatureKey Illegal conditions for Strings");
        return false;
    }

    private static boolean compareFeatureKey(int ruleInt, int currentSdkInt, String ruleCondition) {
        if (ruleCondition.isEmpty()) {
            Log.i(TAG, "compareFeatureKey condition is null");
            return false;
        }
        if (ruleCondition.equals(NE)) {
            return currentSdkInt != ruleInt;
        }
        if (ruleCondition.equals(EQUAL)) {
            return currentSdkInt == ruleInt;
        }
        if (ruleCondition.equals(GE)) {
            return currentSdkInt >= ruleInt;
        }
        if (ruleCondition.equals(GREATER)) {
            return currentSdkInt > ruleInt;
        }
        if (ruleCondition.equals(LE)) {
            return currentSdkInt <= ruleInt;
        }
        if (ruleCondition.equals(LESS)) {
            return currentSdkInt < ruleInt;
        }

        Log.i(TAG, "compareFeatureKey Illegal conditions for Integer");
        return false;
    }

    private static RomItem getRomInfoFromConfigFileById(RomInfoData romInfoData, int romId) {
        if (romInfoData == null) {
            Log.i(TAG, "getRomInfoFromConfigFileById romInfoData is null");
            return null;
        }
        RomItem romItem = romInfoData.getRomMap().get(romId);
        if (romItem == null) {
            Log.i(TAG, "getRomInfoFromConfigFileById no found romitem id=" + romId);
            return null;
        }
        Log.i(TAG, "getRomInfoFromConfigFileById  romitem romItem=" + romItem);
        return romItem;
    }

    private static String getSystemPropertiesByKey(String key) {
        if (key.isEmpty()) {
            Log.i(TAG, "getSystemPropertiesByKey  the key is empty");
        }
        if (!key.startsWith(RO)) {
            Log.i(TAG, "getSystemProperties  the key is not SystemProperty key=" + key);
            return "";
        }

        // TODO: 2017/1/3  怎么弄这个SystemProperties
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getDeclaredMethod("get", new Class[]{String.class}).invoke(cls.newInstance(), new Object[]{key});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getSystemBuildInfo(String key) {
        if (key.isEmpty()) {
            Log.i(TAG, "getSystemBuildInfo  the key is empty");
            return "";
        }
        String condition;
        switch (key) {
            case BRAND:
                condition = Build.BRAND;
                break;
            case DEVICE:
                condition = Build.DEVICE;
                break;
            case DISPLAY:
                condition = Build.DISPLAY;
                break;
            case ID:
                condition = Build.ID;
                break;
            case MANUFACTURER:
                condition = Build.MANUFACTURER;
                break;
            case RELEASE:
                condition = VERSION.RELEASE;
                break;
            case SDK_INT:
                condition = String.valueOf(VERSION.SDK_INT);
                break;
            case PRODUCT:
                condition = Build.PRODUCT;
                break;
            default:
                Log.i(TAG, "getSystemBuildInfo  the key is not BUILD key=" + key);
                return "";
        }
        Log.i(TAG, "getSystemBuildInfo KEY=" + key + " VALUE=" + condition);
        return condition;
    }
}