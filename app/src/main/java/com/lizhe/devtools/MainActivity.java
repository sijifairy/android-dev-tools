package com.lizhe.devtools;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lizhe.devtools.accessibility.PermissionType;
import com.lizhe.devtools.itemview.DragItemTouchHelper;
import com.lizhe.devtools.itemview.ItemDragAdapter;
import com.lizhe.devtools.itemview.ItemModel;
import com.lizhe.devtools.utils.CommonUtils;
import com.vansuita.materialabout.builder.AboutBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        initDrawerLayout();

        initAboutView();

        initRecyclerView();

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

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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
    }

    private void initDrawerLayout() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
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

        LinearLayout drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        if (CommonUtils.hasNavBar(this)) {
            drawerContent.setPadding(0, 0, 0, CommonUtils.getNavigationBarHeight(this));
        }
    }

    private void initAboutView() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("Li Zhe")
                .setSubTitle("Mobile Developer")
                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .addGooglePlayStoreLink("8002078663318221363")
                .addGitHubLink("user")
                .addFacebookLink("user")
                .addFiveStarsAction()
                .setVersionAsAppTitle()
                .addShareAction(R.string.app_name)
                .build();
        navigationView.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initRecyclerView() {
        List<ItemModel> data = new ArrayList<>();
        data.add(new ItemModel(R.drawable.icon_overdraw_on,
                R.string.overdraw,
                PermissionType.TYPE_GPU_OVERDRAW_ON,
                PermissionType.TYPE_GPU_OVERDRAW_OFF));
        data.add(new ItemModel(R.drawable.icon_force_rtl,
                R.string.force_rtl,
                PermissionType.TYPE_FORCE_RTL));
        data.add(new ItemModel(R.drawable.icon_layout_bounds,
                R.string.layout_bounds,
                PermissionType.TYPE_LAYOUT_BOUNDS));
        data.add(new ItemModel(R.drawable.icon_gpu_bar,
                R.string.gpu_rendering,
                PermissionType.TYPE_GPU_PROFILING_BAR,
                PermissionType.TYPE_GPU_PROFILING_OFF));
        data.add(new ItemModel(R.drawable.icon_stay_awake,
                R.string.stay_awake,
                PermissionType.TYPE_STAY_AWAKE));
        data.add(new ItemModel(R.drawable.icon_cpu_usage,
                R.string.cpu_usage,
                PermissionType.TYPE_SHOW_CPU_USAGE));
        data.add(new ItemModel(R.drawable.icon_kill_activity,
                R.string.kill_activity,
                PermissionType.TYPE_KILL_ACTIVITY));
        data.add(new ItemModel(R.drawable.icon_wait_debugger,
                R.string.wait_debugger,
                PermissionType.TYPE_WAIT_DEBUGGER));
        data.add(new ItemModel(R.drawable.icon_settings,
                R.string.system_setting,
                PermissionType.TYPE_SYSTEM_SETTINGS));
        data.add(new ItemModel(R.drawable.icon_developer,
                R.string.developer_option,
                PermissionType.TYPE_DEVELOPPER_OPTIONS));


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ItemDragAdapter adapter = new ItemDragAdapter(this);
        adapter.setData(data);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(new DragItemTouchHelper(adapter));
        helper.attachToRecyclerView(recyclerView);
    }
}
