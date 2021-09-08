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
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.carlife.sdk.receiver.CarLife;
import com.baidu.carlife.sdk.util.Logger;
import com.baidu.carlife.sdk.util.TimerUtils;
import com.baidu.carlifevehicle.R;
import com.baidu.carlifevehicle.message.MsgBaseHandler;
import com.baidu.carlifevehicle.message.MsgHandlerCenter;
import com.baidu.carlifevehicle.util.CommonParams;
import com.baidu.carlifevehicle.view.LoadingProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends BaseFragment implements OnClickListener {
    private static final String TAG = "MainFragment";
    private ImageView mImgView = null;
    private LoadingProgressBar mConnectProgress = null;
    private TextView mConnectInfo = null;

    private Button mRetryBtn = null;
    private TextView mHelpBtn = null;
    private ImageButton mExitAppBtn = null;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    public static int CONNECT_TIMEOUT_MS = 20 * 1000;
    public static final int CONNECT_TIMEOUT_MS_WIFI = 20 * 1000;
    public static final int CONNECT_TIMEOUT_MS_USB = 30 * 1000;
    public static final int CONNECT_TIMEOUT_MS_INSTALL = 60 * 1000;
    private MsgBaseHandler mHandler = null;

    public static final String MAIN_TYPE = "main_type";
    public static final int MAIN_TYPEE_NONE = 0x0000;
    public static final int MAIN_TYPE_FROM_AUTO_CONNECT = 0x0001;
    public static final int MAIN_TYPE_FROM_UNAUTO_CONNECT = 0x0002;
    public static final int MAIN_TYPE_FROM_EXCEPTION_FRAGMENT = 0x0003;
    public static final int MAIN_TYPE_FROM_SETTING_FRAGMENT = 0x0004;
    private int connectStatusHeight = 0;
    private RelativeLayout mRellayoutStatus;
    RelativeLayout.LayoutParams wParams = null;
    RelativeLayout.LayoutParams cParams = null;
    private static MainFragment mMainFragment;

    private boolean isAoaNotSupportADBNotOpen = false;
    private boolean isNeedShowHelpMainFragment = true;

    public static MainFragment getInstance() {
        if (mMainFragment == null) {
            mMainFragment = new MainFragment();
        }
        return mMainFragment;

    }

    public MainFragment() {
        mHandler = new MsgMainFragmentHandler();
        MsgHandlerCenter.registerMessageHandler(mHandler);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Logger.d(TAG, "onHiddenChanged, hidden: " + hidden);
        super.onHiddenChanged(hidden);
        if (isNeedShowHelpMainFragment && !hidden) {
            MsgHandlerCenter.dispatchMessageDelay(CommonParams.MSG_MAIN_DISPLAY_HELP_MAIN_FRAGMENT, 30 * 1000);
            //主页首次显示时才开始去执行连接
            beginConnect();
            isNeedShowHelpMainFragment = false;
        } else if (hidden && mHandler.hasMessages(CommonParams.MSG_MAIN_DISPLAY_HELP_MAIN_FRAGMENT)) {
            MsgHandlerCenter.removeMessages(CommonParams.MSG_MAIN_DISPLAY_HELP_MAIN_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Logger.d(TAG, "onCreateView");
        mContentView = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.frag_main, null);
        mImgView = (ImageView) mContentView.findViewById(R.id.main_img_view);
        mConnectProgress = (LoadingProgressBar) mContentView.findViewById(R.id.main_load_round_progress);
        mConnectProgress.setVisibility(View.GONE);

        mConnectInfo = (TextView) mContentView.findViewById(R.id.main_info_text_view);
        mRellayoutStatus = (RelativeLayout) mContentView.findViewById(R.id.main_rellayout_status);
        mRetryBtn = (Button) mContentView.findViewById(R.id.main_retry_btn);
        
        mHelpBtn = (TextView) mContentView.findViewById(R.id.main_btn_help);
        mHelpBtn.setOnClickListener(this);
        mExitAppBtn = (ImageButton) mContentView.findViewById(R.id.exit_img_btn);
        mExitAppBtn.setOnClickListener(this);
        if (CommonParams.VEHICLE_CHANNEL.equals(CommonParams.VEHICLE_CHANNEL_EL_AFTER_MARKET)) {
            mRetryBtn.setVisibility(View.GONE);
            mRetryBtn = null;
            changeUILayout();
        } else {
            mRetryBtn.setOnClickListener(this);
        }
        
        CONNECT_TIMEOUT_MS = CONNECT_TIMEOUT_MS_USB;
        Logger.d(TAG, "set timeout: " + CONNECT_TIMEOUT_MS_USB);
        return mContentView;
    }
    
    private void changeUILayout() {
        mHelpBtn.setVisibility(View.GONE);
        mExitAppBtn.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = null;
        
        params = (RelativeLayout.LayoutParams) mImgView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        params.height = 200;
        mImgView.setLayoutParams(params);
        
        params = (RelativeLayout.LayoutParams) mConnectInfo.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mConnectInfo.setLayoutParams(params);
        mConnectInfo.setTextSize(22.0f);
        
        mContentView.invalidate();
    }

    private void retryBtnClick() {
        //todo adb 连接是否需要适配
        if (mRetryBtn != null) {
            mRetryBtn.setVisibility(View.INVISIBLE);
        }
        mConnectInfo.setText(R.string.usb_not_connected);
        mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_usbwifi));
        beginConnect();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParams();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void beginConnect() {
        CarLife.receiver().connect();
        TimerUtils.INSTANCE.schedule(mConnectionTask, CONNECT_TIMEOUT_MS, CONNECT_TIMEOUT_MS);
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
        MsgHandlerCenter.unRegisterMessageHandler(mHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_retry_btn:
                retryBtnClick();
                Logger.e(TAG, "Retry connection when click retry button");
                break;
            case R.id.main_btn_help:
                mFragmentManager.showFragment(HelpMainFragment.getInstance());
                break;
            case R.id.exit_img_btn:
                if (mActivity != null) {
                    mActivity.openExitAppDialog();
                }
                break;

            default:
                break;
        }
    }

    private class MsgMainFragmentHandler extends MsgBaseHandler {

        @Override
        public void handleMessage(Message msg) {
            Logger.d(TAG, "MsgMainFragmentHandler handleMessage get msg: " + CommonParams.getMsgName(msg.what));
            try {
                switch (msg.what) {
                    case CommonParams.MSG_CONNECT_STATUS_CONNECTED:
                        TimerUtils.INSTANCE.stop(mConnectionTask);
                        TimerUtils.INSTANCE.schedule(mProtocolInteractionTask, CONNECT_TIMEOUT_MS, CONNECT_TIMEOUT_MS);
                        break;
                    case CommonParams.MSG_CONNECT_STATUS_ESTABLISHED:
                        TimerUtils.INSTANCE.stop(mProtocolInteractionTask);
                        break;
                    case CommonParams.MSG_CONNECT_FAIL_AUTHEN_FAILED:
                        mConnectProgress.setVisibility(View.GONE);
                        mConnectInfo.setText(R.string.usb_connect_fail_authen_failed);
                        mRellayoutStatus.setVisibility(View.VISIBLE);
                        mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_connect_error));
                        TimerUtils.INSTANCE.stop(mProtocolInteractionTask);
                        break;
                    case CommonParams.MSG_CONNECT_STATUS_DISCONNECTED:
                        mConnectProgress.setVisibility(View.GONE);
                        mRellayoutStatus.setVisibility(View.VISIBLE);
                        mConnectInfo.setText(R.string.usb_connect_fail);
                        mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_connect_error));
                        TimerUtils.INSTANCE.stop(mConnectionTask);
                        break;
                    case CommonParams.MSG_CONNECT_FAIL_NOT_SURPPORT:
                        mConnectProgress.setVisibility(View.GONE);
                        if (mRetryBtn != null) {
                            mRetryBtn.setVisibility(View.VISIBLE);
                        }
                        mConnectInfo.setText(R.string.usb_connect_fail_not_surpport);
                        mRellayoutStatus.setVisibility(View.VISIBLE);
                        mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_connect_error));
                        TimerUtils.INSTANCE.stop(mProtocolInteractionTask);
                        break;
                    case CommonParams.MSG_CONNECT_CHANGE_PROGRESS_NUMBER:
                        int rate = msg.arg1;
                        try {
                            if (mConnectProgress.getVisibility() != View.VISIBLE) {
                                mConnectProgress.setVisibility(View.VISIBLE);
                                mConnectProgress.resetLoadingView();
                                mRellayoutStatus.setVisibility(View.GONE);
                            }
                            mConnectProgress.updateRate(rate);
                        } catch (Exception ex) {
                            Logger.e(TAG, "set progress number error");
                            ex.printStackTrace();
                        }
                        break;
                    case CommonParams.MSG_CMD_VIDEO_ENCODER_START:
                        mConnectProgress.setVisibility(View.VISIBLE);
                        mConnectProgress.loadFinished();
                        break;
                    case CommonParams.MSG_CONNECT_FAIL_TIMEOUT:
                        mConnectProgress.setVisibility(View.GONE);
                        mConnectInfo.setText(R.string.usb_connect_fail_timeout);
                        mRellayoutStatus.setVisibility(View.VISIBLE);
                        mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_connect_error));
                        TimerUtils.INSTANCE.stop(mConnectionTask);
                        break;
                    case CommonParams.MSG_FRAGMENT_REFRESH:
                        if (isAoaNotSupportADBNotOpen) {
                            isAoaNotSupportADBNotOpen = false;
                            mConnectProgress.setVisibility(View.GONE);
                            mRellayoutStatus.setVisibility(View.VISIBLE);
                            mConnectInfo.setText(R.string.usb_connect_fail);
                            mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_connect_error));
                            Logger.d(TAG, "ChangeUI:: MSG_FRAGMENT_REFRESH  AoaNotSupportADBNotOpen " );
                        }
                        break;
                    case CommonParams.MSG_CONNECT_FAIL_START:
                        mConnectProgress.setVisibility(View.GONE);
                        mRellayoutStatus.setVisibility(View.VISIBLE);
                        mConnectInfo.setText(R.string.usb_connect_fail_install);
                        mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_qr));
                        TimerUtils.INSTANCE.stop(mProtocolInteractionTask);
                         break;
                    case CommonParams.MSG_MAIN_DISPLAY_HELP_MAIN_FRAGMENT:
                        Logger.d(TAG, "showFragment(HelpMainFragment.getInstance()");
                        if (mFragmentManager != null) {
                            mFragmentManager.showFragment(HelpMainFragment.getInstance());
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Logger.e(TAG, "change main fragment get exception");
            }
        }

        @Override
        public void careAbout() {
            addMsg(CommonParams.MSG_CONNECT_STATUS_CONNECTED);
            addMsg(CommonParams.MSG_CONNECT_STATUS_ESTABLISHED);
            addMsg(CommonParams.MSG_CONNECT_STATUS_DISCONNECTED);
            addMsg(CommonParams.MSG_CONNECT_FAIL_START);
            addMsg(CommonParams.MSG_CONNECT_FAIL_NOT_SURPPORT);
            addMsg(CommonParams.MSG_CONNECT_FAIL_START_BDSC);
            addMsg(CommonParams.MSG_CONNECT_FAIL);
            addMsg(CommonParams.MSG_CONNECT_AOA_NO_PERMISSION);
            addMsg(CommonParams.MSG_CONNECT_AOA_REQUEST_MD_PERMISSION);
            addMsg(CommonParams.MSG_CONNECT_AOA_NOT_SUPPORT);
            addMsg(CommonParams.MSG_CONNECT_FAIL_TIMEOUT);
            addMsg(CommonParams.MSG_CONNECT_FAIL_AUTHEN_FAILED);

            addMsg(CommonParams.MSG_CONNECT_CHANGE_PROGRESS_NUMBER);
            addMsg(CommonParams.MSG_MAIN_DISPLAY_HELP_MAIN_FRAGMENT);

            addMsg(CommonParams.MSG_CMD_VIDEO_ENCODER_START);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mActivity != null) {
            mActivity.openExitAppDialog();
        }
        return true;
    }

    private Runnable mConnectionTask = new Runnable() {
        @Override
        public void run() {
            Logger.e(TAG, "Carlife Connect Timeout");
            MsgHandlerCenter.dispatchMessage(CommonParams.MSG_CONNECT_FAIL_TIMEOUT);
        }
    };

    private Runnable mProtocolInteractionTask = new Runnable() {
        @Override
        public void run() {
            Logger.e(TAG, "Carlife Protocol interaction Timeout");
            MsgHandlerCenter.dispatchMessage(CommonParams.MSG_CONNECT_FAIL_START);
        }
    };

    private void initParams() {
        if (wParams == null) {
            connectStatusHeight = getResources().getDimensionPixelSize(R.dimen.connect_status_height);
            Logger.d(TAG, "connectStatusHeight=" + connectStatusHeight);
            wParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            int screenHeigh = 12;//PhoneUtil.getInstance().getScreenHeight();
            int screenWidth = 13;//PhoneUtil.getInstance().getScreenWidth();
            Logger.d(TAG, "screenHeigh=" + screenHeigh + ",screenWidth=" + screenWidth);
            wParams.topMargin = ((screenHeigh - connectStatusHeight) * 2) / 3;
            wParams.bottomMargin = (screenHeigh - connectStatusHeight) / 3;
            Logger.d(TAG, "topMargin=" + wParams.topMargin + ",bottomMargin" + wParams.bottomMargin);
        }
        if (cParams == null) {
            cParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            cParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
    }

    public void updateExceptionTips(String exceptionTips) {
        if (isAoaNotSupportADBNotOpen) {
            String hintResStr = getResources().getString(R.string.usb_connect_aoa_request_md_permisson);
            if ( hintResStr.equals(exceptionTips) ) {
                return;
            }
        }

        if (!TextUtils.isEmpty(exceptionTips) && mConnectInfo != null) {
            mConnectProgress.setVisibility(View.GONE);
            mRellayoutStatus.setVisibility(View.VISIBLE);
            mImgView.setImageDrawable(getResources().getDrawable(R.drawable.car_ic_connect_error));
            mConnectInfo.setText(exceptionTips);
        }
    }
}