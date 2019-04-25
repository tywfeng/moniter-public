package com.tyw.moniter.main.ui;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import moniter.tyw.com.moniterlibrary.SDK.SDK;
import com.tyw.moniter.common.Interfaces;
import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.adapter.TagsListAdapter;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import moniter.tyw.com.moniterlibrary.Modbus.RTU.DeviceModbusRTU;
import moniter.tyw.com.moniterlibrary.Modbus.TCP.DeviceModbusTCPIPEthernet;
import moniter.tyw.com.moniterlibrary.Siemens.S7.DeviceSiemensS7TCPIPEthernet;
import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.Device;
import moniter.tyw.com.moniterlibrary.common.Tag;

public class TagsFragment extends Fragment implements Interfaces.OnMoniterDataChangedListener {

    // 数据模型
    private MoniterDataViewModel mMoniterData;
    // 设备信息
    private Device mDevice = null;
    // 控件变量
    private TextView mTextDeviceState, mTextDeviceKeyString, mTextDeviceDetail;
    // 可变参数变量
    private TextView mTextDeviceID, mTextDeviceSlaveID, mTextDeviceIPAddress, mTextDevicePort;
    private TextView mTextDeviceWriteMinInterval;
    // check变量
    private CheckBox mCheckSwappedFp, mCheckSwappedDbl, mCheckFirstWordLowIn32BitData, mCheckFirstDwordLowIn64BitData;
    private CheckBox mCheckWriteUsingCode15, mCheckWriteUsingCode16, mCheckWriteMatchAddrType;
    // 设备折叠控件
    private View mExpandDevice;
    private ImageView mViewExpandDeviceIcon;
    private View mFrameDeviceControl;
    private boolean bExpandDevice = false;        // 默认折叠
    // 列表变量
    private RecyclerView mListView;
    // 适配器
    private TagsListAdapter mTagsListAdapter;

    //线程UI同步
    Handler mHandler = new Handler();

    public static TagsFragment newInstance() {
        return new TagsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMoniterData = ViewModelProviders.of(getActivity()).get(MoniterDataViewModel.class);
        // TODO: Use the ViewModel
        initView();
    }

    private void findViews(View _rootView) {
        if (_rootView == null) return;
        mTextDeviceState = _rootView.findViewById(R.id.text_fragment_tags_device_state);
        mTextDeviceKeyString = _rootView.findViewById(R.id.text_fragment_tags_device_keystring);
        mTextDeviceDetail = _rootView.findViewById(R.id.text_fragment_tags_device_detail);
        mListView = _rootView.findViewById(R.id.list_tags);
        mCheckSwappedFp = _rootView.findViewById(R.id.checkbox_fragment_tags_device_swappedFP);
        mCheckSwappedDbl = _rootView.findViewById(R.id.checkbox_fragment_tags_device_swappedDbl);
        mCheckFirstWordLowIn32BitData = _rootView.findViewById(R.id.checkbox_fragment_tags_device_firstWordLowIn32BitData);
        mCheckFirstDwordLowIn64BitData = _rootView.findViewById(R.id.checkbox_fragment_tags_device_firstDwordLowIn64BitData);
        mCheckWriteUsingCode15 = _rootView.findViewById(R.id.checkbox_fragment_tags_deviceWrite_UsingCode15);
        mCheckWriteUsingCode16 = _rootView.findViewById(R.id.checkbox_fragment_tags_deviceWrite_UsingCode16);
        mCheckWriteMatchAddrType = _rootView.findViewById(R.id.checkbox_fragment_tags_deviceWrite_MatchAddrType);
        mTextDeviceID = _rootView.findViewById(R.id.text_fragment_tags_deviceid);
        mTextDeviceSlaveID = _rootView.findViewById(R.id.text_fragment_tags_deviceslaveid);
        mTextDeviceIPAddress = _rootView.findViewById(R.id.text_fragment_tags_deviceip);
        mTextDevicePort = _rootView.findViewById(R.id.text_fragment_tags_deviceport);
        mTextDeviceWriteMinInterval = _rootView.findViewById(R.id.text_fragment_tags_deviceWrite_minInterval);
        mExpandDevice = _rootView.findViewById(R.id.expand_device_toggle_control);
        mViewExpandDeviceIcon = _rootView.findViewById(R.id.btn_device_expand_icon);
        mFrameDeviceControl = _rootView.findViewById(R.id.frame_device_control);
    }

