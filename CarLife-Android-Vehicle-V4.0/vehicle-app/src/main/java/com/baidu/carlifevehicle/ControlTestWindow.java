package com.baidu.carlifevehicle;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.baidu.carlife.protobuf.CarlifeCarHardKeyCodeProto;
import com.baidu.carlife.protobuf.CarlifeCarSpeedProto;
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage;
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes;
import com.baidu.carlife.sdk.receiver.CarLife;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.KeyEvent.KEYCODE_DEL;
import static com.baidu.carlife.sdk.Constants.MIC_STATUS_NOT_SUPPORTED;
import static com.baidu.carlife.sdk.Constants.MIC_STATUS_USE_MOBILE_MIC;
import static com.baidu.carlife.sdk.Constants.MSG_CHANNEL_CMD;
import static com.baidu.carlife.sdk.Constants.MSG_CHANNEL_TOUCH;
import static com.baidu.carlife.sdk.Constants.NAVI_STATUS_IDLE;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_BACK;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_HOME;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MAIN;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MEDIA;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MEDIA_START;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MEDIA_STOP;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MOVE_DOWN;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MOVE_LEFT;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MOVE_RIGHT;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_MOVE_UP;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_NAV;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_NUMBER_CLEAR;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_NUMBER_DEL;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_OK;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_PHONE_CALL;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_PHONE_END;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_SEEK_ADD;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_SEEK_SUB;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_SELECTOR_NEXT;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_SELECTOR_PREVIOUS;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_TEL;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_VR_START;
import static com.baidu.carlife.sdk.internal.protocol.ServiceTypes.KEYCODE_VR_STOP;

public class ControlTestWindow implements OnClickListener {

    public static final String TAG = "ControlTestWindow";
    private static ControlTestWindow mInstance = null;

    private ViewGroup mRootView = null;
    private Context mContext = null;

    private PopupWindow mFullWindow = null;
    private View mFullWindowLayout = null;
    private EditText mVideoEt = null;
    //    private Button mVideoCommitBtn = null;
//    private Button mVideoSaveBtn = null;
    private ImageButton mExitBtn = null;
    private Button mHomeBtn = null;
    private Button mPhoneBtn = null;
    private Button mMapBtn = null;
    private Button mMeidaBtn = null;
    private Button mSeekSubBtn = null;
    private Button mSeekAddBtn = null;
    private Button mSeekSelectorPreviousBtn = null;
    private Button mSeekSelectorNextBtn = null;
    private Button mBackBtn = null;
    private Button mOkBtn = null;
    private Button mUpBtn = null;
    private Button mDownBtn = null;
    private Button mLeftBtn = null;
    private Button mRightBtn = null;
    private OutputStream mFout = null;
    private Button mMusicStart;
    private Button mMusicStop;
    private Button mRecordStart;
    private Button mRecordStop;
    private Button mNaviStop;
    private Button mPhoneCall;
    private Button mPhoneEnd;
    private Button mCarSpeed10km;
    private Button mCarSpeed2km;

    private Spinner v_spinner;
    private Button v_spinnerText;
    private String[] v_spinnerArray;
    private Map<String, Integer> v_spinnerMap;

    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/spspps.data";

    private ControlTestWindow() {
    }

