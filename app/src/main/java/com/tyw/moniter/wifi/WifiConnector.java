package com.tyw.moniter.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN;

public class WifiConnector {

    private static final String PREF_WIFI = "Pref_Wifi_Set";
    private static final String PREF_WIFI_CUSTOM_LIST = "Pref_Wifi_Custom_WifiList";

    private Activity mActivity;
    WifiManager mWifiManager;

    public WifiConnector(Activity _activity) {
        mActivity = _activity;
        mWifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager == null) {
            Toast.makeText(mActivity.getApplicationContext(), "get wifiManager failed!", Toast.LENGTH_SHORT).show();
        }
    }

    // 数据源
    private List<WifiDetail> mWifiList = new ArrayList<>();

    public List<WifiDetail> GetWifiList() {
        return mWifiList;
    }


    // 刷新wifi列表
    public List<WifiDetail> WifiRefresh() {
        if (mWifiManager == null) {
            Toast.makeText(mActivity.getApplicationContext(), "get wifiManager failed!", Toast.LENGTH_SHORT).show();
            return mWifiList;
        }
        // clean
        mWifiList.clear();
        // 当前连接
        WifiInfo currentWifi = mWifiManager.getConnectionInfo();
        // 加载历史
        List<WifiConfiguration> wifiConfigs = mWifiManager.getConfiguredNetworks();
        if (wifiConfigs != null && wifiConfigs.size() > 0) {
            for (WifiConfiguration config : wifiConfigs) {
                WifiDetail configItem = new WifiDetail();
                configItem.bCustomConfig = false;
                configItem.mWifiConfig = config;
                // 已连接
                if (currentWifi != null && currentWifi.getNetworkId() == config.networkId) {
                    configItem.SetConnectState(WifiDetail.ConnectState.Connected);
                    configItem.mWifiConnectInfo = currentWifi;
                } else {
                    // 未连接
                    configItem.SetConnectState(WifiDetail.ConnectState.Unconnected);
                }
                mWifiList.add(configItem);
            }
        }
        // 扫描
        mWifiManager.startScan();
        List<ScanResult> scanList = mWifiManager.getScanResults();
        if (scanList != null && scanList.size() > 0) {
            for (ScanResult scan : scanList) {
                WifiDetail wifiItem = null;
                boolean bFindInHistory = false;
                // 查找存在项
                for (WifiDetail searchItem : mWifiList) {
                    if (searchItem.mWifiConfig == null) continue;
                    if (!scan.SSID.isEmpty() && !searchItem.mWifiConfig.SSID.isEmpty()
                            && searchItem.mWifiConfig.SSID.equals('\"' + scan.SSID + '\"')) {
                        bFindInHistory = true;
                        wifiItem = searchItem;
                        break;
                    }
                }
                // 未找到
                if (!bFindInHistory) {
                    wifiItem = new WifiDetail();
                    wifiItem.bCustomConfig = false;
                }
                wifiItem.mWifiScanResult = scan;
                if (!bFindInHistory) {
                    mWifiList.add(wifiItem);
                }
            }
        }
        // 加载自定义
        LoadCustomConfig(false);

        // 排序
        Collections.sort(mWifiList);

        return mWifiList;
    }

    // 增加新自定义项
    public boolean AddCustomWifi(WifiDetail _wifi, boolean _updateIfExist) {
        if (_wifi == null || mWifiList == null) return false;
        // 校验是否存在
        for (WifiDetail item : mWifiList) {
            if (item.bCustomConfig && item.strSSID.equals(_wifi.strSSID) && item.strAlias.equals(_wifi.strAlias)) {
                // 如果存在则更新
                if (_updateIfExist) {
                    item.strPassword = _wifi.strPassword;
                    item.cipherType = _wifi.cipherType;
                    SaveCustomConfig();
                    return true;
                } else {
                    return false;
                }
            }
        }
        mWifiList.add(_wifi);
        // 排序
        Collections.sort(mWifiList);
        SaveCustomConfig();
        return true;
    }

    // 存储自定义WIFI配置
    public void SaveCustomConfig() {
        Set<String> wifiset = new HashSet<>();
        // 遍历列表并保存
        for (WifiDetail item : mWifiList) {
            // 过滤非自定义类型
            if (!item.bCustomConfig) continue;
            String setItem = item.toSerializeJSONString();
            wifiset.add(setItem);
        }
        SharedPreferences sharedPref = mActivity.getApplication().getSharedPreferences(PREF_WIFI
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(PREF_WIFI_CUSTOM_LIST, wifiset);
        editor.commit();
    }

    // 判断ssid是否存在
    public WifiConfiguration IsExistConfiguration(String _ssid) {
        if (_ssid == null) return null;
        for (WifiDetail item : mWifiList) {
            // 过滤自定义类型
            if ( item.mWifiConfig == null) continue;
            if (item.mWifiConfig.SSID.equals('\"' + _ssid + '\"')) return item.mWifiConfig;
        }
        return null;
    }

    // 移除wifi
    public void RemoveExistWifi(WifiConfiguration _existWifi) {
        if (_existWifi != null && mWifiManager != null) {
            mWifiManager.removeNetwork(_existWifi.networkId);
        }
        // 从缓存中移除
        Iterator<WifiDetail> iter = mWifiList.iterator();
        // 遍历处理fragment
        while (iter.hasNext()) {
            WifiDetail wifi = iter.next();
            if (wifi == null) continue;
            if (wifi.mWifiConfig == _existWifi) {
                iter.remove();
            }
        }
    }

    // 加载自定义WIFI配置
    public boolean LoadCustomConfig(boolean bClean) {

        SharedPreferences sharedPref = mActivity.getApplication().getSharedPreferences(PREF_WIFI
                , Context.MODE_PRIVATE);
        // 默认set
        Set<String> default_wifiset = new HashSet<String>();
        // 获取序列化数据
        Set<String> wifiset = sharedPref.getStringSet(PREF_WIFI_CUSTOM_LIST, default_wifiset);
        if (wifiset != null && wifiset.size() > 0) {
            // 清空缓存
            if (bClean) mWifiList.clear();
            // 反序列化数据并存储
            for (String item : wifiset) {
                WifiDetail wifi = new WifiDetail();
                wifi.fromJSONStringSerialize(item);
                mWifiList.add(wifi);
            }
        }
        return true;
    }

    // 创建连接信息
    public WifiConfiguration createWifiInfo(WifiDetail wifi) {
        if (wifi == null) return null;
        if (wifi.mWifiConfig != null) return wifi.mWifiConfig;
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + wifi.strSSID + "\"";
        // nopass
        if (wifi.cipherType == WifiDetail.CipherType.NoPass) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (wifi.cipherType == WifiDetail.CipherType.WEP) {
            if (!TextUtils.isEmpty(wifi.strPassword)) {
                if (isHexWepKey(wifi.strPassword)) {
                    config.wepKeys[0] = wifi.strPassword;
                } else {
                    config.wepKeys[0] = "\"" + wifi.strPassword + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (wifi.cipherType == WifiDetail.CipherType.WPA) {
            config.preSharedKey = "\"" + wifi.strPassword + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private static boolean isHexWepKey(String wepKey) {
        if (wepKey == null) return false;
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    // 打开wifi
    public boolean openWifi() {

        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            if (mWifiManager == null) {
                //Toast.makeText(mActivity.getApplicationContext(), "[OpenWifi]get wifiManager failed!", Toast.LENGTH_SHORT).show();
                return false;
            }
            // 启用wifi（权限？）
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    // 获取wifi状态
    public int getWifiState() {
        if (mWifiManager == null) return WIFI_STATE_UNKNOWN;
        return mWifiManager.getWifiState();
    }

    public WifiManager GetWifiManager()
    {
        return mWifiManager;
    }
}