    // 初始化view
    private void initView() {
        // get device
        long lDevicePtr = Long.valueOf(getTag());
        mDevice = mMoniterData.SearchDevice(lDevicePtr);
        if (mDevice == null) return;

        if (mListView != null) {
            mTagsListAdapter = new TagsListAdapter(getContext());
            mTagsListAdapter.setHasStableIds(true);
            mListView.setAdapter(mTagsListAdapter);
            ((SimpleItemAnimator) mListView.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        // 事件监听
        mMoniterData.AddListener(this);
        mExpandDevice.setOnClickListener(mExpandDeviceClickListener);
        mTagsListAdapter.SetTags(mDevice.GetTags());
        // 更新view
        UpdateViews();
    }

    // 更新UI-view
    private void UpdateViews() {
        if (mDevice == null) return;
        // 禁用功能
        mCheckSwappedFp.setVisibility(View.GONE);
        mCheckSwappedDbl.setVisibility(View.GONE);
        mCheckFirstWordLowIn32BitData.setVisibility(View.GONE);
        mCheckFirstDwordLowIn64BitData.setVisibility(View.GONE);
        mCheckWriteUsingCode15.setVisibility(View.GONE);
        mCheckWriteUsingCode16.setVisibility(View.GONE);
        mCheckWriteMatchAddrType.setVisibility(View.GONE);
        mTextDeviceID.setVisibility(View.GONE);
        mTextDeviceSlaveID.setVisibility(View.GONE);
        mTextDeviceIPAddress.setVisibility(View.GONE);
        mTextDevicePort.setVisibility(View.GONE);
        mTextDeviceWriteMinInterval.setVisibility(View.GONE);

        mTextDeviceState.setText(mDevice.GetViewStateString());
        mTextDeviceKeyString.setText(mDevice.GetViewKeyString());
        mTextDeviceDetail.setText(mDevice.GetViewDetailString());
        // 折叠设备属性
        mViewExpandDeviceIcon.setImageResource(bExpandDevice ? R.drawable.btn_expand_on : R.drawable.btn_expand_off);
        mFrameDeviceControl.setVisibility(bExpandDevice ? View.VISIBLE : View.GONE);
        Channel channel = mDevice.GetChannel();
        if (channel != null && channel.GetType().IsType(DEFINE.ChannelType.SiemensS7TCPIPEthernet)) {
            if (mDevice instanceof DeviceSiemensS7TCPIPEthernet) { //siemens s7 tcp
                DeviceSiemensS7TCPIPEthernet s7Device = (DeviceSiemensS7TCPIPEthernet) mDevice;
                // 控件可见性
                mTextDeviceID.setVisibility(View.VISIBLE);
                mTextDevicePort.setVisibility(View.VISIBLE);
                mCheckSwappedFp.setVisibility(View.VISIBLE);
                mCheckFirstWordLowIn32BitData.setVisibility(View.VISIBLE);
                mCheckFirstDwordLowIn64BitData.setVisibility(View.VISIBLE);

                // 更新控件值
                mCheckSwappedFp.setChecked(s7Device.bSwappedFP);
                mCheckFirstWordLowIn32BitData.setChecked(s7Device.bFirstWORDLowIn32BitData);
                mCheckFirstDwordLowIn64BitData.setChecked(s7Device.bFirstDWORDLowIn64BitData);
                mTextDeviceID.setText(s7Device.GetViewId());
                mTextDevicePort.setText(s7Device.GetViewPortNumber());

                // 点击事件
                mTextDeviceID.setOnClickListener(mDevicePropertyClickListener);
                mTextDevicePort.setOnClickListener(mDevicePropertyClickListener);
                mCheckSwappedFp.setOnCheckedChangeListener(mCheckedChangeListener);
                mCheckFirstWordLowIn32BitData.setOnCheckedChangeListener(mCheckedChangeListener);
                mCheckFirstDwordLowIn64BitData.setOnCheckedChangeListener(mCheckedChangeListener);
            }
        } else if (channel != null && (channel.GetType().IsType(DEFINE.ChannelType.ModbusRTU) ||
                channel.GetType().IsType(DEFINE.ChannelType.ModbusTCPEthernet))) {
            // modbus RTU和modbus TCPEnthernet启用功能
            mCheckWriteUsingCode15.setVisibility(View.VISIBLE);
            mCheckWriteUsingCode16.setVisibility(View.VISIBLE);
            mCheckWriteMatchAddrType.setVisibility(View.VISIBLE);
            mCheckSwappedFp.setVisibility(View.VISIBLE);
            mCheckSwappedDbl.setVisibility(View.VISIBLE);
            mCheckFirstWordLowIn32BitData.setVisibility(View.VISIBLE);
            mCheckFirstDwordLowIn64BitData.setVisibility(View.VISIBLE);
            // 初始化
            if (channel.GetType().IsType(DEFINE.ChannelType.ModbusRTU) &&
                    mDevice instanceof DeviceModbusRTU) {
                // modbus rtu
                DeviceModbusRTU rtuDevice = (DeviceModbusRTU) mDevice;

                // 控件可见性
                mTextDeviceID.setVisibility(View.VISIBLE);
                mTextDeviceIPAddress.setVisibility(View.VISIBLE);
                mTextDevicePort.setVisibility(View.VISIBLE);
                mTextDeviceWriteMinInterval.setVisibility(View.VISIBLE);

                // 更新控件值
                mCheckWriteUsingCode15.setChecked(rtuDevice.bUsingWriteMultipleCoilsCode);
                mCheckWriteUsingCode16.setChecked(rtuDevice.bUsingWriteMultipleRegisgerCode);
                mCheckWriteMatchAddrType.setChecked(rtuDevice.bWriteMatchAddrType);
                mCheckSwappedFp.setChecked(rtuDevice.bSwappedFP);
                mCheckSwappedDbl.setChecked(rtuDevice.bSwappedDbl);
                mCheckFirstWordLowIn32BitData.setChecked(rtuDevice.bFirstWORDLowIn32BitData);
                mCheckFirstDwordLowIn64BitData.setChecked(rtuDevice.bFirstDWORDLowIn64BitData);
                mTextDeviceID.setText(rtuDevice.GetViewId());
                mTextDeviceIPAddress.setText(rtuDevice.GetViewIPAddress());
                mTextDevicePort.setText(rtuDevice.GetViewPortNumber());
                mTextDeviceWriteMinInterval.setText(rtuDevice.GetViewWriteMinInterval());

                // 点击事件
                mTextDeviceID.setOnClickListener(mDevicePropertyClickListener);
                mTextDeviceIPAddress.setOnClickListener(mDevicePropertyClickListener);
                mTextDevicePort.setOnClickListener(mDevicePropertyClickListener);
                mTextDeviceWriteMinInterval.setOnClickListener(mDevicePropertyClickListener);
            } else if (channel.GetType().IsType(DEFINE.ChannelType.ModbusTCPEthernet) &&
                    mDevice instanceof DeviceModbusTCPIPEthernet) {
                //modbus tcp
                DeviceModbusTCPIPEthernet tcpDevice = (DeviceModbusTCPIPEthernet) mDevice;

                // 控件可见性
                mTextDeviceID.setVisibility(View.VISIBLE);
                mTextDeviceSlaveID.setVisibility(View.VISIBLE);
                //mTextDeviceIPAddress.setVisibility(View.VISIBLE);
                mTextDevicePort.setVisibility(View.VISIBLE);
                mTextDeviceWriteMinInterval.setVisibility(View.VISIBLE);

                // 更新控件值
                mCheckWriteUsingCode15.setChecked(tcpDevice.bUsingWriteMultipleCoilsCode);
                mCheckWriteUsingCode16.setChecked(tcpDevice.bUsingWriteMultipleRegisgerCode);
                mCheckWriteMatchAddrType.setChecked(tcpDevice.bWriteMatchAddrType);
                mCheckSwappedFp.setChecked(tcpDevice.bSwappedFP);
                mCheckSwappedDbl.setChecked(tcpDevice.bSwappedDbl);
                mCheckFirstWordLowIn32BitData.setChecked(tcpDevice.bFirstWORDLowIn32BitData);
                mCheckFirstDwordLowIn64BitData.setChecked(tcpDevice.bFirstDWORDLowIn64BitData);
                mTextDeviceID.setText(tcpDevice.GetViewId());
                mTextDeviceSlaveID.setText(tcpDevice.GetViewSlaveID());
                //mTextDeviceIPAddress.setText(tcpDevice.strIPAddress);
                mTextDevicePort.setText(tcpDevice.GetViewPortNumber());
                mTextDeviceWriteMinInterval.setText(tcpDevice.GetViewWriteMinInterval());

                // 点击事件
                mTextDeviceID.setOnClickListener(mDevicePropertyClickListener);
                mTextDeviceSlaveID.setOnClickListener(mDevicePropertyClickListener);
                mTextDeviceIPAddress.setOnClickListener(mDevicePropertyClickListener);
                mTextDevicePort.setOnClickListener(mDevicePropertyClickListener);
                mTextDeviceWriteMinInterval.setOnClickListener(mDevicePropertyClickListener);
            }

            // 点击事件
            mCheckWriteUsingCode15.setOnCheckedChangeListener(mCheckedChangeListener);
            mCheckWriteUsingCode16.setOnCheckedChangeListener(mCheckedChangeListener);
            mCheckWriteMatchAddrType.setOnCheckedChangeListener(mCheckedChangeListener);
            mCheckSwappedFp.setOnCheckedChangeListener(mCheckedChangeListener);
            mCheckSwappedDbl.setOnCheckedChangeListener(mCheckedChangeListener);
            mCheckFirstWordLowIn32BitData.setOnCheckedChangeListener(mCheckedChangeListener);
            mCheckFirstDwordLowIn64BitData.setOnCheckedChangeListener(mCheckedChangeListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mDevicesListener = null;
        mMoniterData.RemoveListener(this);
    }

    @Override
    public void OnDeviceStateChangedNotify(final Device _device, final int state) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (_device == mDevice) {
                    mTextDeviceState.setText(DEFINE.DeviceState.GetStateWithValue(state).GetDescription());
                    mTextDeviceState.setTextColor(DEFINE.DeviceState.GetStateWithValue(state).GetColor());
                    mTagsListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void OnLoadConfigComplete() {

    }

    @Override
    public void OnDeviceReadOnceCompleted(final Device _device) {
        if (_device == null || _device != mDevice) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                List<Tag> tags = _device.GetTags();
                for (Tag tag : tags) {
                    if (tag == null) continue;
                    ;
                    if (tag.GetChart() == null) continue;
                    tag.GetChart().UpdateChartSrcDataFromTagLastData();
                }
                mTagsListAdapter.notifyDataSetChanged();
            }
        });
    }

    private View.OnClickListener mExpandDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            bExpandDevice = !bExpandDevice;
            UpdateViews();
        }
    };

