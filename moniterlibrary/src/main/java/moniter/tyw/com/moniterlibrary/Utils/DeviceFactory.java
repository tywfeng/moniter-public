package moniter.tyw.com.moniterlibrary.Utils;

import moniter.tyw.com.moniterlibrary.Modbus.RTU.DeviceModbusRTU;
import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.Device;

public class DeviceFactory {
    /**
     * 创建设备工厂
     * @param channel
     * @return
     */
    public static Device CreateFactory(Channel channel)
    {
        if(channel==null) return null;
        switch(channel.GetType())
        {
            case ModbusRTU:
            {
                return new DeviceModbusRTU(channel);
            }
            case ModbusTCPEthernet:
            {
                return new DeviceModbusRTU(channel);

            }
            case SiemensS7TCPIPEthernet:
            {
                return new DeviceModbusRTU(channel);
            }
            default:break;
        }
        return null;
    }
}
