package com.example.boolan.module.MainActivityfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;
import com.example.boolan.beans.User;
import com.example.boolan.module.home.AddContentActivity;
import com.example.boolan.module.home.GuanzhuFragment;
import com.example.boolan.module.home.TuijianFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.home_tabs)
    TabLayout mTableLayout;
    @BindView(R.id.home_view)
    ViewPager mViewPager;
    @BindView(R.id.home_add)
    Button add;
    private List<User> userList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private List<String> titles = new ArrayList<>();
    private GuanzhuFragment fragment1;
    private TuijianFragment fragment;
    private String[] strings = new String[]{"关注","推荐"};
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void finishCreateView(Bundle state) {
        fragment=new TuijianFragment();
        fragment1=new GuanzhuFragment();
        initData();
        lazyLoad();
        mTableLayout.getTabAt(1).select();
        add.setOnClickListener(this);
    }

    @Override
    public void lazyLoad() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_add:
                Intent intent = new Intent(getActivity(), AddContentActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initData() {
        fragments.clear();
        fragments.add(fragment1);
        fragments.add(fragment);
        MyAdapter fragmentAdater = new  MyAdapter(getChildFragmentManager());
        mViewPager.setAdapter(null);
        mViewPager.setAdapter(fragmentAdater);
        mTableLayout.setupWithViewPager(mViewPager);//此方法就是让tablayout和ViewPager联动
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
}