package com.tyw.moniter.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiDetail implements Comparable {
    public boolean bCustomConfig = false;                  // 是否为自定义配置
    // 自定义配置属性
    public String strAlias = "";                           // 别名
    public String strSSID = "";                                 // SSID
    public CipherType cipherType = CipherType.Invalid;                          // 加密类型
    public String strPassword = "";                             // 密码


    // 非自定义配置属性
    public ScanResult mWifiScanResult;                     // 扫描信息
    public WifiConfiguration mWifiConfig;                  // 已存配置信息
    public WifiInfo mWifiConnectInfo;                       // 连接信息

    // 运行信息
    public ConnectState mConnectState = ConnectState.Unconnected;                      // 当前连接状态
    public boolean bExpandDetail = false;

    public String GetViewKeyword() {
        if (bCustomConfig)
            return new StringBuilder(strAlias).append("(").append(strSSID).append(")").toString();

        if (mWifiScanResult != null) return mWifiScanResult.SSID;

        if (mWifiConfig != null) return mWifiConfig.SSID;

        return "";
    }

    public String GetViewState() {
        if (mConnectState == ConnectState.Connecting) return mConnectState.GetName();
        if (mConnectState == ConnectState.Connected) {
            StringBuilder builder = new StringBuilder();
            if (mWifiConnectInfo != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.append(mConnectState.GetName()).append("[").append(GetIPAddress(mWifiConnectInfo.getIpAddress()))
                            .append("][").append(mWifiConnectInfo.getLinkSpeed()).append(WifiInfo.LINK_SPEED_UNITS).append("][")
                            .append(mWifiConnectInfo.getFrequency()).append(WifiInfo.FREQUENCY_UNITS).append("][")
                            .append(mWifiConnectInfo.getRssi()).append(" dBm]");
                } else {
                    // API < 21
                    builder.append(mConnectState.GetName()).append("[").append(GetIPAddress(mWifiConnectInfo.getIpAddress()))
                            .append("][").append(mWifiConnectInfo.getLinkSpeed()).append(WifiInfo.LINK_SPEED_UNITS).append("][")
                            .append(mWifiConnectInfo.getRssi()).append(" dBm]");
                }
            }
            return builder.toString();
        }
        return "";
    }

    public String GetIPAddress(int ip) {
        return new StringBuilder().append(ip & 0xff).append(".").append((ip >> 8) & 0xff).append(".")
                .append((ip >> 16) & 0xff).append(".").append((ip >> 24) & 0xff).toString();
    }

    public String GetViewDetail() {
        if (bCustomConfig) {
            return new StringBuilder("[").append(cipherType.GetName()).append("][").append(strPassword).append("]").toString();
        }
        if (mWifiScanResult != null) return mWifiScanResult.toString();
        if (mWifiConfig != null) return mWifiConfig.toString();
        return "";
    }

    // 排序方式 扫描出的信号 - 自定义配置 - 系统保存的配置
    @Override
    public int compareTo(Object o) {
        if (o instanceof WifiDetail) {
            WifiDetail s = (WifiDetail) o;
            if(s.mWifiScanResult!=null) return 1;
            if(s.bCustomConfig && this.mWifiScanResult==null)return 1;
            if(s.bCustomConfig) return -1;
            return -1;
        }
        return 0;
    }

    /**
     * @return
     */
    public String toSerializeJSONString() {
        JSONObject jsonWifi = new JSONObject();

        try {
            jsonWifi.put("custom", bCustomConfig);
            jsonWifi.put("alias", strAlias);
            jsonWifi.put("ssid", strSSID);
            jsonWifi.put("cipher", cipherType.GetValue());
            jsonWifi.put("pwd", (strPassword == null ? "" : strPassword));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonWifi.toString();
    }

    public boolean fromJSONStringSerialize(String _serialize) {

        try {
            JSONObject jsonWifi = new JSONObject(_serialize);
            if (jsonWifi.isNull("custom")) {
                bCustomConfig = false;
                return false;
            }
            bCustomConfig = jsonWifi.getBoolean("custom");
            if (!bCustomConfig) return false;
            strAlias = jsonWifi.getString("alias");
            strSSID = jsonWifi.getString("ssid");
            cipherType = CipherType.GetStateWithValue(jsonWifi.getInt("cipher"));
            strPassword = jsonWifi.getString("pwd");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void SetConnectState(ConnectState connected) {
        mConnectState = connected;
    }

    public interface WifiListener {
        // wifi状态改变
        void OnWifiStateChanged(WifiDetail _wifi, ConnectState _state);
    }

    /**
     * wifi连接状态类型
     */
    public enum CipherType {

        Invalid(0x00, 0xff8d8d8d, "Invalid", "Invalid"),
        WPA(0x01, 0xff808080, "WPA", "WPA"),
        WEP(0x02, 0xff808080, "WEP", "WEP"),
        NoPass(0x03, 0xff808080, "NoPass", "NoPass");

        CipherType(int _value, int _colorVal, String _name, String _description) {
            typeValue = _value;
            colorValue = _colorVal;
            stateName = _name;
            stateDescription = _description;
        }

        public String GetName() {
            return stateName;
        }

        public String GetDescription() {
            return stateDescription;
        }

        public static CipherType GetStateWithValue(int _value) {
            for (CipherType ct : CipherType.values())
                if (ct.IsType(_value)) return ct;
            return Invalid;
        }

        public boolean IsType(CipherType _ds) {
            return (typeValue == _ds.typeValue);
        }

        public boolean IsType(int _value) {
            return (typeValue == _value);
        }

        public int GetColor() {
            return colorValue;
        }

        public int GetValue() {
            return typeValue;
        }

        // 类型值
        private int typeValue, colorValue;
        private String stateName, stateDescription;
    }

    /**
     * wifi连接状态类型
     */
    public enum ConnectState {

        Unconnected(0x00, 0xff8d8d8d, "未连接", "未连接"),
        Connecting(0x01, 0xff8d8d8d, "连接中...", "连接中..."),
        Connected(0x02, 0xff8d8d8d, "已连接", "已连接"),
        ConnectFailed(0x02, 0xff8d8d8d, "连接失败", "连接失败");

        ConnectState(int _value, int _colorVal, String _name, String _description) {
            stateValue = _value;
            colorValue = _colorVal;
            stateName = _name;
            stateDescription = _description;
        }

        public String GetName() {
            return stateName;
        }

        public String GetDescription() {
            return stateDescription;
        }

        public static ConnectState GetStateWithValue(int _value) {
            for (ConnectState ct : ConnectState.values())
                if (ct.IsState(_value)) return ct;
            return Unconnected;
        }

        public boolean IsState(ConnectState _ds) {
            return (stateValue == _ds.stateValue);
        }

        public boolean IsState(int _value) {
            return (stateValue == _value);
        }

        public int GetColor() {
            return colorValue;
        }

        // 类型值
        private int stateValue, colorValue;
        private String stateName, stateDescription;
    }

}
