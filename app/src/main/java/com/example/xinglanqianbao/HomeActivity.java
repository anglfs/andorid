package com.example.xinglanqianbao;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;
import android.hardware.fingerprint.FingerprintManager;
import android.util.Base64;
import android.util.Log;
import javax.crypto.Cipher;

import com.example.xinglanqianbao.data.DBOpenHelper;
import com.example.xinglanqianbao.data.User;
import com.example.xinglanqianbao.tool.ACache;
import com.example.xinglanqianbao.tool.App;
import com.example.xinglanqianbao.tool.BiometricPromptManager;
import com.example.xinglanqianbao.ui.Login.loginActivity;
import com.example.xinglanqianbao.ui.Register.RegisterActivity;
import com.example.xinglanqianbao.databinding.HomeBinding;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private HomeBinding homeBinding;
    private Handler handler = new Handler();
    private boolean fingerLoginEnable = false;
    private BiometricPromptManager mManager;
    private ACache aCache;
    private ACache cache;
    private String mobile ;
    private String appUserId ;
    private String image ;
    private int id;
    private DBOpenHelper mDBOpenHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.home);
        aCache = ACache.get(App.getContext());
        mDBOpenHelper = new DBOpenHelper(this);

        initview();
    }

    private void initview(){
        homeBinding.btLogin.setOnClickListener(this);
        homeBinding.btRes.setOnClickListener(this);
        homeBinding.textView5.setOnClickListener(this);
    }

    private void commonDialog(String titie , String content,String Status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(titie);// 设置标题
       //  builder.setIcon(R.drawable.ic_launcher);//设置图标
        builder.setMessage(content);// 为对话框设置内容
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(HomeActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if (Status.equals("1")){
            builder.setNeutralButton("开通指纹登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    openFingerLogin("15220229042");

                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (fingerLoginEnable){
                        fingerLogin();
                    }else {
                        Toast.makeText(HomeActivity.this, "请先开通指纹登录", Toast.LENGTH_LONG).show();
                    }
                    // TODO Auto-generated method stub
                }
            });
        }
        builder.create().show();// 使用show()方法显示对话框
    }
    private void checkHardware() {
        if (Build.VERSION.SDK_INT >= 23 ) {
            mManager = BiometricPromptManager.from(HomeActivity.this);
            if (mManager.isHardwareDetected() && mManager.hasEnrolledFingerprints() && mManager.isKeyguardSecure()){
                commonDialog("提示！","手机支持指纹登录!","1");
            }else {
                commonDialog("提示！","手机不支持指纹登录!","0");
            }
        }else {
            commonDialog("提示！","手机低于安全23，不支持指纹登录!","0");
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity

    }
    public void onClick(View view) {
        if (view.getId() == R.id.bt_login) {
            checkHardware();
        }
        if (view.getId() == R.id.bt_res) {
            Intent intent1 = new Intent(this, RegisterActivity.class);
            startActivity(intent1);
            finish();
        }
    }
    private void fingerLogin() {
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(true, new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSucceeded(FingerprintManager.AuthenticationResult result) {
                    try {
                        Cipher cipher = result.getCryptoObject().getCipher();
                        String text = aCache.getAsString("pwdEncode");
                        Log.i("test", "获取保存的加密密码: " + text);
                        byte[] input = Base64.decode(text, Base64.URL_SAFE);
                        byte[] bytes = cipher.doFinal(input);
                        /**
                         * 然后这里用原密码(当然是加密过的)调登录接口
                         */
                        Log.i("test", "解密得出的加密的登录密码: " + new String(bytes));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onSucceeded(android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {
                    try {
                        Cipher cipher = result.getCryptoObject().getCipher();
                        String text = aCache.getAsString("pwdEncode");
                        Log.i("test", "获取保存的加密密码: " + text);
                        byte[] input = Base64.decode(text, Base64.URL_SAFE);
                        byte[] bytes = cipher.doFinal(input);
                        /**
                         * 然后这里用原密码(当然是加密过的)调登录接口
                         */
                        Log.i("test", "解密得出的原始密码: " + new String(bytes));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {
                }

                @Override
                public void onError(int code, String reason) {
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    private void openFingerLogin(final String pwd) {
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(false, new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSucceeded(FingerprintManager.AuthenticationResult result) {
                    try {
                        /**
                         * 加密后的密码和iv可保存在服务器,登录时通过接口根据账号获取
                         */
                        Log.i("test", "原密码: " + pwd);
                        Cipher cipher = result.getCryptoObject().getCipher();
                        byte[] bytes = cipher.doFinal(pwd.getBytes());
                        Log.i("test", "设置指纹时保存的加密密码: " + Base64.encodeToString(bytes,Base64.URL_SAFE));
                        aCache.put("pwdEncode", Base64.encodeToString(bytes,Base64.URL_SAFE));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "设置指纹时保存的加密IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        aCache.put("iv", Base64.encodeToString(iv,Base64.URL_SAFE));
                        fingerLoginEnable = true;
                        Toast.makeText(HomeActivity.this, "开通成功", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onSucceeded(BiometricPrompt.AuthenticationResult result) {
                    try {
                        Cipher cipher = result.getCryptoObject().getCipher();
                        byte[] bytes = cipher.doFinal(pwd.getBytes());
                        //保存加密过后的字符串
                        Log.i("test", "设置指纹保存的加密密码: " + Base64.encodeToString(bytes,Base64.URL_SAFE));
                        aCache.put("pwdEncode", Base64.encodeToString(bytes,Base64.URL_SAFE));
                        byte[] iv = cipher.getIV();
                        Log.i("test", "设置指纹保存的加密IV: " + Base64.encodeToString(iv,Base64.URL_SAFE));
                        aCache.put("iv", Base64.encodeToString(iv,Base64.URL_SAFE));
                        fingerLoginEnable = true;
                        Toast.makeText(HomeActivity.this, "开通成功", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {
                }

                @Override
                public void onError(int code, String reason) {
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

}
