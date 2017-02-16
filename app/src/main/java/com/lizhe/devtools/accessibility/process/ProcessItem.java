package com.lizhe.devtools.accessibility.process;

import java.util.List;

public class ProcessItem {
    int mId = -1;
    public int mIntentId = -1;
    public List<Integer> mActionIdList;

    public String toString() {
        return "{ ProcessItem : id = " + this.mId + " intentId = " + this.mIntentId + " actionIdList = " + this.mActionIdList + " }";
    }
}