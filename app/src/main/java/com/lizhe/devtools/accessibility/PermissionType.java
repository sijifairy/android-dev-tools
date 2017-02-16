package com.lizhe.devtools.accessibility;

import android.util.SparseArray;

public enum PermissionType {
    TYPE_LAYOUT_BOUNDS(1),
    TYPE_GPU_OVERDRAW_ON(2),
    TYPE_GPU_OVERDRAW_OFF(3),
    TYPE_FORCE_RTL(4),
    TYPE_GPU_PROFILING_BAR(5),
    TYPE_GPU_PROFILING_OFF(6),
    TYPE_STAY_AWAKE(7),
    TYPE_SHOW_CPU_USAGE(8),
    TYPE_KILL_ACTIVITY(9),
    TYPE_WAIT_DEBUGGER(10);

    private int value;

    private static SparseArray<PermissionType> sArray = new SparseArray<>();

    PermissionType(final int value) {
        this.value = value;
    }

    static {
        for (PermissionType type : PermissionType.values()) {
            sArray.put(type.value, type);
        }
    }

    public static PermissionType valueOf(int value) {
        return sArray.get(value);
    }

    public int getValue() {
        return value;
    }
}
