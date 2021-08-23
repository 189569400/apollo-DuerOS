/******************************************************************************
 * Copyright 2017 The Baidu Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package com.baidu.carlifevehicle.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.baidu.carlife.sdk.util.Logger;
import com.baidu.carlifevehicle.CarlifeActivity;

/**
 * base fragment
 */
public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    protected static CarlifeActivity mActivity = null;

    protected static Context mContext = null;

    protected static CarLifeFragmentManager mFragmentManager = null;
    protected View mContentView = null;
    /**
     * store data for show
     */
    protected Bundle mShowBundle = null;
    /**
     * store instance state for restore
     */
    protected Bundle mBackBundle = null;

    public static void initBeforeAll(CarlifeActivity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mFragmentManager = mActivity.getCarLifeVehicleFragmentManager();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logger.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d(TAG, "onDetach");
    }

    /**
     * Called when back pressed
     *
     * @return true for consumed, false to pass to subclasses
     */
    public boolean onBackPressed() {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public static CarlifeActivity getMainActivity() {
        return mActivity;
    }

}
