package com.tyw.moniter.main.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.core.net.ConnectivityManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import moniter.tyw.com.moniterlibrary.Utils.DebugLog;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.tyw.moniter.main.ui.View.ExpandLinearLayout;
import com.tyw.moniter.wifi.WifiConnector;
import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.adapter.WifiListViewAdapter;
import com.tyw.moniter.wifi.WifiDetail;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WifiFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    // 列表及适配器
    RecyclerView mViewRecycler;
    WifiListViewAdapter mWifiListAdapter;
    // wifi连接管理器
    WifiConnector mWifiConnector;
    // 添加/编辑
    ImageButton mBtnExpandNewWifi;
    EditText mEditSSID, mEditAlias, mEditPwd;
    ExpandLinearLayout mExpandWifiNew;
    RadioButton mRadioWap, mRadioWep, mRadioNopass;
    Button mBtnNewWifiSubmit;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WifiFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WifiFragment newInstance(int columnCount) {
        WifiFragment fragment = new WifiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    // 广播
    private NetworkConnectChangedReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_wifi, container, false);

        findViews(viewFragment);
        testwifi();

        // 注册广播
        mReceiver = new NetworkConnectChangedReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mReceiver, mIntentFilter);
        return viewFragment;
    }

    private void findViews(View viewFragment) {
        mViewRecycler = viewFragment.findViewById(R.id.list_wifi);
        mWifiConnector = new WifiConnector(getActivity());
        viewFragment.findViewById(R.id.btn_wifilist_refresh).setOnClickListener(mRefreshWifi);
        // 添加
        mBtnExpandNewWifi = viewFragment.findViewById(R.id.btn_wifilist_newwifi);
        mEditSSID = viewFragment.findViewById(R.id.edit_wifi_new_ssid);
        mEditAlias = viewFragment.findViewById(R.id.edit_wifi_new_alias);
        mEditPwd = viewFragment.findViewById(R.id.edit_wifi_new_pwd);
        mExpandWifiNew = viewFragment.findViewById(R.id.expand_wifi_new);
        mRadioWap = viewFragment.findViewById(R.id.btnRadio_wifi_cipher_wpa);
        mRadioWep = viewFragment.findViewById(R.id.btnRadio_wifi_cipher_wep);
        mRadioNopass = viewFragment.findViewById(R.id.btnRadio_wifi_cipher_nopass);
        mBtnNewWifiSubmit = viewFragment.findViewById(R.id.btn_wifi_new_submit);
        // list
        mViewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mWifiListAdapter = new WifiListViewAdapter(getContext(), mWifiConnector.WifiRefresh(), mListListener);
        mViewRecycler.setAdapter(mWifiListAdapter);
        // init
        mExpandWifiNew.SetExpand(false, false, null);
        // 事件
        mBtnExpandNewWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bExpand = (mExpandWifiNew.getVisibility() == View.GONE) ? true : false;
                mExpandWifiNew.SetExpand(bExpand, false, null);
                if (bExpand)
                    mEditSSID.requestFocus();
            }
        });
        mBtnNewWifiSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnNewWifiSubmit();
            }
        });
    }

    // 提交新增wifi配置
    private void OnNewWifiSubmit() {
        // 检查参数
        WifiDetail.CipherType cipherType = WifiDetail.CipherType.Invalid;

        String strSSID = mEditSSID.getText().toString();
        String strAlias = mEditAlias.getText().toString();
        String strPwd = mEditPwd.getText().toString();
        if (mRadioWap.isChecked()) cipherType = WifiDetail.CipherType.WPA;
        else if (mRadioWep.isChecked()) cipherType = WifiDetail.CipherType.WEP;
        else if (mRadioNopass.isChecked()) cipherType = WifiDetail.CipherType.NoPass;

        // 参数校验
        if (cipherType.IsType(WifiDetail.CipherType.Invalid)) {
            Toast.makeText(getContext(), "please slect a cipher item!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strSSID.isEmpty()) {
            Toast.makeText(getContext(), "please input SSID item!", Toast.LENGTH_SHORT).show();
            mEditSSID.requestFocus();
            return;
        }
        if (strAlias.isEmpty()) {
            Toast.makeText(getContext(), "please input Alias item!", Toast.LENGTH_SHORT).show();
            mEditAlias.requestFocus();
            return;
        }

        if (mWifiConnector == null) return;
        WifiDetail newWifi = new WifiDetail();
        newWifi.bCustomConfig = true;
        newWifi.strSSID = strSSID;
        newWifi.strAlias = strAlias;
        newWifi.strPassword = strPwd;
        newWifi.cipherType = cipherType;
        mWifiConnector.AddCustomWifi(newWifi, true);
        mWifiListAdapter.notifyDataSetChanged();
    }


    private OnListFragmentInteractionListener mListListener = new OnListFragmentInteractionListener() {
        @Override
        public void onEditCustomConfig(WifiDetail item) {
            if (item == null) return;
            mExpandWifiNew.SetExpand(true, false, null);
            mEditSSID.setText(item.strSSID);
            mEditAlias.setText(item.strAlias);
            mEditPwd.setText(item.strPassword);
            if (item.cipherType.IsType(WifiDetail.CipherType.WPA)) mRadioWap.setChecked(true);
            else if (item.cipherType.IsType(WifiDetail.CipherType.WEP)) mRadioWep.setChecked(true);
            else if (item.cipherType.IsType(WifiDetail.CipherType.NoPass))
                mRadioNopass.setChecked(true);
        }

        @Override
        public void onConnectConfig(final WifiDetail item) {
            if (item == null) return;
            // 弹框提示
            if (item.mWifiScanResult != null && !item.bCustomConfig && item.mWifiConfig == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("无法连接提示").setMessage("请使用系统WLAN功能连接至[" + item.GetViewKeyword() + "]网络")
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                return;
            }
            // 弹框提示
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("连接网络提示").setMessage("确认要连接至[" + item.GetViewKeyword() + "]吗?")
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TryConnectWifiDetail(item);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    };

    // 当前连接状态
    enum ConnectWifiState {
        None,
        Connecting,
        Failed,
        Successed
    }

    // 当前连接状态
    private ConnectWifiState mConnectWifiState = ConnectWifiState.None;
    // 连接消息
    private static final int TRY_CONNECTWIFI_PARAM_ERROR = 0;       // 参数错误
    private static final int TRY_CONNECTWIFI_OPENWIFI_FAILED = 1;       // wifi打开失败
    private static final int TRY_CONNECTWIFI_CONNECTING = 2;       // wifi连接中
    private static final int TRY_CONNECTWIFI_EXCEPTION = 3;       // 连接出现异常参数错误
    private static final int TRY_CONNECTWIFI_ENABLE_FAILED = 4;       // wifi启用失败
    private static final int TRY_CONNECTWIFI_CREATE_CONFIG_FAILED = 5;       // wifi创建连接失败
    private static final int TRY_CONNECTWIFI_REMOVE_EXIST = 6;       // wifi移除存在的项
    private static final int TRY_CONNECTWIFI_SUCCESS = 7;       // wifi连接成功
    private static final int TRY_CONNECTWIFI_FAILED = 8;       // wifi连接失败

    // 尝试连接至wifi
    private void TryConnectWifiDetail(WifiDetail wifi) {
        if (wifi == null) return;
        // 冲突检测
        if (mConnectWifiState == ConnectWifiState.Connecting) {
            Toast.makeText(getContext(), "连接操作进行中，稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        // 开启连接线程
        new Thread(new ConnectWifiRunnable(wifi)).start();
    }

    // WIFI状态广播
    public class NetworkConnectChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();
            if (strAction == null) return;
            switch (strAction) {
                case ConnectivityManager.CONNECTIVITY_ACTION:       // 网络连接
                {
                    ConnectivityManager connManager = (ConnectivityManager) getActivity()
                            .getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connManager, intent);
                    if (info == null) return;
                    //DebugLog.v(info.getTypeName());
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                            Toast.makeText(getContext(), getString(R.string.wifi_connected_hint, info.getExtraInfo()), Toast.LENGTH_SHORT).show();

                        }

                        mWifiConnector.WifiRefresh();
                        mWifiListAdapter.notifyDataSetChanged();
                    }
                        //DebugLog.v("info:" + info.toString());
                       //DebugLog.v("get:" +info.getType()+","+ info.getSubtype()+","+info.getDetailedState()+","+info.getSubtypeName()+","+info.getExtraInfo());

                }
                break;
                default:
                    break;

            }
        }
    }

    // 连接wifi线程
    class ConnectWifiRunnable implements Runnable {
        private WifiDetail wifi;

        public ConnectWifiRunnable(WifiDetail _wifi) {
            wifi = _wifi;
        }

        @Override
        public void run() {
            try {
                // 参数检查
                if (wifi == null || mWifiConnector == null) {
                    Message msg = new Message();
                    msg.what = TRY_CONNECTWIFI_PARAM_ERROR;
                    msgHandler.sendMessage(msg);
                    return;
                }
                WifiManager wifiManager = mWifiConnector.GetWifiManager();
                if (wifiManager == null) {
                    Message msg = new Message();
                    msg.what = TRY_CONNECTWIFI_PARAM_ERROR;
                    msgHandler.sendMessage(msg);
                    return;
                }
                // 连接中状态
                {
                    Message msg = new Message();
                    msg.what = TRY_CONNECTWIFI_CONNECTING;
                    msgHandler.sendMessage(msg);
                }
                // 打开wifi功能
                if (!mWifiConnector.openWifi()) {
                    Message msg = new Message();
                    msg.what = TRY_CONNECTWIFI_OPENWIFI_FAILED;
                    msg.obj = wifi;
                    msgHandler.sendMessage(msg);
                    return;
                }
                //
                Thread.sleep(200);
                while (mWifiConnector.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个100毫秒检测……
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                    }
                }
                // 状态判断
                if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                    Message msg = new Message();
                    msg.what = TRY_CONNECTWIFI_ENABLE_FAILED;
                    msg.obj = wifi;
                    msgHandler.sendMessage(msg);
                    return;
                }
                // 获取连接信息
                WifiConfiguration wifiConfig = wifi.mWifiConfig;
                int netID;
                if (wifiConfig == null) {
                    // 创建连接信息
                    wifiConfig = mWifiConnector.createWifiInfo(wifi);
                    // 结果判断
                    if (wifiConfig == null) {
                        Message msg = new Message();
                        msg.what = TRY_CONNECTWIFI_CREATE_CONFIG_FAILED;
                        msg.obj = wifi;
                        msgHandler.sendMessage(msg);
                        return;
                    }
                    // 判断存在
                    WifiConfiguration existWifi = mWifiConnector.IsExistConfiguration(wifi.strSSID);
                    if (existWifi != null) {
                        mWifiConnector.RemoveExistWifi(existWifi);
                        msgHandler.sendEmptyMessage(TRY_CONNECTWIFI_REMOVE_EXIST);
                    }
                    //  添加至网络
                    netID = wifiManager.addNetwork(wifiConfig);
                } else {
                    netID = wifiConfig.networkId;
                }


                // 尝试连接
                boolean enable = wifiManager.enableNetwork(netID, true);
                //
                //boolean connected = wifiManager.reconnect();


                // 连接完成通知
                Message msg = new Message();
                msg.what = TRY_CONNECTWIFI_SUCCESS;
                msg.obj = wifi;
                msgHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = TRY_CONNECTWIFI_EXCEPTION;
                msg.obj = wifi;
                msgHandler.sendMessage(msg);
            }
        }
    }

    // 处理UI消息
    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRY_CONNECTWIFI_PARAM_ERROR: {
                    Toast.makeText(getContext(), "连接失败，参数错误!", Toast.LENGTH_SHORT).show();
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Failed;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_EXCEPTION: {
                    Toast.makeText(getContext(), "连接出现异常!", Toast.LENGTH_SHORT).show();
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Failed;
                    WifiDetail wifi = (WifiDetail) msg.obj;
                    if (wifi != null) wifi.mConnectState = WifiDetail.ConnectState.ConnectFailed;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_CONNECTING: {
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Connecting;
                    WifiDetail wifi = (WifiDetail) msg.obj;
                    if (wifi != null) wifi.mConnectState = WifiDetail.ConnectState.Connecting;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_OPENWIFI_FAILED: {
                    Toast.makeText(getContext(), "无法打开WIFI", Toast.LENGTH_SHORT).show();
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Failed;
                    WifiDetail wifi = (WifiDetail) msg.obj;
                    if (wifi != null) wifi.mConnectState = WifiDetail.ConnectState.ConnectFailed;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_ENABLE_FAILED: {
                    Toast.makeText(getContext(), "WIFI启用失败！", Toast.LENGTH_SHORT).show();
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Failed;
                    WifiDetail wifi = (WifiDetail) msg.obj;
                    if (wifi != null) wifi.mConnectState = WifiDetail.ConnectState.ConnectFailed;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_CREATE_CONFIG_FAILED: {
                    Toast.makeText(getContext(), "WIFI创建连接失败！", Toast.LENGTH_SHORT).show();
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Failed;
                    WifiDetail wifi = (WifiDetail) msg.obj;
                    if (wifi != null) wifi.mConnectState = WifiDetail.ConnectState.ConnectFailed;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_REMOVE_EXIST: {
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_FAILED: {
                    Toast.makeText(getContext(), "WIFI连接失败！", Toast.LENGTH_SHORT).show();
                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Failed;
                    WifiDetail wifi = (WifiDetail) msg.obj;
                    if (wifi != null) wifi.mConnectState = WifiDetail.ConnectState.ConnectFailed;
                    mWifiListAdapter.notifyDataSetChanged();
                }
                break;
                case TRY_CONNECTWIFI_SUCCESS: {

                    // fragment connect state
                    mConnectWifiState = ConnectWifiState.Successed;
                }
                break;


            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    @Override
    public void onDestroy() {
        // 反注册广播
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onEditCustomConfig(WifiDetail item);

        void onConnectConfig(WifiDetail item);
    }

    private void testwifi() {
    }


    private View.OnClickListener mRefreshWifi = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mWifiConnector != null && mWifiListAdapter != null) {
                mWifiListAdapter.RefreshWifi(mWifiConnector.WifiRefresh());
            }
        }
    };

}
