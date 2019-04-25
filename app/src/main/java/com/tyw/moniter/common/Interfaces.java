package com.tyw.moniter.common;

import com.tyw.moniter.main.ui.adapter.DevicesListViewAdapter;

import moniter.tyw.com.moniterlibrary.common.Device;

public class Interfaces {
    // 设备列表项被点击事件
    public  interface OnListDevicesListener {
        // TODO: Update argument type and name
        // 设备点击
        void onClickListDeviceItem(DevicesListViewAdapter.DevicesListItem item);
        // 切换状态点击
        void onClickListDeviceItemRunOrStop(DevicesListViewAdapter.DevicesListItem item);
//        // 设备位置点击
//        void onClickListDeviceItemLocationMap(DevicesListViewAdapter.DevicesListItem item);
    }
    // 监控模型数据改变事件
    public interface OnMoniterDataChangedListener
    {
        // 设备状态改变
        void OnDeviceStateChangedNotify(Device _device, int state);
        // 配置加载完毕事件
        void OnLoadConfigComplete();
        // 读取完成事件
        void OnDeviceReadOnceCompleted(Device _device);
    }
}
