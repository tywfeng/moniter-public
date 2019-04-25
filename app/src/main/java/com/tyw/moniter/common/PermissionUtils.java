package com.tyw.moniter.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 权限工具，用于动态申请权限等
 */
public class PermissionUtils {
    public static boolean CheckPermission(Context _context, String _checkPermission) {
        //

        return ContextCompat.checkSelfPermission(_context, _checkPermission) == PackageManager.PERMISSION_GRANTED;
    }

    // 权限判断
    public static boolean IsPermission(Context _context,String _permission) {
        return CheckPermission(_context, _permission);
    }



}
