package com.lizhe.devtools.accessibility;

import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;

import com.ihs.permission.IPermissionRequestListener;
import com.ihs.permission.IPermissionService;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PermissionRequestMgr {

    private static PermissionRequestMgr sInstance = null;
    private final AtomicBoolean mIsRunning = new AtomicBoolean(false);
    private PermissionRequestCallback mListener;
    private Handler mCallBackHandler;

    public static PermissionRequestMgr getInstance() {
        if (sInstance == null) {
            sInstance = new PermissionRequestMgr();
        }
        return sInstance;
    }

    private PermissionRequestMgr() {
    }

    public void startRequest(final EnumSet<PermissionType> permissionTypes) {
        startRequest(permissionTypes, new PermissionRequestCallback.Stub());
    }

    public void startRequest(final EnumSet<PermissionType> permissionTypes, final PermissionRequestCallback listener) {
        mCallBackHandler = Utils.getValidHandler(null);
        if (!mIsRunning.compareAndSet(false, true)) {
            mCallBackHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFinished(0, permissionTypes.size());
                }
            });
            return;
        }

        mListener = listener;

        final List<String> permissionValues = new ArrayList<>();
        for (PermissionType type : permissionTypes) {
            permissionValues.add(String.valueOf(type.getValue()));
        }

        final BindServiceManager.BindServiceListenerHolder bindServiceListenerHolder = new BindServiceManager.BindServiceListenerHolder();
        BindServiceManager.getInstance().bindService(bindServiceListenerHolder.setListener(new BindServiceManager.BindServiceListener() {
            @Override
            public void onServiceBound(IPermissionService iPermissionService) {
                if (!mIsRunning.get()) {
                    mCallBackHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onFinished(0, permissionTypes.size());
                        }
                    });
                    return;
                }
                try {
                    iPermissionService.startPermissionRequest(permissionValues, new IPermissionRequestListener.Stub() {
                        @Override
                        public void onSinglePermissionStarted(final int index) throws RemoteException {
                            if (mIsRunning.get()) {
                                mCallBackHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            if (index == 0) {
                                                mListener.onStarted();
                                            }
                                            mListener.onSinglePermissionStarted(index);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onSinglePermissionFinished(final int index, final boolean isSucceed) throws RemoteException {
                            if (mIsRunning.get()) {
                                mCallBackHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onSinglePermissionFinished(index, isSucceed);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onDeviceAdminAction(final Intent intent) throws RemoteException {
                            if (mIsRunning.get()) {
                                mCallBackHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onDeviceAdminAction(intent);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFinished(final int succeedCount, final int totalCount) throws RemoteException {
                            if (mIsRunning.compareAndSet(true, false)) {
                                mCallBackHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onFinished(succeedCount, totalCount);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(final int succeedCount, final int totalCount) throws RemoteException {
                            if (mIsRunning.compareAndSet(true, false)) {
                                mCallBackHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onCancelled();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } catch (RemoteException e) {
                    if (mIsRunning.compareAndSet(true, false)) {
                        mCallBackHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) {
                                    mListener.onFinished(0, permissionTypes.size());
                                }
                            }
                        });
                    }
                    BindServiceManager.getInstance().removeListener(bindServiceListenerHolder.getListener());
                }
            }

            @Override
            public void onServiceUnbound() {
                if (mIsRunning.compareAndSet(true, false)) {
                    mCallBackHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onFinished(0, permissionTypes.size());
                            }
                        }
                    });
                }
                BindServiceManager.getInstance().removeListener(bindServiceListenerHolder.getListener());
            }
        }));
    }

    public void cancelRequest() {
        BindServiceManager.getInstance().bindService(new BindServiceManager.BindServiceListener() {
            @Override
            public void onServiceBound(IPermissionService iBoostService) {
                try {
                    iBoostService.cancelPermissionRequest();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                BindServiceManager.getInstance().removeListener(this);
            }

            @Override
            public void onServiceUnbound() {
                BindServiceManager.getInstance().removeListener(this);
            }
        });
    }
}