package moniter.tyw.com.moniterlibrary.Modbus.RTU;

import org.json.JSONObject;

import java.util.ArrayList;

import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.Device;

public class ChannelModbusRTU extends Channel {
    /**
     * 构造函数
     *
     * @param _type
     * @param _name
     * @param _description
     */
    public ChannelModbusRTU(DEFINE.ChannelType _type, String _name, String _description) {
        super(_type, _name, _description);
    }

    @Override
    protected void SetDefaultValue() {

        // 模拟数据
        listDevices = new ArrayList<Device>();
        Device device1 = new DeviceModbusRTU(this);
        listDevices.add(device1);
    }

}
