package com.tyw.moniter.main.ui;

import androidx.lifecycle.ViewModel;

import moniter.tyw.com.moniterlibrary.SDK.SDK;
import com.tyw.moniter.common.Interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import moniter.tyw.com.moniterlibrary.Utils.DebugLog;
import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.ConfigInterface;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.Device;
import moniter.tyw.com.moniterlibrary.common.Tag;
import moniter.tyw.com.moniterlibrary.common.TagData;

public class MoniterDataViewModel extends ViewModel implements SDK.OnSCADAListener {
    // TODO: Implement the ViewModel
    private List<Channel> mListChannel = new ArrayList<Channel>();
    private boolean mIsLoadedConfig = false;
    private SDK mSDK = null;
    // 数据更改监听事件列表
    private List<Interfaces.OnMoniterDataChangedListener> mDataChangedListenersList = new ArrayList<>();
    // 配置改变事件列表
    private List<ConfigInterface> mConfigListenersList = new ArrayList<>();

    public void AddListener(ConfigInterface _listener)
    {
        if(_listener==null)return;
        Iterator<ConfigInterface> iter = mConfigListenersList.iterator();
        // 遍历处理
        while (iter.hasNext()) {
            ConfigInterface _tf = iter.next();
            if(_tf==null)iter.remove();
            if(_listener==_tf)return;
        }
        mConfigListenersList.add(_listener);
    }
    public void RemoveListener(ConfigInterface _listener)
    {
        if(_listener==null)return;
        Iterator<ConfigInterface> iter = mConfigListenersList.iterator();
        // 遍历处理
        while (iter.hasNext()) {
            ConfigInterface _tf = iter.next();
            if (_tf == null) continue;
            if(_listener==_tf)
            {
                iter.remove();
                return;
            }
        }
    }
    // 增加监听
    public void AddListener(Interfaces.OnMoniterDataChangedListener _listener)
    {
        if(_listener==null)return;
        Iterator<Interfaces.OnMoniterDataChangedListener> iter = mDataChangedListenersList.iterator();
        // 遍历处理
        while (iter.hasNext()) {
            Interfaces.OnMoniterDataChangedListener _tf = iter.next();
            if(_tf==null)iter.remove();
            if(_listener==_tf)return;
        }
        mDataChangedListenersList.add(_listener);
    }
    // 移除监听
    public void RemoveListener(Interfaces.OnMoniterDataChangedListener _listener)
    {
        if(_listener==null)return;
        Iterator<Interfaces.OnMoniterDataChangedListener> iter = mDataChangedListenersList.iterator();
        // 遍历处理
        while (iter.hasNext()) {
            Interfaces.OnMoniterDataChangedListener _tf = iter.next();
            if (_tf == null) continue;
            if(_listener==_tf)
            {
                iter.remove();
                return;
            }
        }
    }
    // 加载配置
    public boolean LoadConfig(String _path, boolean _bAutoLoad) {
        // 重复加载
        if (_bAutoLoad && mIsLoadedConfig) return false;

        if (mSDK == null) {
            mSDK =SDK.getInstance();
            mSDK.Init();
        }
        boolean bSuccess = mSDK.LoadConfig(_path);
        if (!bSuccess) return false;
        // 清空事件
        mConfigListenersList.clear();

        // 加载标识
        mIsLoadedConfig = true;
        // 添加事件
        mSDK.AddDeviceListener(this);
        // 读取配置
        do {
            Channel channels[] = mSDK.GetChannelList();
            if (channels == null) break;
            Arrays.sort(channels);
            for (Channel channel : channels) {
                if (channel == null) continue;
                AddChannel(channel);
                // 注册配置事件
                AddListener(channel);
                // 读取设备
                Device devices[] = mSDK.GetDeviceList(channel.GetPtr());
                if (devices == null) continue;
                Arrays.sort(devices);
                for (Device device : devices) {
                    if (device == null) continue;
                    channel.AddDevice(device);
                    // 注册配置事件
                    AddListener(device);
                    // 读取点
                    Tag[] tags = mSDK.GetTagList(channel.GetPtr(), device.GetPtr());
                    if (tags == null) continue;
                    Arrays.sort(tags);
                    for (Tag tag : tags) {
                        if (tag == null) continue;
                        device.AddTag(tag);
                        // 注册配置事件
                        AddListener(tag);
                    }
                }
            }
        } while (false);
        // 遍历通知事件
        Iterator<ConfigInterface> iter = mConfigListenersList.iterator();
        // 遍历处理
        while (iter.hasNext()) {
            ConfigInterface _tf = iter.next();
            if (_tf == null)
            {
                iter.remove();
                continue;
            }
             _tf.OnConfigLoadCompleted();
        }
        return true;
    }