    // 动态设置参数临时类型
    static DEFINE.DynamicSetParamType ChangeParamKey = DEFINE.DynamicSetParamType.Unknown;
    // 设备属性点击响应
    private View.OnClickListener mDevicePropertyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mDevice == null) return;
            // 变量声明 mTextDeviceID, mTextDeviceSlaveID, mTextDeviceIPAddress, mTextDevicePort
            String strMsg = "", strTitle = "", strEditString = "";
            boolean bShowDialog = false;
            // 初始化参数key
            ChangeParamKey = DEFINE.DynamicSetParamType.Unknown;
            if (view == mTextDeviceID) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ResetDeviceID;
                strMsg = getString(R.string.msg_editdeviceid_new_deviceid);
                strTitle = getString(R.string.title_editdeviceid_new_deviceid);
            } else if (view == mTextDeviceSlaveID) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ResetSlaveID;
                strMsg = getString(R.string.msg_editslaveid_new_slaveid);
                strTitle = getString(R.string.title_editslaveid_new_slaveid);
            } else if (view == mTextDeviceIPAddress) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ResetConnectIPAddress;
                strMsg = getString(R.string.msg_editip_new_ip);
                strTitle = getString(R.string.title_editip_new_ip);
            } else if (view == mTextDevicePort) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ResetPortNumber;
                strMsg = getString(R.string.msg_editport_new_port);
                strTitle = getString(R.string.title_editport_new_port);
            } else if (view == mTextDeviceWriteMinInterval) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.WriteMinIntervalMS;
                strMsg = getString(R.string.msg_editWriteMinInterval_new_interval);
                strTitle = getString(R.string.title_editWriteMinInterval_new_interval);
            }
            if (mDevice instanceof DeviceModbusRTU) {
                // Modbus RTU设备类型
                DeviceModbusRTU rtuDevice = (DeviceModbusRTU) mDevice;
                if (view == mTextDeviceID) strEditString = rtuDevice.GetId();
                if (view == mTextDeviceIPAddress) strEditString = rtuDevice.GetIPAddress();
                if (view == mTextDevicePort) strEditString = rtuDevice.GetPortNumber() + "";
                if (view == mTextDeviceWriteMinInterval)
                    strEditString = rtuDevice.GetWriteMinInterval() + "";
            } else if (mDevice instanceof DeviceModbusTCPIPEthernet) {
                // Modbus TCPIPEnthernet设备类型
                DeviceModbusTCPIPEthernet tcpDevice = (DeviceModbusTCPIPEthernet) mDevice;
                if (view == mTextDeviceID) strEditString = tcpDevice.GetId();
                if (view == mTextDeviceSlaveID) strEditString = tcpDevice.GetSlaveID() + "";
                //if (view == mTextDeviceIPAddress)strEditString=tcpDevice.strIPAddress;
                if (view == mTextDevicePort) strEditString = tcpDevice.GetPortNumber() + "";
                if (view == mTextDeviceWriteMinInterval)
                    strEditString = tcpDevice.GetWriteMinInterval() + "";
            } else if (mDevice instanceof DeviceSiemensS7TCPIPEthernet) {
                // Modbus TCPIPEnthernet设备类型
                DeviceSiemensS7TCPIPEthernet s7Device = (DeviceSiemensS7TCPIPEthernet) mDevice;
                if (view == mTextDeviceID) strEditString = s7Device.GetId();
                if (view == mTextDevicePort) strEditString = s7Device.GetPortNumber() + "";
            }
            if (!bShowDialog) return;
            // editText控件
            final EditText editInput = new EditText(getContext());
            // 提取更新edittext值
            editInput.setText(strEditString);
            // alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(strMsg)
                    .setTitle(strTitle)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setView(editInput).setCancelable(false)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String strRet = "";
                            String strEditInput = "";
                            int nEditInput = 0;
                            boolean bUsedStringValue = false, bUsedIntValue = false;
                            // 获取参数类型
                            if (mDevice instanceof DeviceModbusRTU) {
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetDeviceID))
                                    bUsedStringValue = true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetConnectIPAddress))
                                    bUsedStringValue = true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetPortNumber))
                                    bUsedIntValue = true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.WriteMinIntervalMS))
                                    bUsedIntValue = true;
                            } else if (mDevice instanceof DeviceModbusTCPIPEthernet) {
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetDeviceID))
                                    bUsedStringValue = true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetSlaveID))
                                    bUsedIntValue = true;
                                //if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetConnectIPAddress))bUsedStringValue=true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetPortNumber))
                                    bUsedIntValue = true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.WriteMinIntervalMS))
                                    bUsedIntValue = true;
                            } else if (mDevice instanceof DeviceSiemensS7TCPIPEthernet) {
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetDeviceID))
                                    bUsedStringValue = true;
                                if (ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ResetPortNumber))
                                    bUsedIntValue = true;
                            }
                            // 尝试提取数值
                            strEditInput = editInput.getText().toString();
                            if (bUsedIntValue) {
                                // 使用int类型参数
                                try {
                                    nEditInput = Integer.valueOf(strEditInput).intValue();
                                } catch (Exception e) {
                                    // 要求输入合法的整数型
                                    e.printStackTrace();
                                    Toast.makeText(getContext(),
                                            getString(R.string.hint_edittext_require_number_with_key, ChangeParamKey.GetName()),
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                strRet = SDK.getInstance().SetDeviceParam(mDevice.GetPtr(), ChangeParamKey.GetValue(), nEditInput);
                            } else if (bUsedStringValue) {
                                // 使用string类型参数
                                strRet = SDK.getInstance().SetDeviceParam(mDevice.GetPtr(), ChangeParamKey.GetValue(), strEditInput);
                            } else {
                                Toast.makeText(getContext(),
                                        getString(R.string.hint_edittext_none_support_type),
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            // success
                            if (DEFINE.strSetParamRetSuccess.equals(strRet)) {
                                Toast.makeText(getContext(),
                                        getString(R.string.msg_changeparam_success_with_key, ChangeParamKey.GetName()),
                                        Toast.LENGTH_LONG).show();
                                // 更改值
                                if (mDevice instanceof DeviceModbusRTU) {
                                    // Modbus RTU设备类型
                                    DeviceModbusRTU rtuDevice = (DeviceModbusRTU) mDevice;
                                    switch (ChangeParamKey) {
                                        case ResetDeviceID: {
                                            rtuDevice.SetId(strEditInput);
                                        }
                                        break;
                                        case ResetConnectIPAddress: {
                                            rtuDevice.SetIPAddress(strEditInput);
                                        }
                                        break;
                                        case ResetPortNumber: {
                                            rtuDevice.SetPortNumber(nEditInput);
                                        }
                                        break;
                                        case WriteMinIntervalMS: {
                                            rtuDevice.SetWriteMinInterval(nEditInput);
                                        }
                                        break;
                                    }
                                } else if (mDevice instanceof DeviceModbusTCPIPEthernet) {
                                    // Modbus TCPIPEnthernet设备类型
                                    DeviceModbusTCPIPEthernet tcpDevice = (DeviceModbusTCPIPEthernet) mDevice;
                                    switch (ChangeParamKey) {
                                        case ResetDeviceID: {
                                            tcpDevice.SetId(strEditInput);
                                        }
                                        break;
                                        case ResetSlaveID: {
                                            tcpDevice.SetSlaveID(nEditInput);
                                        }
                                        break;
                                        case ResetPortNumber: {
                                            tcpDevice.SetPortNumber(nEditInput);
                                        }
                                        break;
                                        case WriteMinIntervalMS: {
                                            tcpDevice.SetWriteMinInterval(nEditInput);
                                        }
                                        break;
                                    }
                                } else if (mDevice instanceof DeviceSiemensS7TCPIPEthernet) {
                                    // SiemensS7 TCPIPEnthernet设备类型
                                    DeviceSiemensS7TCPIPEthernet s7Device = (DeviceSiemensS7TCPIPEthernet) mDevice;
                                    switch (ChangeParamKey) {
                                        case ResetDeviceID: {
                                            s7Device.SetId(strEditInput);
                                        }
                                        break;
                                        case ResetPortNumber: {
                                            s7Device.SetPortNumber(nEditInput);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), strRet, Toast.LENGTH_LONG).show();
                            }
                            UpdateViews();
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };
    // checkbox点击事件响应
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
            if (mDevice == null) return;
            // 变量声明
            String strMsg = "";
            boolean bShowDialog = false;
            // 初始化参数key
            ChangeParamKey = DEFINE.DynamicSetParamType.Unknown;
            if (compoundButton == mCheckSwappedFp) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.SwappedFP;
            } else if (compoundButton == mCheckSwappedDbl) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.SwappedDbl;
            } else if (compoundButton == mCheckFirstWordLowIn32BitData) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.FirstWORDLowIn32BitData;
            } else if (compoundButton == mCheckFirstDwordLowIn64BitData) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.FirstDWORDLowIn64BitData;
            } else if (compoundButton == mCheckWriteUsingCode15) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ModbusWriteCoilsUsingCode15;
            } else if (compoundButton == mCheckWriteUsingCode16) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ModbusWriteRegUsingCode16;
            } else if (compoundButton == mCheckWriteMatchAddrType) {
                bShowDialog = true;
                ChangeParamKey = DEFINE.DynamicSetParamType.ModbusWriteMatchAddrType;
            }
            strMsg = b ? getString(R.string.msg_makesure_enable_control, mDevice.GetName(), ChangeParamKey.GetName()) :
                    getString(R.string.msg_makesure_disable_control, mDevice.GetName(), ChangeParamKey.GetName());


            // 重复过滤
            if (mDevice instanceof DeviceModbusRTU) {
                // Modbus RTU设备类型
                DeviceModbusRTU rtuDevice = (DeviceModbusRTU) mDevice;
                if (rtuDevice.bSwappedFP == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.SwappedFP))
                    return;
                if (rtuDevice.bSwappedDbl == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.SwappedDbl))
                    return;
                if (rtuDevice.bFirstWORDLowIn32BitData == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.FirstWORDLowIn32BitData))
                    return;
                if (rtuDevice.bFirstDWORDLowIn64BitData == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.FirstDWORDLowIn64BitData))
                    return;
                if (rtuDevice.bUsingWriteMultipleCoilsCode == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ModbusWriteCoilsUsingCode15))
                    return;
                if (rtuDevice.bUsingWriteMultipleRegisgerCode == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ModbusWriteRegUsingCode16))
                    return;
                if (rtuDevice.bWriteMatchAddrType == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ModbusWriteMatchAddrType))
                    return;

            } else if (mDevice instanceof DeviceModbusTCPIPEthernet) {
                // Modbus TCPIPEnthernet设备类型
                DeviceModbusTCPIPEthernet tcpDevice = (DeviceModbusTCPIPEthernet) mDevice;
                if (tcpDevice.bSwappedFP == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.SwappedFP))
                    return;
                if (tcpDevice.bSwappedDbl == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.SwappedDbl))
                    return;
                if (tcpDevice.bFirstWORDLowIn32BitData == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.FirstWORDLowIn32BitData))
                    return;
                if (tcpDevice.bFirstDWORDLowIn64BitData == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.FirstDWORDLowIn64BitData))
                    return;
                if (tcpDevice.bUsingWriteMultipleCoilsCode == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ModbusWriteCoilsUsingCode15))
                    return;
                if (tcpDevice.bUsingWriteMultipleRegisgerCode == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ModbusWriteRegUsingCode16))
                    return;
                if (tcpDevice.bWriteMatchAddrType == b && ChangeParamKey.IsType(DEFINE.DynamicSetParamType.ModbusWriteMatchAddrType))
                    return;

            }
            if (!bShowDialog) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(strMsg)
                    .setTitle(getString(R.string.title_changeparam_alert))
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            compoundButton.setChecked(!b);
                            dialogInterface.dismiss();
                        }
                    }).setCancelable(true)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            compoundButton.setChecked(!b);
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String strRet = SDK.getInstance().SetDeviceParam(mDevice.GetPtr(), ChangeParamKey.GetValue(), b ? 1 : 0);
                            // success
                            if (DEFINE.strSetParamRetSuccess.equals(strRet)) {
                                Toast.makeText(getContext(), getString(R.string.msg_changeparam_success_with_key, ChangeParamKey.GetName()), Toast.LENGTH_LONG).show();
                                // 更改值
                                if (mDevice instanceof DeviceModbusRTU) {
                                    // Modbus RTU设备类型
                                    DeviceModbusRTU rtuDevice = (DeviceModbusRTU) mDevice;
                                    switch (ChangeParamKey) {
                                        case SwappedFP: {
                                            rtuDevice.bSwappedFP = b;
                                        }
                                        break;
                                        case SwappedDbl: {
                                            rtuDevice.bSwappedDbl = b;
                                        }
                                        break;
                                        case FirstWORDLowIn32BitData: {
                                            rtuDevice.bFirstWORDLowIn32BitData = b;
                                        }
                                        break;
                                        case FirstDWORDLowIn64BitData: {
                                            rtuDevice.bFirstDWORDLowIn64BitData = b;
                                        }
                                        break;
                                        case ModbusWriteCoilsUsingCode15: {
                                            rtuDevice.bUsingWriteMultipleCoilsCode = b;
                                        }
                                        break;
                                        case ModbusWriteRegUsingCode16: {
                                            rtuDevice.bUsingWriteMultipleRegisgerCode = b;
                                        }
                                        break;
                                        case ModbusWriteMatchAddrType: {
                                            rtuDevice.bWriteMatchAddrType = b;
                                        }
                                        break;
                                    }
                                } else if (mDevice instanceof DeviceModbusTCPIPEthernet) {
                                    // Modbus TCPIPEnthernet设备类型
                                    DeviceModbusTCPIPEthernet tcpDevice = (DeviceModbusTCPIPEthernet) mDevice;
                                    switch (ChangeParamKey) {
                                        case SwappedFP: {
                                            tcpDevice.bSwappedFP = b;
                                        }
                                        break;
                                        case SwappedDbl: {
                                            tcpDevice.bSwappedDbl = b;
                                        }
                                        break;
                                        case FirstWORDLowIn32BitData: {
                                            tcpDevice.bFirstWORDLowIn32BitData = b;
                                        }
                                        break;
                                        case FirstDWORDLowIn64BitData: {
                                            tcpDevice.bFirstDWORDLowIn64BitData = b;
                                        }
                                        break;
                                        case ModbusWriteCoilsUsingCode15: {
                                            tcpDevice.bUsingWriteMultipleCoilsCode = b;
                                        }
                                        break;
                                        case ModbusWriteRegUsingCode16: {
                                            tcpDevice.bUsingWriteMultipleRegisgerCode = b;
                                        }
                                        break;
                                        case ModbusWriteMatchAddrType: {
                                            tcpDevice.bWriteMatchAddrType = b;
                                        }
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), strRet, Toast.LENGTH_LONG).show();
                                compoundButton.setChecked(!b);
                            }

                            dialogInterface.dismiss();
                        }
                    }).show();

        }
    };
}
