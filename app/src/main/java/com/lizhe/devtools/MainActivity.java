package com.lizhe.devtools;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lizhe.devtools.accessibility.PermissionRequestMgr;
import com.lizhe.devtools.accessibility.PermissionType;
import com.lizhe.devtools.accessibility.Utils;
import com.lizhe.devtools.utils.CommonUtils;

import java.util.EnumSet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View stubStatusBar = findViewById(R.id.stub_statusbar);
        stubStatusBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                CommonUtils.getStatusBarHeight(this)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        findViewById(R.id.tv_layout_bounds).setOnClickListener(this);
        findViewById(R.id.tv_gpu_overdraw_on).setOnClickListener(this);
        findViewById(R.id.tv_gpu_overdraw_off).setOnClickListener(this);
        findViewById(R.id.tv_force_rtl).setOnClickListener(this);
        findViewById(R.id.tv_gpu_rendering_bar).setOnClickListener(this);
        findViewById(R.id.tv_gpu_rendering_off).setOnClickListener(this);
        findViewById(R.id.tv_stay_awake).setOnClickListener(this);
        findViewById(R.id.tv_cpu_usage).setOnClickListener(this);
        findViewById(R.id.tv_kill_activity).setOnClickListener(this);
        findViewById(R.id.tv_wait_for_debugger).setOnClickListener(this);
        findViewById(R.id.tv_goto_developer_options).setOnClickListener(this);
        findViewById(R.id.tv_goto_system_settings).setOnClickListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        final ColorTransitionView colorTransitionView = (ColorTransitionView) findViewById(R.id.color_transition_view);
//        colorTransitionView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP
//                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
//                    if (event.getRawX() <= colorTransitionView.getMeasuredWidth() / 3) {
//                        colorTransitionView.startColorTransition(0);
//                    } else if (event.getRawX() <= colorTransitionView.getMeasuredWidth() * 2 / 3) {
//                        colorTransitionView.startColorTransition(1);
//                    } else {
//                        colorTransitionView.startColorTransition(2);
//                    }
//                }
//                return true;
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_layout_bounds:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_LAYOUT_BOUNDS));
                    }
                });
                break;
            case R.id.tv_gpu_overdraw_on:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_GPU_OVERDRAW_ON));
                    }
                });
                break;
            case R.id.tv_gpu_overdraw_off:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_GPU_OVERDRAW_OFF));
                    }
                });
                break;
            case R.id.tv_force_rtl:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_FORCE_RTL));
                    }
                });
                break;
            case R.id.tv_gpu_rendering_bar:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_GPU_PROFILING_BAR));
                    }
                });
            case R.id.tv_gpu_rendering_off:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_GPU_PROFILING_OFF));
                    }
                });
            case R.id.tv_stay_awake:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_STAY_AWAKE));
                    }
                });
            case R.id.tv_cpu_usage:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_SHOW_CPU_USAGE));
                    }
                });
            case R.id.tv_kill_activity:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_KILL_ACTIVITY));
                    }
                });
            case R.id.tv_wait_for_debugger:
                Utils.doThingsWithAccessibilityCheck(new Runnable() {
                    @Override
                    public void run() {
                        PermissionRequestMgr.getInstance().startRequest(EnumSet.of(PermissionType.TYPE_WAIT_DEBUGGER));
                    }
                });
            case R.id.tv_goto_developer_options:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.tv_goto_system_settings:
                intent = new Intent(Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }
}
