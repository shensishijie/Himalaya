package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.Base.BaseFragment;
import com.example.himalaya.R;

public class HistoryFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_history, container, false);
        return rootView;
    }
}
