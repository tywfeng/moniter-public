package com.tyw.moniter.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class USBUtils {
    private static final String TAG = "UsbUtil";

    /**
     * 获取usb list
     *
     * @return
     */
    public static List<String> getStorageList() {
        List<String> storageList = new ArrayList<String>();
        ArrayList<String> exStorageMountPath = getExternalStorageMountPath();
        File sdcardFile = Environment.getExternalStorageDirectory();
        String sdcardPath = "";
        if (sdcardFile != null && sdcardFile.exists()) {
            sdcardPath = sdcardFile.getAbsolutePath();
        }

        if (!exStorageMountPath.contains(sdcardPath)) {
            exStorageMountPath.add(sdcardPath);
        }

        for (int i = exStorageMountPath.size() - 1; i >= 0; i--) {
            // 符号连接不扫描
            if (isSymlink(new File(exStorageMountPath.get(i)))) {
                continue;
            }
            storageList.add(exStorageMountPath.get(i));
        }

        return storageList;
    }

    /**
     * 是否为符号链接
     *
     * @param file
     * @return
     */
    private static boolean isSymlink(File file) {
        if (file == null) {
            return false;
        }
        File canon = null;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = null;
            try {
                canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
                return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 获取外部存储挂载路径
     *
     * @return
     */
    private static ArrayList<String> getExternalStorageMountPath() {
        ArrayList<String> exStorageMountPath = new ArrayList<String>();
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.contains("secure"))
                        continue;
                    if (line.contains("asec"))
                        continue;
                    if (line.startsWith("/dev/block/vold/")) {
                        String columns[] = line.split(" ");
                        if (columns != null && columns.length > 1) {
                            String element = columns[1];
                            Log.i(TAG, "/dev/block/vold/ element:" + element);
                            exStorageMountPath.add(element);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exStorageMountPath;
    }

    /**
     * 获取sdcard paths
     *
     * @param context
     * @return
     */
    public static String[] getVolumePaths(Context context) {
        String[] paths = null;
        StorageManager mStorageManager = (StorageManager) context
                .getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static class StorageInfo {
        public String path;
        public String state;
        public boolean isRemoveable;
        public StorageInfo(String path) {
            this.path = path;
        }
        public boolean isMounted() {
            return "mounted".equals(state);
        }
        @Override
        public String toString() {
            return "\n StorageInfo [path=" + path + ", state=" + state
                    + ", isRemoveable=" + isRemoveable + "]";
        }
    }

    public static List<StorageInfo> listAllStorage(Context context) {
        ArrayList<StorageInfo> storages = new ArrayList<StorageInfo>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path);

                    Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                    String state = (String) getVolumeState.invoke(storageManager, info.path);
                    info.state = state;

                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();
                    storages.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    public static List<StorageInfo> getAvaliableStorage(List<StorageInfo> infos) {
        List<StorageInfo> storages = new ArrayList<StorageInfo>();
        for (StorageInfo info : infos) {
            File file = new File(info.path);
            if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                if (info.isMounted()) {
                    storages.add(info);
                }
            }
        }

        return storages;
    }
}
