package com.lizhe.devtools.accessibility.action;

import android.util.JsonReader;

import com.lizhe.devtools.accessibility.node.BaseNodeInfo;
import com.lizhe.devtools.accessibility.node.CheckNodeInfo;
import com.lizhe.devtools.accessibility.node.IdentifyNodeInfo;
import com.lizhe.devtools.accessibility.node.LocateNodeInfo;
import com.lizhe.devtools.accessibility.node.OperationNodeInfo;
import com.lizhe.devtools.accessibility.node.ScrollNodeInfo;

import java.io.IOException;

public class ActionItem extends BaseNodeInfo {

    public int mId = -1;
    public IdentifyNodeInfo mIdentifyNodeInfo;
    public LocateNodeInfo mLocateNodeInfo;
    public ScrollNodeInfo mScrollNodeInfo;
    public CheckNodeInfo mCheckNodeInfo;
    public OperationNodeInfo mOperationNodeInfo;
    public boolean mNeedWaitWindow = true;
    public int mNeedWaitTime;

    public boolean parseItem(String str, JsonReader jsonReader) {
        try {
            if ("id".equals(str)) {
                mId = jsonReader.nextInt();
            } else if ("need_wait_window".equals(str)) {
                mNeedWaitWindow = jsonReader.nextBoolean();
            } else if ("need_wait_time".equals(str)) {
                mNeedWaitTime = jsonReader.nextInt();
            } else if ("identify_node".equals(str)) {
                mIdentifyNodeInfo = new IdentifyNodeInfo().startJsonParse(jsonReader);
            } else if ("locate_node".equals(str)) {
                mLocateNodeInfo = new LocateNodeInfo().startJsonParse(jsonReader);
            } else if ("scroll_node".equals(str)) {
                mScrollNodeInfo = new ScrollNodeInfo().startJsonParse(jsonReader);
            } else if ("check_node".equals(str)) {
                mCheckNodeInfo = new CheckNodeInfo().startJsonParse(jsonReader);
            } else if ("operation_node".equals(str)) {
                mOperationNodeInfo = new OperationNodeInfo().startJsonParse(jsonReader);
            } else {
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return "{ ActionItem : id = " + mId + " locateNodeInfo = " + mLocateNodeInfo
                + " scrollNodeInfo = " + mScrollNodeInfo + " checkNodeInfo = "
                + mCheckNodeInfo + " operationNodeInfo = " + mOperationNodeInfo + " }";
    }
}