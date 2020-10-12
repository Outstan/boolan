package com.example.boolan.module.find;

import android.os.Bundle;

import com.example.boolan.R;
import com.example.boolan.base.BaseFragment;

public class FindcountFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.find;
    }

    @Override
    public void finishCreateView(Bundle state) {

    }

    @Override
    public void lazyLoad() {

    }

    public static FindcountFragment newInstance() {
        FindcountFragment fragment = new FindcountFragment();
        return fragment;
    }
}
