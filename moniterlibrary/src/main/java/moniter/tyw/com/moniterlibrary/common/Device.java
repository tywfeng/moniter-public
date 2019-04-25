package moniter.tyw.com.moniterlibrary.common;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Device implements ConfigInterface, Comparable {
    // Identification
    protected int nIndex = 0;                                        // 排序索引
    protected String strDeviceName, strDeviceDescription, strId;    //  名、 描述 , Id
    protected Channel channelParent;                                // 所属channel
    protected long lDevicePtr;                                      // 设备JNI调用地址
    DEFINE.DeviceState mDeviceState = DEFINE.DeviceState.Undefined;// 设备连接状态

    public void SetStateWithValue(int _value) {
        mDeviceState = DEFINE.DeviceState.GetStateWithValue(_value);
    }

    public void SetState(DEFINE.DeviceState _state) {
        mDeviceState = _state;
    }

    public DEFINE.DeviceState getDeviceState() {
        return mDeviceState;
    }

    // 数据列表
    protected List<Tag> listTags = new ArrayList<Tag>();

    public Device(String _name, String _description, String _id) {
        strDeviceName = _name;
        strDeviceDescription = _description;
        strId = _id;
    }

    public Device(Channel _channel) {
        SetChannel(_channel);
    }

    // 构造函数 used by JNI call , 修改请注意C端代码
    protected Device(long _lPtr, int _index, int _state, String _name, String _description, String _id) {
        lDevicePtr = _lPtr;
        nIndex = _index;
        mDeviceState = DEFINE.DeviceState.GetStateWithValue(_state);
        strDeviceName = _name;
        strDeviceDescription = _description;
        strId = _id;
    }

    /**
     * 获取同chart组的taglist
     * @param _tag
     * @param _ignoreSelf 忽略自己
     * @return
     */
    public List<TagChart> GetTagsWithSameChartGroup(Tag _tag, boolean _ignoreSelf) {
        //检查点
        if (listTags == null || listTags.size() == 0) return null;
        // 遍历设备查找
        List<TagChart> tagCharts = new ArrayList<>();
        for (Tag tag : listTags) {
            if ( tag.GetChart().IsSameGroup(_tag.GetChart(),_ignoreSelf)) {
                tagCharts.add(tag.GetChart());
            }
        }
        return tagCharts;
    }

    // 设置通道
    public void SetChannel(Channel _channel) {
        channelParent = _channel;
        //SetDefaultValue();
    }


    // 获取状态信息
    public String GetViewStateString() {

        return mDeviceState.GetDescription();
    }

    // 获取关键信息
    public String GetViewKeyString() {

        return "";
    }

    // 获取详细信息
    public String GetViewDetailString() {

        return "";
    }

    public void AddTag(Tag tag) {
        if (tag == null || listTags == null) return;
        tag.SetDevice(this);
        listTags.add(tag);
    }

    public void RemoveAllTags() {
        // 预留
//        for(int i = 0 ; i < listTags.size();++i)
//        {
//            listTags.get(i).RemoveAllCharts();
//        }
        listTags.clear();
    }

    protected void SetDefaultValue() {
    }

    public Channel GetChannel() {
        return channelParent;
    }

    // 获取tag数量
    public int GetTagCount() {
        return listTags.size();
    }

    public List<Tag> GetTags() {
        return listTags;
    }

    public String GetName() {
        return strDeviceName;
    }

    public long GetPtr() {
        return lDevicePtr;
    }

    public void SetPtr(long _lptr) {
        lDevicePtr = _lptr;
    }

    public String GetId() {
        return strId;
    }
    public void SetId(String id)
    {
        strId=id;
    }
public String GetViewId()
{
    return new StringBuilder("DeviceId: ").append(strId).toString();
}

    public String GetDescription() {
        return strDeviceDescription;
    }

    // 计算申请分配字节缓存大小
    public int CalculateAllocateByteBufferSize() {
        return 0;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Device) {
            Device s = (Device) o;
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
