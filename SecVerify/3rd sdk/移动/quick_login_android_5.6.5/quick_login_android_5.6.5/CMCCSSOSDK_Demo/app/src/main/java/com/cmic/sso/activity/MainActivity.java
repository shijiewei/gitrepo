package com.cmic.sso.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmic.sso.R;
import com.cmic.sso.sdk.AuthRegisterViewConfig;
import com.cmic.sso.sdk.AuthThemeConfig;
import com.cmic.sso.sdk.auth.AuthnHelper;
import com.cmic.sso.sdk.auth.TokenListener;
import com.cmic.sso.sdk.utils.rglistener.CustomInterface;
import com.cmic.sso.tokenValidate.Request;
import com.cmic.sso.tokenValidate.RequestCallback;
import com.cmic.sso.util.Constant;
import com.cmic.sso.util.MobileParmUtil;
import com.cmic.sso.util.SpUtils;
import com.cmic.sso.util.StringFormat;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener {

    protected String TAG = "MainActivity";
    private static final int RESULT = 0x111;
    protected static final int RESULT_OF_SIM_INFO = 0x222;
    private Context mContext;
    public String mResultString;
    private TextView phoneEt;
    private String mSDKVersion=null;
    private TokenListener mListener;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE_PRE = 1000;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE_IMPLICIT_LOGIN = 2000;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE_DISPLAY_LOGIN = 3000;
    private static final int CMCC_SDK_REQUEST_GET_PHONE_INFO_CODE = 1111;
    private static final int CMCC_SDK_REQUEST_MOBILE_AUTH_CODE = 2222;
    private static final int CMCC_SDK_REQUEST_LOGIN_AUTH_CODE = 3333;
    private static final int CMCC_SDK_REQUEST_TOKEN_VALIDATE_CODE = 4444;
    private static final int CMCC_SDK_REQUEST_PHONE_VALIDATE_CODE = 5555;
    private String mAccessToken;
    private AuthnHelper mAuthnHelper;
    public ResultDialog mResultDialog;
    private Button mTitleBtn,mBtn;
    private String []operatorArray = {"未知","移动","联通","电信"};
    private String []networkArray = {"未知","数据流量","纯WiFi","流量+WiFi"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initDynamicButton();
        initSDK();
        init();
    }

    private void initDynamicButton(){
        mTitleBtn = new Button(this);
        mTitleBtn.setText("其他");
        mTitleBtn.setTextColor(0xffffffff);
        mTitleBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTitleBtn.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        mTitleBtn.setLayoutParams(mLayoutParams);

        mBtn = new Button(this);
        mBtn.setText("其他方式登录");
        mBtn.setTextColor(0xff3a404c);
        mBtn.setBackgroundColor(Color.TRANSPARENT);
        mBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//        mBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        RelativeLayout.LayoutParams mLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        mLayoutParams1.setMargins(0, MobileParmUtil.dip2px(this,450),0,0);
        mBtn.setLayoutParams(mLayoutParams1);
    }

    private void initSDK(){
        Log.e(TAG, System.currentTimeMillis() + " ");
        System.out.print(System.currentTimeMillis());
        AuthnHelper.setDebugMode(true);
        mAuthnHelper = AuthnHelper.getInstance(mContext.getApplicationContext());
        mAuthnHelper.SMSAuthOn(true);
        mAuthnHelper.setAuthThemeConfig(new AuthThemeConfig.Builder()
                .setAuthNavTransparent(false)//授权页head是否隐藏
                .setNavColor(0xff0086d0)//导航栏颜色
                .setNavText("登录")//导航栏标题
                .setNavTextColor(0xffffffff)//导航栏字体颜色
                .setNavReturnImgPath("umcsdk_return_bg")//导航返回图标
                .setLogoImgPath("umcsdk_mobile_logo")//logo图片
                .setLogoWidthDip(70)//图片宽度
                .setLogoHeightDip(70)//图片高度
                .setLogoHidden(false)//logo图片隐藏
                .setNumberColor(0xff333333)//手机号码字体颜色
                .setNumberSize(18)////手机号码字体大小
                .setSwitchAccTextColor(0xff329af3)//切换账号字体颜色
                .setSwitchAccHidden(false)//切换账号是否隐藏
                .setLogBtnText("本机号码一键登录")//登录按钮文本
                .setLogBtnTextColor(0xffffffff)//登录按钮文本颜色
                .setLogBtnImgPath("umcsdk_login_btn_bg")//登录按钮背景
                .setAuthBGImgPath("umcsdk_rootbg")// 授权页背景图片
                .setClauseOne("应用自定义服务条款一","https://www.baidu.com")//条款1
                .setClauseTwo("应用自定义服务条款二","https://www.hao123.com")//条款2
                .setClauseColor(0xff666666,0xff0085d0)//条款颜色
                .setUncheckedImgPath("umcsdk_uncheck_image")//chebox未被勾选图片
                .setCheckedImgPath("umcsdk_check_image")//chebox被勾选图片
                .setSloganTextColor(0xff999999)//slogan文字颜色
                .setLogoOffsetY(100)//图片Y偏移量
//                .setLogoOffsetY(60)//横屏
                .setNumFieldOffsetY(170)//号码栏Y偏移量
//                .setNumFieldOffsetY(85)//横屏
                .setSloganOffsetY(230)//slogan声明标语Y偏移量
//                .setSloganOffsetY(115)//横屏
                .setLogBtnOffsetY(254)//登录按钮Y偏移量
//                .setLogBtnOffsetY(150)//横屏
                .setSwitchOffsetY(310)//切换账号偏移量
//                .setSwitchOffsetY(190)//横屏
                .setPrivacyOffsetY_B(30)//隐私条款Y偏移量
                .setSmsLogBtnText("短信验证码登录")//短信验证码按钮文字
                .setSmsLogBtnImgPath("umcsdk_login_btn_bg")//短信验证码登录按钮背景图片
                .setSmsLogBtnTextColor(0xffffffff)//短信验证码按钮文字颜色
                .setSmsNavText("短信验证码登录")//短信验证码标题栏文本
                .setSmsCodeImgPath("umcsdk_get_smscode_btn_bg")//短信验证码按钮背景
                .setSmsCodeBtnTextColor(0xffffffff)//获取短信验证码按钮text颜色
                .setSmsNavTransparent(false)//短信验证码页面head是否隐藏
                .setPrivacyState(false)//授权页check
                .build());
        Log.e(TAG, System.currentTimeMillis() + " ");
        mSDKVersion= AuthnHelper.SDK_VERSION;
    }

    private void initCustomView(){
        mAuthnHelper.addAuthRegistViewConfig("title_button",new AuthRegisterViewConfig.Builder()
                .setView(mTitleBtn)
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_TITLE_BAR)
                .setCustomInterface(new CustomInterface() {
                    @Override
                    public void onClick(Context context) {
                        Toast.makeText(context,"动态注册的其他按钮",Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
        ).addAuthRegistViewConfig("text_button",new AuthRegisterViewConfig.Builder()
                .setView(mBtn)
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .setCustomInterface(new CustomInterface() {
                    @Override
                    public void onClick(Context context) {
                        Toast.makeText(context,"动态注册的其他登录按钮",Toast.LENGTH_SHORT).show();
                        mAuthnHelper.quitAuthActivity();
                    }
                })
                .build()
        );
    }

    private void init() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.wap_login1).setOnClickListener(this);
        findViewById(R.id.get_user_info).setOnClickListener(this);
        findViewById(R.id.pre_getphone).setOnClickListener(this);
        findViewById(R.id.validate_phone_bt).setOnClickListener(this);
        findViewById(R.id.network_btn).setOnClickListener(this);
        findViewById(R.id.wap_login_diaplay).setOnClickListener(this);
        findViewById(R.id.del_scrip).setOnClickListener(this);
        TextView mVersionText = findViewById(R.id.text_version);
        mVersionText.setText(mSDKVersion);
        phoneEt = findViewById(R.id.phone_et);
        mResultDialog = new ResultDialog(mContext);
        mListener = new TokenListener() {
            @Override
            public void onGetTokenComplete(int SDKRequestCode, JSONObject jObj) {
                if (jObj != null) {
                    try {
                        //时间
                        Log.e(TAG,"SDKRequestCode"+SDKRequestCode);//调用方式不传SDKRequestCode时，该值默认为：-1
                        long phoneTimes = SpUtils.getLong("phonetimes");
                        jObj.put("phonetimes", System.currentTimeMillis() -  phoneTimes + "ms");
                        mResultString = jObj.toString();
                        mHandler.sendEmptyMessage(RESULT);
                        if (jObj.has("token")) {
                            mAccessToken = jObj.optString("token");
                            HashMap<String, String> map = new HashMap<>(2);
                            map.put("token", mAccessToken);
                            MobclickAgent.onEvent(MainActivity.this, "user_token", map);
                            if(null != mBtn){
                                mBtn.setText(mAccessToken);
                            }
                            mAuthnHelper.quitAuthActivity();
//                            mAuthnHelper.quitSmsActivity();
                        }
                        mTitleBtn = null;
                        mBtn = null;
                        MobclickAgent.onEvent(MainActivity.this, "getResult", jObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_user_info:
                getUserInfo(Constant.APP_ID, Constant.APP_KEY);
                break;
            case R.id.pre_getphone:
                PGWGetMobile();
                break;
            case R.id.wap_login1:
                implicitLogin();
                break;
            case R.id.validate_phone_bt:
                phoneValidate();
                break;
            case R.id.network_btn:
                getNetAndOprate();
                break;
            case R.id.wap_login_diaplay:
                displayLogin();
                break;
            case R.id.del_scrip:
                deleteScrip();
                break;
            default:
                break;
        }
    }

    private void deleteScrip() {
        mAuthnHelper.delScrip();
        Toast.makeText(this,"清除scrip成功",Toast.LENGTH_LONG).show();
    }

    private void displayLogin() {
        initDynamicButton();
        initCustomView();
        SpUtils.putLong("getPrePhoneTimes", 0);
        SpUtils.putLong("phonetimes", System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE_DISPLAY_LOGIN);
            }else {
                mAuthnHelper.loginAuth(Constant.APP_ID, Constant.APP_KEY , mListener, CMCC_SDK_REQUEST_LOGIN_AUTH_CODE);
            }
        }else {
            mAuthnHelper.loginAuth(Constant.APP_ID, Constant.APP_KEY , mListener, CMCC_SDK_REQUEST_LOGIN_AUTH_CODE);
        }
    }

    private void getUserInfo(String appid, String appkey) {
        tokenValidate(appid, appkey, mAccessToken, mListener);
    }

    public void tokenValidate(final String appId, final String appKey, final String token, final TokenListener listener) {
        Bundle values = new Bundle();
        values.putString("appkey", appKey);
        values.putString("appid", appId);
        values.putString("token", token);
        SpUtils.putLong("phonetimes", System.currentTimeMillis());
        Request.getInstance(mContext).tokenValidate(values, new RequestCallback() {
            @Override
            public void onRequestComplete(String resultCode, String resultDes, JSONObject jsonobj) {
                Log.i("Token校验结果：", jsonobj.toString());
                listener.onGetTokenComplete(CMCC_SDK_REQUEST_TOKEN_VALIDATE_CODE, jsonobj);
                String phone = jsonobj.optString("msisdn");
                HashMap<String, String> map = new HashMap<>(2);
                map.put("token", mAccessToken);
                map.put("msisdn", phone);
                MobclickAgent.onEvent(MainActivity.this, "user_phone", map);
                MobclickAgent.onEvent(MainActivity.this, "tokenValidateResult", jsonobj.toString());
            }
        });
    }

    /**
     * 需要权限：READ_PHONE_STATE， ACCESS_NETWORK_STATE
     * operatortype获取网络运营商: 0.未知 1.移动流量 2.联通流量网络 3.电信流量网络
     * networktype 网络状态：0未知；1流量 2 wifi；3 数据流量+wifi
     */
    private void getNetAndOprate(){
        JSONObject jsonObject = mAuthnHelper.getNetworkType(mContext);
        int operator,net;
        try {
            operator = Integer.parseInt(jsonObject.getString("operatortype"));
            net = Integer.parseInt(jsonObject.getString("networktype"));
            jsonObject.put("operatortype",operatorArray[operator]);
            jsonObject.put("networktype",networkArray[net]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mResultString = jsonObject.toString();
        mResultDialog.setResult(StringFormat.logcatFormat(mResultString));
    }

    private void phoneValidate(){
        phoneValidate(Constant.APP_ID, Constant.APP_KEY, mAccessToken, mListener);
    }

    public void phoneValidate(final String appId, final String appKey, final String token, final TokenListener listener) {
        Bundle values = new Bundle();
        values.putString("appkey", appKey);
        values.putString("appid", appId);
        values.putString("token", token);
        values.putString("phone", phoneEt.getText().toString());
        SpUtils.putLong("phonetimes", System.currentTimeMillis());
        Request.getInstance(mContext).phoneValidate(values, new RequestCallback() {
            @Override
            public void onRequestComplete(String resultCode, String resultDes, JSONObject jsonobj) {
                Log.i("Token校验结果：", jsonobj.toString());
                listener.onGetTokenComplete(CMCC_SDK_REQUEST_PHONE_VALIDATE_CODE, jsonobj);
            }
        });
    }


    private void PGWGetMobile(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE_PRE);
            }else {
                SpUtils.putLong("phonetimes", System.currentTimeMillis());
                mAuthnHelper.getPhoneInfo(Constant.APP_ID, Constant.APP_KEY, 8000, mListener, CMCC_SDK_REQUEST_GET_PHONE_INFO_CODE);
            }
        }else {
            SpUtils.putLong("phonetimes", System.currentTimeMillis());
            mAuthnHelper.getPhoneInfo(Constant.APP_ID, Constant.APP_KEY, 8000, mListener, CMCC_SDK_REQUEST_GET_PHONE_INFO_CODE);
        }
    }

    private void implicitLogin() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE_IMPLICIT_LOGIN);
            }else {
                SpUtils.putLong("phonetimes", System.currentTimeMillis());
                mAuthnHelper.mobileAuth(Constant.APP_ID, Constant.APP_KEY, mListener, CMCC_SDK_REQUEST_MOBILE_AUTH_CODE);
            }
        }else {
            SpUtils.putLong("phonetimes", System.currentTimeMillis());
            mAuthnHelper.mobileAuth(Constant.APP_ID, Constant.APP_KEY, mListener, CMCC_SDK_REQUEST_MOBILE_AUTH_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE_PRE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PGWGetMobile();
                } else {
                    mListener.onGetTokenComplete(CMCC_SDK_REQUEST_GET_PHONE_INFO_CODE, StringFormat.getLoginResult("200005", "用户未授权READ_PHONE_STATE"));
                }
                break;
            case PERMISSIONS_REQUEST_READ_PHONE_STATE_IMPLICIT_LOGIN:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    implicitLogin();
                } else {
                    mListener.onGetTokenComplete(CMCC_SDK_REQUEST_MOBILE_AUTH_CODE, StringFormat.getLoginResult("200005", "用户未授权READ_PHONE_STATE"));
                }
                break;
            case PERMISSIONS_REQUEST_READ_PHONE_STATE_DISPLAY_LOGIN:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLogin();
                } else {
                    mListener.onGetTokenComplete(CMCC_SDK_REQUEST_LOGIN_AUTH_CODE, StringFormat.getLoginResult("200005", "用户未授权READ_PHONE_STATE"));
                }
                break;
            default:
                break;
        }
    }

    Handler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> referenceActivity;
        private MyHandler(MainActivity activity){
            referenceActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESULT:
                    referenceActivity.get().mResultDialog.setResult(StringFormat
                            .logcatFormat(referenceActivity.get().mResultString));
                    break;
                case RESULT_OF_SIM_INFO:
                    referenceActivity.get().mResultDialog.setResult(referenceActivity.get().mResultString);
                default:
                    break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
