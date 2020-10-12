package com.example.boolan.module.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.boolan.R;
import com.example.boolan.module.MainActivityfragment.FindFragment;
import com.example.boolan.module.MainActivityfragment.HomeFragment;
import com.example.boolan.module.MainActivityfragment.Mefragment;
import com.example.boolan.module.MainActivityfragment.NewsFragment;
import com.example.boolan.module.chat.ChatActivity;
import com.example.boolan.service.PreferencesService;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.NetWorkUtils;
import com.example.boolan.utils.Toast;
import com.example.boolan.utils.WsManager;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private long firstTime;
    private TabLayout mTabLayout;
    private HomeFragment mHomeFragment;
    private FindFragment mFindFragment;
    private NewsFragment mTidingsFragment;
    private Mefragment mMefragment;
    private Fragment[] mFragments;
    private Fragment fragment;
    private String[] tabs = new String[]{"首页", "发现", "消息", "我"};
    private int[] selectorImg = new int[]{R.drawable.tab_home_selector, R.drawable.tab_find_selector, R.drawable.tab_tidings_selector, R.drawable.tab_me_selector};
    private FragmentManager fm = getSupportFragmentManager();
    public static WsManager wsManage;
    private Map<String, String> data;
    private String urll = ConstantUtil.SERVER_ADDRES;
    private int pos=0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPower();
        mFragments = new Fragment[]{mHomeFragment.newInstance(), mFindFragment.newInstance(), mTidingsFragment.newInstance(), mMefragment.newInstance()};
        fragment = mHomeFragment.newInstance();
        initTabLayout();
        //默认选中的tab
        mTabLayout.getTabAt(0).select();
        initnetwork();
        SharedPreferences s = getSharedPreferences("user", MainActivity.MODE_PRIVATE);
        s.registerOnSharedPreferenceChangeListener(this);
        data = new PreferencesService(MainActivity.this).getdengluflag();
        if (data.get("dengluflag").equals("true")) {
            ws(data.get("id"));
        }
    }

    private void initnetwork() {
        int i = NetWorkUtils.getAPNType(MainActivity.this);
        if (i == 0) {
            Toast.getInstance(MainActivity.this).show("没有网络连接", R.drawable.tips);
        } else if (i == 1) {
            Toast.getInstance(MainActivity.this).show("正在使用WIFI网络", R.drawable.tips);
        } else if (i == 2) {
            Toast.getInstance(MainActivity.this).show("正在使用2G网络,请留意流量使用情况", R.drawable.tips);
        } else if (i == 3) {
            Toast.getInstance(MainActivity.this).show("正在使用3G网络,请留意流量使用情况", R.drawable.tips);
        } else if (i == 4) {
            Toast.getInstance(MainActivity.this).show("正在使用4G网络,请留意流量使用情况", R.drawable.tips);
        }
    }

    /**
     * 初始化TabLayout
     */
    private void initTabLayout() {
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switchFragment(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // 添加tab到容器中
        for (int i = 0; i < 4; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView(i)));
        }
    }
    /**
     * 切换fragment
     *
     * @param position
     */
    @SuppressLint("WrongConstant")
    private void switchFragment(int position) {
        Fragment currentFragment = mFragments[position];
        if (currentFragment != null) {
            //添加渐隐渐现的动画
            FragmentTransaction ft = fm.beginTransaction();
            if (position>pos) {
                ft.setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out);
            }else {
                ft.setCustomAnimations(
                        R.anim.slide_right_in1,
                        R.anim.slide_left_out1,
                        R.anim.slide_left_in1,
                        R.anim.slide_right_out1);
            }
            if (!currentFragment.isAdded()) {  // 先判断是否被add过
                ft.hide(fragment).add(R.id.fragment_content, currentFragment).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(fragment).show(currentFragment).commit(); // 隐藏当前的fragment，显示下一个
            }
            fragment = currentFragment;
            pos = position;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.getInstance(MainActivity.this).show("再按一次退出程序", R.drawable.tips);
                firstTime = secondTime;
            } else {
                System.exit(0);
                wsManage.stopConnect();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置自定义view
     *
     * @param index 索引值
     * @return view对象
     */
    private View getTabView(int index) {
        View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.main_item_tab, null);
        ImageView tabImage = inflate.findViewById(R.id.tab_image);
        TextView tabTitle = inflate.findViewById(R.id.tab_title);
        tabImage.setImageResource(selectorImg[index]);
        tabTitle.setText(tabs[index]);
        return inflate;
    }

    /**
     * 动态权限申请
     */
    public void requestPower() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                android.widget.Toast.makeText(this, "需要读写权限，请打开设置开启对应的权限", android.widget.Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    private void ws(String id) {
        wsManage = new WsManager.Builder(getBaseContext())
                .client(new OkHttpClient().newBuilder()
                        .pingInterval(15, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build())
                .needReconnect(true)
                .wsUrl("ws://" + urll + "/websocket/" + id)
                .build();
        wsManage.setWsStatusListener(ChatActivity.wsBaseStatusListener);
        wsManage.startConnect();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("dengluflag")) {
            if (sharedPreferences.getString("dengluflag", "").equals("true")) {
                ws(sharedPreferences.getString("id", ""));
            } else {
                wsManage.stopConnect();
            }
            mFragments = new Fragment[]{mHomeFragment.newInstance(), mFindFragment.newInstance(), mTidingsFragment.newInstance(), mMefragment.newInstance()};
        }
    }
}