    public static ControlTestWindow getInstance() {
        if (null == mInstance) {
            synchronized (ControlTestWindow.class) {
                if (null == mInstance) {
                    mInstance = new ControlTestWindow();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context, ViewGroup parent) {
        try {
            mContext = context;
            mRootView = parent;

            mFullWindowLayout = LayoutInflater.from(mContext).inflate(R.layout.test_window, null);
            mFullWindow = new PopupWindow(mFullWindowLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            mExitBtn = (ImageButton) mFullWindowLayout.findViewById(R.id.exit_img_btn);
            mExitBtn.setOnClickListener(this);

//            mVideoEt = (EditText) mFullWindowLayout.findViewById(R.id.video_test_et);
//            mVideoCommitBtn = (Button) mFullWindowLayout.findViewById(R.id.video_test_btn1);
//            mVideoSaveBtn = (Button) mFullWindowLayout.findViewById(R.id.video_test_btn2);
//            mVideoCommitBtn.setOnClickListener(this);
//            mVideoSaveBtn.setOnClickListener(this);

            mHomeBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_home);
            mPhoneBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_phone);
            mMapBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_map);
            mMeidaBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_media);
            mHomeBtn.setOnClickListener(this);
            mPhoneBtn.setOnClickListener(this);
            mMapBtn.setOnClickListener(this);
            mMeidaBtn.setOnClickListener(this);

            mSeekSubBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_seek_sub);
            mSeekAddBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_seek_add);
            mSeekSelectorPreviousBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_selector_previous);
            mSeekSelectorNextBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_selector_next);
            mBackBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_back);
            mSeekSubBtn.setOnClickListener(this);
            mSeekAddBtn.setOnClickListener(this);
            mSeekSelectorPreviousBtn.setOnClickListener(this);
            mSeekSelectorNextBtn.setOnClickListener(this);
            mBackBtn.setOnClickListener(this);

            mOkBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_ok);
            mUpBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_up);
            mDownBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_down);
            mLeftBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_left);
            mRightBtn = (Button) mFullWindowLayout.findViewById(R.id.btn_right);
            mOkBtn.setOnClickListener(this);
            mUpBtn.setOnClickListener(this);
            mDownBtn.setOnClickListener(this);
            mLeftBtn.setOnClickListener(this);
            mRightBtn.setOnClickListener(this);

            mMusicStart = (Button) mFullWindowLayout.findViewById(R.id.music_start);
            mMusicStop = (Button) mFullWindowLayout.findViewById(R.id.music_stop);
            mRecordStart = (Button) mFullWindowLayout.findViewById(R.id.record_start);
            mRecordStop = (Button) mFullWindowLayout.findViewById(R.id.record_stop);
            mNaviStop = (Button) mFullWindowLayout.findViewById(R.id.navi_stop);
            mPhoneCall = (Button) mFullWindowLayout.findViewById(R.id.phone_call);
            mPhoneEnd = (Button) mFullWindowLayout.findViewById(R.id.phone_end);
            mMusicStart.setOnClickListener(this);
            mMusicStop.setOnClickListener(this);
            mRecordStart.setOnClickListener(this);
            mRecordStop.setOnClickListener(this);
            mNaviStop.setOnClickListener(this);
            mPhoneCall.setOnClickListener(this);
            mPhoneEnd.setOnClickListener(this);

            mFullWindowLayout.findViewById(R.id.btn_not_support_mic).setOnClickListener(this);
            mFullWindowLayout.findViewById(R.id.btn_use_mobile_mic).setOnClickListener(this);
            mFullWindowLayout.findViewById(R.id.btn_clear).setOnClickListener(this);
            mFullWindowLayout.findViewById(R.id.btn_delete).setOnClickListener(this);

            mCarSpeed10km = (Button) mFullWindowLayout.findViewById(R.id.car_speed_10);
            mCarSpeed10km.setOnClickListener(this);
            mCarSpeed2km = (Button) mFullWindowLayout.findViewById(R.id.car_speed_02);
            mCarSpeed2km.setOnClickListener(this);

            mFullWindowLayout.findViewById(R.id.go_setting).setOnClickListener(this);



            v_spinnerArray = mContext.getResources().getStringArray(R.array.spinner_channel);
            v_spinnerMap = new HashMap<>();
            v_spinnerMap.put(v_spinnerArray[0], 0x00018003);
            v_spinnerMap.put(v_spinnerArray[1], 0x00018005);

            v_spinner = mFullWindowLayout.findViewById(R.id.spin);
            v_spinnerText = mFullWindowLayout.findViewById(R.id.spinText);
            v_spinnerText.setOnClickListener(this);
            v_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    v_spinnerText.setText(v_spinnerArray[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void displayWindow() {
        mFullWindow.setFocusable(true);
        mFullWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
    }

    public void closeWindow() {
        if (mFullWindow != null && mFullWindow.isShowing()) {
            mFullWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        CarLifeMessage message = CarLifeMessage.obtain(
                MSG_CHANNEL_TOUCH,
                ServiceTypes.MSG_TOUCH_CAR_HARD_KEY_CODE,
                0);

        switch (v.getId()) {
            case R.id.exit_img_btn:
                closeWindow();
                break;
            case R.id.btn_home:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MAIN)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_phone:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_TEL)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_map:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_NAV)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_media:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MEDIA)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_seek_sub:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_SEEK_SUB)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_seek_add:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_SEEK_ADD)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_selector_previous:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_SELECTOR_PREVIOUS)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_selector_next:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_SELECTOR_NEXT)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_back:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_BACK)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_ok:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_OK)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_up:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MOVE_UP)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_down:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MOVE_DOWN)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_left:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MOVE_LEFT)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_right:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MOVE_RIGHT)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.music_start:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MEDIA_START)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.music_stop:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_MEDIA_STOP)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.record_start:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_VR_START)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.record_stop:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_VR_STOP)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.navi_stop:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(NAVI_STATUS_IDLE)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.phone_call:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_PHONE_CALL)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.phone_end:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_PHONE_END)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_not_support_mic:
