package com.lizhe.devtools.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.lizhe.devtools.DevToolsApplication;
import com.lizhe.devtools.accessibility.action.ActionItem;
import com.lizhe.devtools.accessibility.intent.IntentItem;
import com.lizhe.devtools.accessibility.node.IdentifyNodeInfo;

import java.util.Collections;
import java.util.LinkedList;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SCROLLED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class ActionExecutor {

    private enum ActionState {
        NONE,
        PREPARED,
        WAIT_SCROLL,
        WAIT_WINDOW,
        ACTION_EXECUTING,
        BACK,
        FINISH
    }

    private static final String TAG = ActionExecutor.class.getSimpleName();
    private Context mContext;
    private IExecuteCallback mCallback;
    private Handler mHandler;
    private final Object mThreadLocker = ActionExecutor.class;
    private volatile ActionState mActionState = ActionState.NONE;
    private ExecuteThread mActionExecuteThread;
    private AccessibilityService mAccessibilityService;
    private IntentItem mIntentItem;
    private LinkedList<ActionItem> mActionItems;

    public static final int FINISH_CODE_DATA_INCOMPLETE = 11;
    public static final int FINISH_CODE_PROCESS_INFO_ERROR = 12;
    public static final int FINISH_CODE_INTENT_INFO_ERROR = 13;
    public static final int FINISH_CODE_ACTION_INFO_ERROR = 14;
    private static final int FINISH_CODE_NO_ACCESSIBILITY_SERVICE = 16;
    private static final int FINISH_CODE_NO_ACTIVITY_TO_HANDLE = 17;
    private static final int FINISH_CODE_TIME_OUT = 18;
    private static final int FINISH_CODE_CANCELED = 19;
    private static final int FINISH_CODE_INTERRUPTED = 111;

    private static final int MSG_TIME_OUT_TO_FINISH = 1;
    private static final int MSG_NO_RESPONDING_TO_FINISH = 2;
    private static final int MSG_BACK_CHECK = 3;

    private static final int INTERVAL_TIMEOUT = 8000;

    private int mCode = 0;
    private int mWindowId = -1;
    private long mTime = 0;
    private boolean mIsExpired = false;

    ActionExecutor(IntentItem intentItem, ActionItem[] actionItemArr) {
        mContext = DevToolsApplication.getContext();
        mAccessibilityService = CommonAccessibilityService.getInstance();
        mActionState = ActionState.PREPARED;
        mIntentItem = intentItem;
        mActionItems = new LinkedList<>();
        Collections.addAll(mActionItems, actionItemArr);

        HandlerThread handlerThread = new HandlerThread("ActionExecutorThread");
        handlerThread.start();
        mHandler = new ActionExecutorHandler(handlerThread.getLooper());
    }

    void start(IExecuteCallback callback) {
        Log.i(TAG, "permission_executor execute !!!!! ");
        mCallback = callback;
        if (mIsExpired) {
            Log.i(TAG, "permission_executor  已失效");
            return;
        }
        if (mActionState != ActionState.PREPARED) {
            Log.i(TAG, "permission_executor  已经执行");
            return;
        }
        if (mCallback == null) {
            Log.i(TAG, "permission_executor callback == null");
            return;
        }
        if (mAccessibilityService == null) {
            Log.i(TAG, "permission_executor service == null 没有辅助权限服务！！");
            onFinish(FINISH_CODE_NO_ACCESSIBILITY_SERVICE);
            return;
        }

        mActionState = ActionState.WAIT_WINDOW;
        mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT_TO_FINISH, INTERVAL_TIMEOUT);
        if (mActionExecuteThread != null) {
            Log.i(TAG, "permission_executor mExecuteThread is exist !!! ");
        }
        mActionExecuteThread = new ExecuteThread();
        mActionExecuteThread.start();
        mTime = System.currentTimeMillis();

        try {
            Intent intent = mIntentItem.getIntent();
            if (DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN.equals(intent.getAction())) {
                mCallback.onDeviceAdminAction(intent);
            } else {
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFinish(FINISH_CODE_NO_ACTIVITY_TO_HANDLE);
        }
    }

    void cancel() {
        Log.i(TAG, "!!!!! cancel() !!!!!!!!!!!!!!!!!!");
//        onFinish(FINISH_CODE_CANCELED);
        stopThread(FINISH_CODE_CANCELED);
        if (mActionExecuteThread != null) {
            mActionExecuteThread.onFinish();
        }
    }

    void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (mActionState == ActionState.FINISH) {
            Log.i(TAG, "permission_executor onAccessibilityEvent finish ");
            return;
        }
        if (accessibilityEvent == null) {
            Log.i(TAG, "permission_executor event == null ");
            return;
        }
        if (accessibilityEvent.getEventType() != TYPE_WINDOW_STATE_CHANGED && accessibilityEvent.getEventType() != TYPE_VIEW_SCROLLED) {
            return;
        }
        if (accessibilityEvent.getPackageName().equals(mIntentItem.mPkgName)) {
            if (accessibilityEvent.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
                Log.i(TAG, "permission_executor WindowId " + accessibilityEvent.getWindowId());
                mHandler.removeMessages(MSG_TIME_OUT_TO_FINISH);
                synchronized (mThreadLocker) {
                    Log.i(TAG, "permission_executor state !!!" + mActionState);
                    mWindowId = accessibilityEvent.getWindowId();
                    if (mActionState == ActionState.ACTION_EXECUTING) {
                        Log.i(TAG, "permission_executor 运行中切换页面");
                    } else if (mActionState == ActionState.BACK) {
                        onBack(0);
                    } else if (mActionState == ActionState.WAIT_WINDOW) {
                        mActionState = ActionState.ACTION_EXECUTING;
                        mThreadLocker.notify();
                    }
                }
            } else if (accessibilityEvent.getEventType() == TYPE_VIEW_SCROLLED) {
                synchronized (mThreadLocker) {
                    if (mActionState == ActionState.WAIT_SCROLL) {
                        Log.i(TAG, "permission_executor 通知滑动等待结束");
                        mActionState = ActionState.ACTION_EXECUTING;
                        mThreadLocker.notify();
                    }
                }
            }
        } else if (mActionState == ActionState.BACK && accessibilityEvent.getPackageName().equals(mContext.getPackageName())) {
            Log.i(TAG, "permission_executor remove MESSAGE_BACK_TIMEOUT message when finish!");
            onFinish(mCode);
        } else if (mActionState == ActionState.ACTION_EXECUTING) {
            onFinish(FINISH_CODE_INTERRUPTED);
        }
    }

    private void onBack(int tryCount) {
        Log.i(TAG, "permission_executor_back performBack mState = " + mActionState);
        Log.i(TAG, "permission_executor_back performBack action ret = " + mAccessibilityService.performGlobalAction(GLOBAL_ACTION_BACK));
        Log.i(TAG, "permission_executor_back send back message! tryCounts " + (tryCount + 1));
        mHandler.removeMessages(MSG_BACK_CHECK);
        Message message = new Message();
        message.what = MSG_BACK_CHECK;
        message.arg1 = tryCount + 1;
        mHandler.sendMessageDelayed(message, 2000);
    }

    private void onFinish(int code) {
        if (mActionState == ActionState.FINISH) {
            Log.i(TAG, "permission_executor has finished");
            return;
        }
        stopThread(code);
        mActionState = ActionState.FINISH;
        Log.i(TAG, "permission_executor onFinish code: " + code + " time: " + (System.currentTimeMillis() - mTime));
        mHandler.removeCallbacksAndMessages(null);
        if (mHandler.getLooper() != null) {
            mHandler.getLooper().quit();
        }
        if (code % 100 == 0) {
            mCallback.onSucceeded();
        } else {
            mCallback.onFailed(code);
        }
    }

    private void stopThread(int code) {
        if (mIsExpired) {
            Log.i(TAG, "permission_executor has canceled");
            return;
        }
        mCode = code;
        mIsExpired = true;
        if (mActionExecuteThread != null && mActionExecuteThread.isAlive() && !mActionExecuteThread.isInterrupted()) {
            Log.i(TAG, "permission_executor execute thread interrupted!!!");
            mActionExecuteThread.interrupt();
        }
    }

    boolean isFinished() {
        return mActionState == ActionState.FINISH;
    }

    private boolean checkIdentity(AccessibilityNodeInfo accessibilityNodeInfo, IdentifyNodeInfo identifyNodeInfo) {
        if (Utils.findNodeByTextInScreen(accessibilityNodeInfo, identifyNodeInfo.mFindTextList) != null) {
            return true;
        }
        if (identifyNodeInfo.mAllowSkip) {
            Log.i(TAG, "该结点不是目标结点，并且不允许跳过");
            return false;
        } else {
            return true;
        }
    }

    private class ActionExecutorHandler extends Handler {

        ActionExecutorHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_TIME_OUT_TO_FINISH:
                    onFinish(FINISH_CODE_TIME_OUT);
                    break;
                case MSG_NO_RESPONDING_TO_FINISH:
                    stopThread(112);
                    break;
                case MSG_BACK_CHECK:
                    int tryCounts = message.arg1;
                    Log.i(TAG, "permission_executor_back handle MESSAGE_BACK_TIMEOUT message! tryCounts " + tryCounts);
                    if (tryCounts < 2) {
                        onBack(tryCounts);
                    } else if (mCode == 0) {
                        onFinish(300);
                    } else {
                        onFinish(113);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class ExecuteThread extends Thread {
        private int currentWindowId = -1;

        @Override
        public void run() {
            try {
                Log.i(TAG, "permission_executor ExecuteThread start");
                while (!mActionItems.isEmpty()) {
                    if (mActionState == ActionState.FINISH) {
                        break;
                    }
                    if (mIsExpired) {
                        break;
                    }

                    ActionItem actionItem = mActionItems.poll();
                    if (actionItem == null) {
                        throw new Exception("currentActionItem == null");
                    }
                    Log.i(TAG, "permission_executor actionItem " + actionItem.mId);
                    if (actionItem.mNeedWaitWindow) {
                        synchronized (mThreadLocker) {
                            if (currentWindowId == mWindowId) {
                                Log.i(TAG, "permission_executor 等待窗口变更的回调ing " + currentWindowId);
                                mActionState = ActionState.WAIT_WINDOW;
                                mThreadLocker.wait();
                                Log.i(TAG, "permission_executor 等待窗口变更结束 " + mWindowId);
                            } else {
                                Log.i(TAG, "permission_executor 无需等待，新页面已出现 一般不会出现 windowID" + currentWindowId + " mCurrentWindowID" + mWindowId);
                            }
                            currentWindowId = mWindowId;
                        }
                    }
                    if (actionItem.mNeedWaitTime > 0) {
                        Log.i(TAG, "needWaitTime = " + actionItem.mNeedWaitTime);
                        sleep(actionItem.mNeedWaitTime);
                    }
                    mHandler.removeMessages(MSG_NO_RESPONDING_TO_FINISH);
                    mHandler.sendEmptyMessageDelayed(MSG_NO_RESPONDING_TO_FINISH, 6000);

                    AccessibilityNodeInfo rootNode = null;
                    for (int i = 0; i < 3; i++) {
                        sleep(i * 150);
                        rootNode = mAccessibilityService.getRootInActiveWindow();
                        if (rootNode != null) {
                            Log.i(TAG, "permission_executor getRoot tryCount " + i);
                            break;
                        }
                    }
                    if (rootNode == null) {
                        throw new Exception("rootNode == null");
                    }
                    if (actionItem.mIdentifyNodeInfo != null) {
                        Log.i(TAG, "permission_executor 检测结点是否正确 ");
                        if (checkIdentity(rootNode, actionItem.mIdentifyNodeInfo)) {
                            Log.i(TAG, "permission_executor 结点正确 ");
                        } else {
                            Log.i(TAG, "permission_executor 结点错误 重试 ");
                            mActionItems.addFirst(actionItem);
                        }
                    }
                    currentWindowId = mWindowId;
                    if (actionItem.mLocateNodeInfo == null) {
                        throw new Exception("locateNodeInfo == null");
                    }
                    AccessibilityNodeInfo locateNode = Utils.findNodeByLocateInfo(rootNode, actionItem.mLocateNodeInfo);
                    if (locateNode == null && actionItem.mScrollNodeInfo != null) {
                        AccessibilityNodeInfo scrollNode = Utils.findNodeByScrollInfo(rootNode, actionItem.mScrollNodeInfo);
                        if (scrollNode == null) {
                            Log.i(TAG, "scrollNode == null,  retry!");
                            for (int i = 0; i < 5; i++) {
                                sleep(500);
                                scrollNode = Utils.findNodeByScrollInfo(rootNode, actionItem.mScrollNodeInfo);
                                if (scrollNode != null) {
                                    Log.i(TAG, "retry to get scrollNode successfully, retryCount = " + i);
                                    break;
                                }
                            }
                        }
                        if (scrollNode != null) {
                            sleep(500);
                            while (locateNode == null) {
                                synchronized (mThreadLocker) {
                                    if (scrollNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                                        Log.i(TAG, "permission_executor 等待滑动的回调ing");
                                        mActionState = ActionState.WAIT_SCROLL;
                                        mThreadLocker.wait();
                                        Log.i(TAG, "permission_executor 等待滑动的回调结束 尝试重新获取");
                                        for (int i = 0; i < 3; i++) {
                                            Log.i(TAG, "permission_executor getLocateNode 尝试次数 " + i);
                                            sleep(20);
                                            locateNode = Utils.findNodeByLocateInfo(scrollNode, actionItem.mLocateNodeInfo);
                                            if (locateNode != null) {
                                                break;
                                            }
                                        }
                                    } else {
                                        Log.i(TAG, "permission_executor scrollNode.performAction false");
                                        mThreadLocker.wait(200);
                                        locateNode = Utils.findNodeByLocateInfo(scrollNode, actionItem.mLocateNodeInfo);
                                    }
                                }
                            }
                        } else {
                            throw new Exception("scrollNode == null");
                        }
                    }
                    if (locateNode == null) {
                        throw new Exception("locateNode == null");
                    }
                    Log.i(TAG, "permission_executor 获取 locateNode 结束 " + mActionState + " " + locateNode.getText());
                    if (actionItem.mCheckNodeInfo != null) {
                        AccessibilityNodeInfo checkNode = Utils.findNodeByCheckInfo(locateNode, actionItem.mCheckNodeInfo);
                        if (checkNode != null) {
                            if (Utils.isCheckNodeCorrect(checkNode, actionItem.mCheckNodeInfo)) {
                                Log.i(TAG, "已经是正确的 直接结束就可以了");
                                break;
                            }
                        }
                        Log.i(TAG, "checkNode == null");
                    }
                    if (actionItem.mOperationNodeInfo != null) {
                        locateNode = Utils.findBehaviorNode(locateNode, actionItem);
                        if (locateNode == null) {
                            throw new Exception("operationNode == null");
                        }
                        Log.i(TAG, "permission_executor click " + locateNode.getClassName() + " " + locateNode.getText());
                        if (!locateNode.performAction(actionItem.mOperationNodeInfo.mBehaviorCode)) {
                            throw new Exception("operationNode click failed");
                        }
                    }
                }
                onFinish();
            } catch (Exception e) {
                Log.i(TAG, "permission_executor error >> message: " + e.getMessage());
                onFinish();
            }
        }

        public void onFinish() {
            mHandler.removeMessages(MSG_NO_RESPONDING_TO_FINISH);
            if (mActionState != ActionState.FINISH) {
                Log.i(TAG, "permission_executor onActionExecuted code: " + mCode + " time " + (System.currentTimeMillis() - mTime));
                mActionState = ActionState.BACK;
                Log.i(TAG, "permission_executor_back start performBack");
                onBack(0);
            }
        }
    }
}