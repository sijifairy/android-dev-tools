package com.lizhe.devtools.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.WorkerThread;

import com.ihs.permission.IPermissionService;
import com.lizhe.devtools.DevToolsApplication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 1. 所有 Task 共享一个 IPermissionService 2. UNBIND_SERVICE_DELAY (10s) 后尝试 UnBindService
 */
public class BindServiceManager {

    public interface BindServiceListener {
        @WorkerThread
        void onServiceBound(IPermissionService iPermissionService);

        @WorkerThread
        void onServiceUnbound();
    }

    @IntDef({UNBOUND, UNBINDING, BINDING, BOUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BindState {
    }

    /**
     * 此类目的:保证调用者语法简洁,方便 remove listener
     */
    public static class BindServiceListenerHolder {
        private BindServiceListener listener;

        public BindServiceListenerHolder() {
        }

        public BindServiceListener getListener() {
            return listener;
        }

        public BindServiceListener setListener(BindServiceListener listener) {
            this.listener = listener;
            return listener;
        }
    }

    private static class SingletonHolder {
        private static final BindServiceManager INSTANCE = new BindServiceManager();
    }

    private final static int UNBOUND = 0x0;
    private final static int UNBINDING = 0x1;
    private final static int BINDING = 0x2;
    private final static int BOUND = 0x3;

    private final static int MSG_BIND_SERVICE = 0x1;
    private final static int MSG_UNBIND_SERVICE = 0x2;
    private final static int MSG_SERVICE_CONNECTED = 0x3;
    private final static int MSG_SERVICE_DISCONNECTED = 0x4;
    private final static int MSG_REMOVE_BIND_SERVICE_LISTENER = 0x5;

    public static BindServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final List<BindServiceListener> callbackListenerList = new CopyOnWriteArrayList<>();
    @BindState
    private volatile int serviceBindState = UNBOUND;
    private IPermissionService iPermissionService;
    private Handler handler;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            handler.sendMessage(handler.obtainMessage(MSG_SERVICE_CONNECTED, service));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            handler.sendMessage(handler.obtainMessage(MSG_SERVICE_DISCONNECTED));
        }
    };

    private BindServiceManager() {
        HandlerThread bindServiceThread = new HandlerThread("BindService Work Thread");
        bindServiceThread.start();
        handler = new Handler(bindServiceThread.getLooper(), new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_BIND_SERVICE:
                        bindServiceInner((BindServiceListener) msg.obj);
                        break;
                    case MSG_UNBIND_SERVICE:
                        unbindServiceInner();
                        break;
                    case MSG_SERVICE_CONNECTED:
                        onServiceConnectedInner((IBinder) msg.obj);
                        break;
                    case MSG_SERVICE_DISCONNECTED:
                        callBackOnUnboundInner();
                        break;
                    case MSG_REMOVE_BIND_SERVICE_LISTENER:
                        removeListenerInner((BindServiceListener) msg.obj);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public synchronized void bindService(BindServiceListener listener) {
        handler.sendMessage(handler.obtainMessage(MSG_BIND_SERVICE, listener));
    }

    public synchronized void removeListener(BindServiceListener listener) {
        handler.sendMessage(handler.obtainMessage(MSG_REMOVE_BIND_SERVICE_LISTENER, listener));
    }

    @WorkerThread
    private void bindServiceInner(final BindServiceListener listener) {
        addListenerInner(listener);

        if (serviceBindState == BOUND) {
            if (listener != null) {
                listener.onServiceBound(iPermissionService);
            }
            handler.removeCallbacksAndMessages(MSG_UNBIND_SERVICE);
        } else if (serviceBindState != BINDING) {
            serviceBindState = BINDING;
            Intent intent = new Intent(DevToolsApplication.getContext(), PermissionService.class);
            DevToolsApplication.getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @WorkerThread
    private void unbindServiceInner() {
        if (serviceBindState != BOUND) {
            return;
        }
        if (handler.hasMessages(MSG_BIND_SERVICE)) {
            return;
        }
        iPermissionService = null;
        serviceBindState = UNBINDING;
        DevToolsApplication.getContext().unbindService(serviceConnection);
        callBackOnUnboundInner();
    }

    @WorkerThread
    private void onServiceConnectedInner(IBinder service) {
        try {
            IPermissionService iPermissionService = IPermissionService.Stub.asInterface(service);
            callBackOnBoundInner(iPermissionService);
        } catch (Exception e) {
            e.printStackTrace();
            callBackOnUnboundInner();
        }
    }

    @WorkerThread
    private void callBackOnBoundInner(IPermissionService iPermissionService) {
        this.iPermissionService = iPermissionService;
        serviceBindState = BOUND;
        for (final BindServiceListener listener : callbackListenerList) {
            if (listener != null) {
                listener.onServiceBound(iPermissionService);
            }
        }
        handler.removeCallbacksAndMessages(MSG_UNBIND_SERVICE);
    }

    @WorkerThread
    private void callBackOnUnboundInner() {
        iPermissionService = null;
        serviceBindState = UNBOUND;
        for (final BindServiceListener listener : callbackListenerList) {
            if (listener != null) {
                listener.onServiceUnbound();
            }
        }
    }

    @WorkerThread
    private void addListenerInner(BindServiceListener listener) {
        if (listener != null) {
            callbackListenerList.add(listener);
        }
    }

    @WorkerThread
    private void removeListenerInner(BindServiceListener listener) {
        if (listener != null) {
            callbackListenerList.remove(listener);
            if (callbackListenerList.size() == 0) {
                if (!handler.hasMessages(MSG_BIND_SERVICE)) {
                    handler.sendEmptyMessage(MSG_UNBIND_SERVICE);
                }
            }
        }
    }
}