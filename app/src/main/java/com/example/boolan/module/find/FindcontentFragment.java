package com.example.boolan.module.find;

import android.os.Bundle;

import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;

public class FindcontentFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.find1;
    }

    @Override
    public void finishCreateView(Bundle state) {

    }

    @Override
    public void lazyLoad() {

    }

    public static FindcontentFragment newInstance() {
        FindcontentFragment fragment = new FindcontentFragment();
        return fragment;
    }
}
