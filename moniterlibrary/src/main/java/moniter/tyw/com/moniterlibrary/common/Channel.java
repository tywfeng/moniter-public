package moniter.tyw.com.moniterlibrary.common;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Channel implements ConfigInterface, Comparable {

    // Identification
    protected int nIndex = 0;                                // 排序索引
    protected String strChannelName, strChannelDescription; //  名、 描述

    protected DEFINE.ChannelType channelType;               // 通道类型

    protected long lChannelPtr = 0L;                             // C端Ptr
    // 设备列表
    protected List<Device> listDevices = new ArrayList<Device>();

    /**
     * 构造函数
     *
     * @param _type
     * @param _name
     * @param _description
     */
    public Channel(DEFINE.ChannelType _type, String _name, String _description) {
        channelType = _type;
        strChannelName = _name;
        strChannelDescription = _description;
    }

    // used by JNI call , 修改请注意C端代码
    private Channel(long _lPtr, int _index, int _typeValue, String _name, String _description) {
        lChannelPtr = _lPtr;
        nIndex = _index;
        channelType = DEFINE.ChannelType.GetTypeWithValue(_typeValue);
        strChannelName = _name;
        strChannelDescription = _description;
    }

    protected void SetDefaultValue() {
    }

    public void AddDevice(Device device) {
        if (device == null || listDevices == null) return;
        device.SetChannel(this);
        listDevices.add(device);
    }

    public List<Device> getDevices() {
        return listDevices;
    }

    public void RemoveAllDevices() {
        for (int i = 0; i < listDevices.size(); ++i) {
            listDevices.get(i).RemoveAllTags();
        }
        listDevices.clear();
    }

    public DEFINE.ChannelType GetType() {
        return channelType;
    }

    public int GetDeviceCount() {
        return listDevices.size();
    }

    public String GetName() {
        return strChannelName;
    }

    public String GetDescription() {
        return strChannelDescription;
    }

    public long GetPtr() {
        return lChannelPtr;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof Channel) {
            Channel s = (Channel) o;
            if (this.nIndex > s.nIndex) {
                return 1;
            } else if (this.nIndex == s.nIndex) {
                return 0;
            } else {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public void OnConfigLoadCompleted() {

    }
}