//                MIC_STATUS_NOT_SUPPORTED
                break;
            case R.id.btn_use_mobile_mic:
//                MIC_STATUS_USE_MOBILE_MIC
                break;
            case R.id.btn_delete:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_NUMBER_DEL)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.btn_clear:
                message.payload(
                        CarlifeCarHardKeyCodeProto.CarlifeCarHardKeyCode.newBuilder()
                                .setKeycode(KEYCODE_NUMBER_CLEAR)
                                .build());
                CarLife.receiver().postMessage(message);
                break;
            case R.id.car_speed_10:
                showToast("Car Speed: 10KM");
                sendCarVelocityToMD(10);
                break;
            case R.id.car_speed_02:
                showToast("Car Speed: 0KM");
                sendCarVelocityToMD(0);
                break;
            case R.id.go_setting:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.id.spinText:
                showToast(v_spinnerMap.get(v_spinnerText.getText().toString()).toString());
                CarLife.receiver().postMessage(MSG_CHANNEL_CMD, v_spinnerMap.get(v_spinnerText.getText().toString()));
                break;

            default:
                break;
        }

    }

    private void initFout() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mFout = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

//    private void sendCommandToMd(int moduleId, int statusId) {
//        CarlifeModuleStatus.Builder moduleStatusBuilder = CarlifeModuleStatus.newBuilder();
//        moduleStatusBuilder.setModuleID(moduleId);
//        // start
//        moduleStatusBuilder.setStatusID(statusId);
//        CarlifeModuleStatus carlifeModuleStatus = moduleStatusBuilder.build();
//        CarlifeCmdMessage command = new CarlifeCmdMessage(true);
//        command.setServiceType(CommonParams.MSG_CMD_MODULE_CONTROL);
//        command.setData(carlifeModuleStatus.toByteArray());
//        command.setLength(carlifeModuleStatus.getSerializedSize`());
//        Message msgTmp =
//                Message.obtain(null, command.getServiceType(), CommonParams.MSG_CMD_PROTOCOL_VERSION, 0, command);
//        ConnectClient.getInstance().sendMsgToService(msgTmp);
//    }

    private void sendCarVelocityToMD(int carSpeed) {

        CarLifeMessage message = CarLifeMessage.obtain(
                MSG_CHANNEL_CMD,
                ServiceTypes.MSG_CMD_CAR_VELOCITY,
                0);

        message.payload(
                CarlifeCarSpeedProto.CarlifeCarSpeed.newBuilder()
                        .setSpeed(carSpeed)
                        .setTimeStamp(System.currentTimeMillis())
                        .build());
        CarLife.receiver().postMessage(message);

    }
}
