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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.carlife.sdk.util.Logger;
import com.baidu.carlifevehicle.R;
import com.baidu.carlifevehicle.message.MsgHandlerCenter;
import com.baidu.carlifevehicle.util.CommonParams;

public class ExceptionFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = "ExceptionFragment";
    private static final int CLICK_INTERVAL = 5000;
    private boolean isCalling = false ; 
    private ImageView mDisplayImgView = null;
    private TextView mDisplayTextView = null;
    private Button mStartAppBtn;
    private String mExceptionTips;
    private int mExceptionDrawableId;
    private static ExceptionFragment mExceptionFragment;
    private long mClickTime = System.currentTimeMillis();
    private static boolean mStartAppBtnVisible = true;

    public static ExceptionFragment getInstance() {
        if (mExceptionFragment == null) {
            mExceptionFragment = new ExceptionFragment();
        }
        return mExceptionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Logger.d(TAG, "onCreateView");
        mContentView = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.frag_exception, null);
        mDisplayImgView = (ImageView) mContentView.findViewById(R.id.exception_display_img_view);
        mDisplayTextView = (TextView) mContentView.findViewById(R.id.exception_display_text_view);
//        if (ConnectManager.CONNECTED_BY_NCM_IOS != ConnectManager.getInstance().getConnectType()
//                && ConnectManager.CONNECTED_BY_WIFI != ConnectManager.getInstance().getConnectType()) {
//            Logger.d(TAG, "android phone is install");
//            setIsCalling(false);
//        }
        mStartAppBtn = (Button) mContentView.findViewById(R.id.exception_start_app_btn);
        mStartAppBtn.setOnClickListener(this);

        if (mStartAppBtnVisible) {
            mStartAppBtn.setVisibility(View.VISIBLE);
        } else {
            mStartAppBtn.setVisibility(View.GONE);
        }
        
        if (!TextUtils.isEmpty(mExceptionTips)) {
            mDisplayTextView.setText(mExceptionTips);
        }
        if (mExceptionDrawableId > 0) {
            mDisplayImgView.setImageResource(mExceptionDrawableId);
        }

        // adapt for EL_AFTER_MARKET
        if (CommonParams.VEHICLE_CHANNEL.equals(CommonParams.VEHICLE_CHANNEL_EL_AFTER_MARKET)) {
            mStartAppBtn.setVisibility(View.GONE);
            mStartAppBtn = null;
            changeUILayout();
        }
        return mContentView;
    }
    
    private void changeUILayout() {
        RelativeLayout.LayoutParams params = null;
        
        params = (RelativeLayout.LayoutParams) mDisplayImgView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        params.height = 200;
        mDisplayImgView.setLayoutParams(params);
        
        params = (RelativeLayout.LayoutParams) mDisplayTextView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mDisplayTextView.setLayoutParams(params);
        mDisplayTextView.setTextSize(22.0f);
        
        mContentView.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(TAG, "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d(TAG, "onDetach");
    }

    @Override
    public void onClick(View v) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - mClickTime > CLICK_INTERVAL) {
            mClickTime = clickTime;
        } else {
            return;
        }
        switch (v.getId()) {
            case R.id.exception_start_app_btn:
                //TODO 点击按钮去发送消息给手机端，把手机端carlife切换到前台，IOS和Android处理方式不一样，这里需分开处理
                MsgHandlerCenter.dispatchMessage(CommonParams.MSG_MAIN_DISPLAY_TOUCH_FRAGMENT);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mActivity != null) {
            mActivity.openExitAppDialog();
        }
        return true;
    }

    public void changeTipsCallback(String tips) {
        if (!TextUtils.isEmpty(tips)) {
            mExceptionTips = tips;
        }
        if (mDisplayTextView != null) {
            mDisplayTextView.setText(mExceptionTips);
        }
    }

    public void setIsCalling(boolean state) {
        Logger.d(TAG, "setIsCalling" + state);
        isCalling = state;
    }
    
    public void setStartAppBtnHide() {
        Logger.d(TAG, "setStartAppBtnHide");
//        if (ConnectManager.CONNECTED_BY_NCM_IOS == ConnectManager.getInstance().getConnectType()
//                || ConnectManager.CONNECTED_BY_WIFI == ConnectManager.getInstance().getConnectType()) {
//            setIsCalling(true);
//        }
        if (mStartAppBtn != null) {
            mStartAppBtn.setVisibility(View.GONE);
        }
        mStartAppBtnVisible = false;
    }
    
    public void setStartAppBtnVisible() {
        if (mStartAppBtn != null) {
            mStartAppBtn.setVisibility(View.VISIBLE);
        }
        mStartAppBtnVisible = true;
    }
    
    public void changeDrawableCallback(int drawableId) {
        if (drawableId > 0) {
            mExceptionDrawableId = drawableId;
        }
        if (mDisplayImgView != null) {
            mDisplayImgView.setImageResource(mExceptionDrawableId);
        }
    }

}