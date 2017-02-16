package com.lizhe.devtools.accessibility;

import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;

import com.ihs.permission.IPermissionRequestListener;
import com.ihs.permission.IPermissionService;

import java.util.List;

class PermissionServiceImpl extends IPermissionService.Stub {

    private Handler mCallBackHandler;
    private IPermissionRequestListener mTaskListener;
    private List<PermissionItem> mPermissionItems;
    private int mSucceedCount;
    private boolean mIsCancelled;

    @Override
    public void startPermissionRequest(List<String> permissionValues,
                                       IPermissionRequestListener listener) throws RemoteException {
        mCallBackHandler = Utils.getValidHandler(null);
        mTaskListener = listener;
        mSucceedCount = 0;
        mIsCancelled = false;

        ActionManager.getInstance().loadData();
        mPermissionItems = ActionManager.getInstance().getPermissionItems(permissionValues);

        executeSinglePermission(0);
    }

    @Override
    public void cancelPermissionRequest() throws RemoteException {
        mIsCancelled = true;
        ActionManager.getInstance().cancel();
    }

    private void executeSinglePermission(final int index) {
        if (index < 0 || mPermissionItems == null || index >= mPermissionItems.size()) {
            callBackOnFinished(0, mPermissionItems == null ? 0 : mPermissionItems.size());
            return;
        }
        callBackOnSinglePermissionStarted(index);

        // 执行要权限的操作
        ActionManager.getInstance().executeProcess(mPermissionItems.get(index).mProcessId, new IExecuteCallback() {
            @Override
            public void onSucceeded() {
                callBackOnSinglePermissionFinished(index, true);
                mSucceedCount++;
                if (index == mPermissionItems.size() - 1) {
                    if (!mIsCancelled) {
                        callBackOnFinished(mSucceedCount, mPermissionItems.size());
                    } else {
                        callBackOnCancelled(mSucceedCount, mPermissionItems.size());
                    }
                    return;
                }
                if (!mIsCancelled) {
                    executeSinglePermission(index + 1);
                } else {
                    callBackOnCancelled(mSucceedCount, mPermissionItems.size());
                }
            }

            @Override
            public void onFailed(int code) {
                callBackOnSinglePermissionFinished(index, false);
                if (index == mPermissionItems.size() - 1) {
                    if (!mIsCancelled) {
                        callBackOnFinished(mSucceedCount, mPermissionItems.size());
                    } else {
                        callBackOnCancelled(mSucceedCount, mPermissionItems.size());
                    }
                    return;
                }
                if (!mIsCancelled) {
                    executeSinglePermission(index + 1);
                } else {
                    callBackOnCancelled(mSucceedCount, mPermissionItems.size());
                }
            }

            @Override
            public void onDeviceAdminAction(Intent intent) {
                callBackOnDeviceAdminAction(intent);
            }
        });
    }

    private void callBackOnSinglePermissionStarted(final int index) {
        mCallBackHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTaskListener != null) {
                    try {
                        mTaskListener.onSinglePermissionStarted(index);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void callBackOnSinglePermissionFinished(final int index, final boolean isSucceed) {
        mCallBackHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTaskListener != null) {
                    try {
                        mTaskListener.onSinglePermissionFinished(index, isSucceed);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void callBackOnDeviceAdminAction(final Intent intent) {
        mCallBackHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTaskListener != null) {
                    try {
                        mTaskListener.onDeviceAdminAction(intent);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void callBackOnFinished(final int succeedCount, final int totalCount) {
        mCallBackHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTaskListener != null) {
                    try {
                        mTaskListener.onFinished(succeedCount, totalCount);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void callBackOnCancelled(final int succeedCount, final int totalCount) {
        mCallBackHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTaskListener != null) {
                    try {
                        mTaskListener.onCancelled(succeedCount, totalCount);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}