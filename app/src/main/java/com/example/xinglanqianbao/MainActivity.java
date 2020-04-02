package com.example.xinglanqianbao;
/**
 * Created by 李福森 2020/1/08
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xinglanqianbao.tool.ACache;
import com.example.xinglanqianbao.ui.Login.loginActivity;

import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webview;
    private String mobile;
    private String appUserId;
    private String createTime;
    private String email;
    private String endTime;
    private int id;
    private String image;
    private String invitationCode;
    private boolean keystoreUrl;
    private String lastMobile;
    private String nickName;
    private String pwd;
    private int regDevice;
    private int regSource;
    private String sign;
    private String startTime;
    private int status;
    private int pageNumber;
    private int pageSize;
    private String orderBy;
    private   ACache mCache ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = findViewById(R.id.home);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebSettings settings = webview.getSettings();
        settings.setDomStorageEnabled(true);
        webview.addJavascriptInterface(new JsInterface(), "control");
        mCache = ACache.get(this);
        mobile  = mCache.getAsString("mobile");
        appUserId  = mCache.getAsString("appUserId");
        image  = mCache.getAsString("image");
        TextView mBtMainLogout = findViewById(R.id.bt_main_logout);
        mBtMainLogout.setText("这是什么？"+mobile+appUserId+image);

        //  hookWebView();
      //  initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i("11", "sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i("", "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if (sdkInt < 26) {//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory != null) {
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance);
                Log.i("11", "Hook success!");
            } else {
                Log.i("11", "Hook failed!");
            }
        } catch (Throwable e) {
            Log.w("11", e);
        }
    }

    private void initView() {
        // 初始化控件对象
        TextView mBtMainLogout = findViewById(R.id.bt_main_logout);
        // 绑定点击监听器
        mBtMainLogout.setOnClickListener(this);
        try {
            Bundle bundle = this.getIntent().getExtras();
            String code = bundle.getString("code");
            String data = bundle.getString("data");
            JSONObject jsonObject = new JSONObject(data);
            mobile = jsonObject.getString("mobile");
            appUserId = jsonObject.getString("appUserId");
            createTime = jsonObject.getString("createTime");
            email = jsonObject.getString("email");
            endTime = jsonObject.getString("endTime");
            id = jsonObject.getInt("id");
            image = jsonObject.getString("image");
            invitationCode = jsonObject.getString("invitationCode");
            keystoreUrl = jsonObject.getBoolean("keystoreUrl");
            lastMobile = jsonObject.getString("lastMobile");
            nickName = jsonObject.getString("nickName");
            pwd = jsonObject.getString("pwd");
            regDevice = jsonObject.getInt("regDevice");
            regSource = jsonObject.getInt("regSource");
            sign = jsonObject.getString("sign");
            startTime = jsonObject.getString("startTime");
            status = jsonObject.getInt("status");
            pageNumber = jsonObject.getInt("pageNumber");
            pageSize = jsonObject.getInt("pageSize");
            orderBy = jsonObject.getString("orderBy");
            webview.loadUrl("http://xland.one");
            //  webview.loadUrl("http://xland.one"+"?mobile="+mobile+"&appUserId="+appUserId+"&createTime="+createTime+"&email="+email+"&endTime="+endTime+"&id="+id+"&image="
            //        +image+"&invitationCode="+invitationCode+"&keystoreUrl="+keystoreUrl+"&lastMobile="+lastMobile+"&nickName="+nickName+"&pwd="+pwd+"&regDevice="+regDevice+"&regSource="
            //      +regSource+"&sign="+sign+"&startTime="+startTime+"&status="+status+"&pageNumber="+pageNumber+"&pageSize="+pageSize+"&orderBy="+orderBy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class JsInterface {

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            log("show toast success");
        }

        public String getMobile() {
            return mobile;
        }

        public void log(final String msg) {
            webview.post(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript: log(" + "'" + msg + "'" + ")");
                }
            });
        }

    }

    public void onClick(View view) {
        if (view.getId() == R.id.bt_main_logout) {
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
