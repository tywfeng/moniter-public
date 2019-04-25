package com.tyw.moniter.main.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import moniter.tyw.com.moniterlibrary.Utils.DebugLog;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.snackbar.Snackbar;
import com.tyw.moniter.common.Options;
import com.tyw.moniter.common.PermissionUtils;
import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.Helper.BottomNavigationViewHelper;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // 权限request-code
    private static final int REQUEST_PERMISSION_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_PERMISSION_CHANGE_NETWORK_STATE = 2;
    private static final int REQUEST_PERMISSION_INTERNET = 3;
    private static final int REQUEST_PERMISSION_ACCESS_WIFI_STATE = 4;
    private static final int REQUEST_PERMISSION_CHANGE_WIFI_STATE = 5;
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 5;

    private TextView mTextMessage;
    private Fragment mFragmentWifi = null, mFragmentMoniter = null, mFragmentOptions = null, mFragmentWorkOrder;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_wifi:
                    // 隐藏其他
                    if (mFragmentMoniter != null)
                        transaction.hide(mFragmentMoniter);
                    if (mFragmentOptions != null)
                        transaction.hide(mFragmentOptions);
                    if (mFragmentWorkOrder != null)
                        transaction.hide(mFragmentWorkOrder);
                    if (mFragmentWifi == null) {
                        mFragmentWifi = new WifiFragment();
                        transaction.add(R.id.fragment_content, mFragmentWifi, "wifi");
                    }
                    transaction.show(mFragmentWifi).commit();
                    return true;
                case R.id.navigation_moniter:
                    // 隐藏其他
                    if (mFragmentWifi != null)
                        transaction.hide(mFragmentWifi);
                    if (mFragmentOptions != null)
                        transaction.hide(mFragmentOptions);
                    if (mFragmentWorkOrder != null)
                        transaction.hide(mFragmentWorkOrder);
                    if (mFragmentMoniter == null) {
                        mFragmentMoniter = new MoniterFragment();
                        transaction.add(R.id.fragment_content, mFragmentMoniter, "moniter");
                    }
                    transaction.show(mFragmentMoniter).commit();
                    return true;
                case R.id.navigation_workorder:
                    // 隐藏其他
                    if (mFragmentWifi != null)
                        transaction.hide(mFragmentWifi);
                    if (mFragmentMoniter != null)
                        transaction.hide(mFragmentMoniter);
                    if (mFragmentOptions != null)
                    transaction.hide(mFragmentOptions);
                    if (mFragmentWorkOrder == null) {
                        mFragmentWorkOrder = new WorkOrderFragment();
                        transaction.add(R.id.fragment_content, mFragmentWorkOrder, "workorder");
                    }
                    transaction.show(mFragmentWorkOrder).commit();
                    return true;
                case R.id.navigation_options:
                    // 隐藏其他
                    if (mFragmentWifi != null)
                        transaction.hide(mFragmentWifi);
                    if (mFragmentMoniter != null)
                        transaction.hide(mFragmentMoniter);
                    if (mFragmentWorkOrder != null)
                        transaction.hide(mFragmentWorkOrder);
                    if (mFragmentOptions == null) {
                        mFragmentOptions = new OptionsFragment();
                        transaction.add(R.id.fragment_content, mFragmentOptions, "options");
                    }
                    transaction.show(mFragmentOptions).commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_page);
        // initViews
        InitViews();

        // 初始化配置
        Options.InitAppConfig(this);

        // 权限判断
        if (!PermissionUtils.IsPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestStoragePermission(this, "请求访问存储权限", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_EXTERNAL_STORAGE, getWindow().getDecorView());
        }
        if (!PermissionUtils.IsPermission(getApplicationContext(), Manifest.permission.INTERNET)) {
            requestStoragePermission(this, "请求访问网络权限", Manifest.permission.INTERNET, REQUEST_PERMISSION_INTERNET, getWindow().getDecorView());
        }
        if (!PermissionUtils.IsPermission(getApplicationContext(), Manifest.permission.CHANGE_NETWORK_STATE)) {
            requestStoragePermission(this, "请求改变网络状态权限", Manifest.permission.CHANGE_NETWORK_STATE, REQUEST_PERMISSION_CHANGE_NETWORK_STATE, getWindow().getDecorView());
        }
        if (!PermissionUtils.IsPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE)) {
            requestStoragePermission(this, "请求WIFI状态读取权限", Manifest.permission.ACCESS_WIFI_STATE, REQUEST_PERMISSION_ACCESS_WIFI_STATE, getWindow().getDecorView());
        }
        if (!PermissionUtils.IsPermission(getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE)) {
            requestStoragePermission(this, "请求WIFI改变权限", Manifest.permission.CHANGE_WIFI_STATE, REQUEST_PERMISSION_CHANGE_WIFI_STATE, getWindow().getDecorView());
        }
        if (!PermissionUtils.IsPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestStoragePermission(this, "Android6.0以上扫描WIFI需要定位权限", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_ACCESS_FINE_LOCATION, getWindow().getDecorView());
        }

    }

    private void InitViews() {
        // bottom navigation
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // 初始显示tab
        navigation.setSelectedItemId(R.id.navigation_moniter);
        // 禁用显示完整文字
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

    // 动态权限申请
    protected void requestStoragePermission(final Activity _activity, String hintString, final String requestPermission, final int requestCode, View view) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(_activity, requestPermission)) {
            Snackbar.make(view, hintString, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(_activity, new String[]{requestPermission}, requestCode);
                        }
                    }).show();
        } else {
            Toast.makeText(_activity.getApplicationContext(), hintString, Toast.LENGTH_SHORT)
                    .show();
            ActivityCompat.requestPermissions(_activity, new String[]{requestPermission}, requestCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出提示dialog
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.makesure_quit_title))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.makesure_quit_content))
                    .setNegativeButton(getString(R.string.no), null)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HomeActivity.this.finish();
                            System.exit(0);
                        }
                    }).show();
            dialog.setCanceledOnTouchOutside(false);

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
}