    // 设备状态回调
    @Override
    public void OnDeviceStateEvent(long _devicePtr, int _state) {
        Device device = SearchDevice(_devicePtr);
        if (device == null) return;
        device.SetStateWithValue(_state);
        DebugLog.d("[JAVA impl:]：OnDeviceStateEvent:[" + device.GetName() + "][" + DEFINE.DeviceState.GetStateWithValue(_state).GetName() + "]");
        for(Interfaces.OnMoniterDataChangedListener listener:mDataChangedListenersList)
        {
            if(listener==null)
            {
                continue;
            }
            listener.OnDeviceStateChangedNotify(device,_state);
        }
    }
    // 读点回调
    @Override
    public void OnDeviceReadDataEvent(long _lDevicePtr, long _lTagPtr, TagData _tagData) {
        if (_tagData != null) {
            String strBuffer = String.format("[Tag]JAVA: OnDeviceReadDataEvent:[%s][%s][%s][%s]", _tagData.GetQualityCode().GetName(),
                    _tagData.GetErrorCode().GetName(), _tagData.GetData().GetType().GetName(), _tagData.GetData().toString(0));
            DebugLog.d(strBuffer);
            Device device = SearchDevice(_lDevicePtr);
            if(device==null)return;
            Tag tag = GetTagWithPtr(device.GetChannel(),device,_lTagPtr);
            if(tag!=null) tag.SetNewReadValue(_tagData);
        }
    }

    // 轮询完毕回调
    @Override
    public void OnDeviceReadOnceCompleted(long _lDevicePtr, boolean _success) {
        Device device = SearchDevice(_lDevicePtr);
        if(device==null)return;
        for(Interfaces.OnMoniterDataChangedListener listener:mDataChangedListenersList)
        {
            if(listener==null)continue;
            listener.OnDeviceReadOnceCompleted(device );
        }
    }

    // 开关设备
    public void ToggleDevice(Device _device, boolean _on) {
        if (mSDK == null) return;
        Device device = SearchDevice(_device.GetPtr());
        if (device == null) return;
        mSDK.ToggleDeviceAcquisition(device.GetPtr(), _on);
    }

    // 切换设备开关状态
    public void SwitchToggleDevice(Device _device) {
        if (mSDK == null) return;
        Device device = SearchDevice(_device.GetPtr());
        if (device == null) return;
        boolean bAction =false;
        if(device.getDeviceState().IsState(DEFINE.DeviceState.Undefined)
                || device.getDeviceState().IsState(DEFINE.DeviceState.Disconnect)
                || device.getDeviceState().IsState(DEFINE.DeviceState.BeConnectedFailed)
                || device.getDeviceState().IsState(DEFINE.DeviceState.ConnectFailed)
                || device.getDeviceState().IsState(DEFINE.DeviceState.ListenerFailed))
        {
            bAction=true;
        }
        ToggleDevice(device, bAction);
    }

    public List<Channel> getChannelList() {
        return mListChannel;
    }
    // 添加通道至缓存
    public void AddChannel(Channel _channel) {
        if (_channel == null) return;
        mListChannel.add(_channel);
    }
    // 校验通道
    private boolean checkChannel(Channel _channel) {
        if (_channel == null) return false;
        for (Channel channel : mListChannel) {
            if (channel == _channel) return true;
        }
        return false;
    }
    // 通过ptr获取通道
    public Channel GetChannelWithPtr(long _lptr) {
        for (Channel channel : mListChannel) {
            if (channel == null) continue;
            if (channel.GetPtr() == _lptr) return channel;
        }
        return null;
    }
    // 添加设备至缓存
    public void AddDevice(Channel _channel, Device _device) {
        if (_channel == null || _device == null) return;
        for (Channel channel : mListChannel) {
            if (channel != _channel) continue;
            channel.AddDevice(_device);
            return;
        }
    }
    // 通过ptr获取设备
    public Device GetDeviceWithPtr(long _channelPtr, long _devicePtr) {
        Channel channel = GetChannelWithPtr(_channelPtr);
        if (channel == null) return null;
        List<Device> devices = channel.getDevices();
        if (devices == null) return null;
        for (Device device : devices) {
            if (device.GetPtr() == _devicePtr) return device;
        }
        return null;
    }
    // 检索设备
    public Device SearchDevice(long _lDevicePtr) {
        Device device = null;
        for (Channel channel : mListChannel) {
            if (channel == null) continue;
            device = GetDeviceWithPtr(channel, _lDevicePtr);
            if (device == null) continue;
            return device;
        }
        return null;
    }
    // 通过ptr获取设备
    public Device GetDeviceWithPtr(Channel _channel, long _devicePtr) {
        if (!checkChannel(_channel)) return null;

        return GetDeviceWithPtr(_channel.GetPtr(), _devicePtr);
    }
    // 添加点至缓存
    public void AddTag(Channel _channel, Device _device, Tag _tag) {
        if (_channel == null || _device == null || _tag == null) return;
        if (!checkChannel(_channel)) return;
        List<Device> devices = _channel.getDevices();
        if (devices == null) return;
        for (Device device : devices) {
            if (device != _device) continue;
            device.AddTag(_tag);
            return;
        }
    }
    // 检查设备是否存在
    private boolean checkDevice(Channel _channel, Device _device) {
        if (_device == null) return false;
        if (!checkChannel(_channel)) return false;
        List<Device> devices = _channel.getDevices();
        if (devices == null) return false;
        for (Device device : devices) {
            if (device == _device) return true;
        }
        return false;
    }
    // 通过ptr获取点
    public Tag GetTagWithPtr(Channel _channel, Device _device, long _tagPtr) {
        if (!checkDevice(_channel, _device)) return null;
        List<Tag> tags = _device.GetTags();
        for (Tag tag : tags) {
            if (tag == null) continue;
            if (tag.GetPtr() == _tagPtr) return tag;
        }
        return null;
    }


}
