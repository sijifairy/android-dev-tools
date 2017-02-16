package com.lizhe.devtools.accessibility;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.lizhe.devtools.accessibility.action.ActionInfoData;
import com.lizhe.devtools.accessibility.action.ActionInfoLoader;
import com.lizhe.devtools.accessibility.action.ActionItem;
import com.lizhe.devtools.accessibility.intent.IntentInfoData;
import com.lizhe.devtools.accessibility.intent.IntentInfoLoader;
import com.lizhe.devtools.accessibility.intent.IntentItem;
import com.lizhe.devtools.accessibility.process.ProcessInfoData;
import com.lizhe.devtools.accessibility.process.ProcessInfoLoader;
import com.lizhe.devtools.accessibility.process.ProcessItem;
import com.lizhe.devtools.accessibility.rom.RomInfoData;
import com.lizhe.devtools.accessibility.rom.RomInfoLoader;
import com.lizhe.devtools.accessibility.rules.RuleInfoLoader;
import com.lizhe.devtools.accessibility.rules.RuleItem;
import com.lizhe.devtools.accessibility.rules.RuleManager;

import java.util.ArrayList;
import java.util.List;

class ActionManager {

    private static final String TAG = ActionManager.class.getSimpleName();

    private List<RuleItem> mRuleItems;
    private RomInfoData mRomInfoData;
    private ActionInfoData mActionInfoData;
    private IntentInfoData mIntentInfoData;
    private ProcessInfoData mProcessInfoData;

    private ActionExecutor mExecutor;

    private ActionManager() {
        CommonAccessibilityService.getInstance().registerEvent(new CommonAccessibilityService.IAccCallback() {
            @Override
            public void onEvent(AccessibilityEvent accessibilityEvent) {
                onAccessibilityEvent(accessibilityEvent);
            }
        });
    }

    private static class Holder {
        static ActionManager instance = new ActionManager();

        private Holder() {
        }
    }

    public static ActionManager getInstance() {
        return Holder.instance;
    }

    void loadData() {
        mRuleItems = RuleInfoLoader.loadData();
        mRomInfoData = RomInfoLoader.loadData();
        mProcessInfoData = ProcessInfoLoader.loadData();
        mIntentInfoData = IntentInfoLoader.loadData();
        mActionInfoData = ActionInfoLoader.loadData();
    }

    ArrayList<PermissionItem> getPermissionItems(List<String> permissionValues) {
        return RuleManager.getPermissionItems(mRuleItems, mRomInfoData, permissionValues);
    }

    void executeProcess(int processId, final IExecuteCallback callback) {
        Log.i(TAG, "permission_executor executeProcess processId: " + processId);
        if (mActionInfoData == null || mIntentInfoData == null || mProcessInfoData == null) {
            callback.onFailed(ActionExecutor.FINISH_CODE_DATA_INCOMPLETE);
        } else {
            ProcessItem processItem = mProcessInfoData.mProcessMap.get(processId);
            if (processItem == null) {
                callback.onFailed(ActionExecutor.FINISH_CODE_PROCESS_INFO_ERROR);
                return;
            }
            IntentItem intentItem = mIntentInfoData.mIntentMap.get(processItem.mIntentId);
            if (intentItem == null) {
                callback.onFailed(ActionExecutor.FINISH_CODE_INTENT_INFO_ERROR);
                return;
            }
            ActionItem[] actionItemArr;
            if (processItem.mActionIdList == null || processItem.mActionIdList.size() == 0) {
                Log.i(TAG, "permission_executor actionIdList == null !! mode change to EXECUTE_MODE_ONLY_ENTER");
                actionItemArr = null;
            } else {
                actionItemArr = new ActionItem[processItem.mActionIdList.size()];
                for (int i = 0; i < actionItemArr.length; i++) {
                    ActionItem actionItem = mActionInfoData.mActionMap.get(processItem.mActionIdList.get(i));
                    if (actionItem == null) {
                        callback.onFailed(ActionExecutor.FINISH_CODE_ACTION_INFO_ERROR);
                        return;
                    }
                    actionItemArr[i] = actionItem;
                }
            }

            mExecutor = new ActionExecutor(intentItem, actionItemArr);
            mExecutor.start(new IExecuteCallback() {
                @Override
                public void onFailed(int code) {
                    callback.onFailed(code);
                }

                @Override
                public void onSucceeded() {
                    callback.onSucceeded();
                }

                @Override
                public void onDeviceAdminAction(Intent intent) {
                    callback.onDeviceAdminAction(intent);
                }
            });
        }
    }

    private void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && mExecutor != null && !mExecutor.isFinished()) {
            mExecutor.onAccessibilityEvent(accessibilityEvent);
        }
    }

    void cancel() {
        if (mExecutor != null) {
            mExecutor.cancel();
        }
    }
}