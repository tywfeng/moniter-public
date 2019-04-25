package moniter.tyw.com.moniterlibrary.Modbus.TCP;

import moniter.tyw.com.moniterlibrary.Modbus.RTU.ChannelModbusRTU;
import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.DataType;
import moniter.tyw.com.moniterlibrary.common.Device;

public class DeviceModbusTCPIPEthernet extends Device {
    // Ethernet
    //private String strIPAddress; // instead of strId
    private int nPort;
    private int nSlaveID;

    // data encoding
    // 在32位数据中第一个WORD为低位 default TRUE 包括:FLOAT, DWORD, LONG, LBCD, DOUBLE
    public boolean bFirstWORDLowIn32BitData = true;
    // 64位数据中第一个DWORD为低位 default TRUE 包括:DOUBLE
    public boolean bFirstDWORDLowIn64BitData = true;
    public boolean bSwappedFP; // 浮点数高低位反序 default TRUE
    public boolean bSwappedDbl; // 双精度doble高低位反序 default TRUE

    // trming 时间限制
    private int nConnectTimeout; // seconds
    private int nRequestTimeoutMS; // millseconds
    private int nFailAfterSuccessiveTimeouts; // 连续N次超时后失败
    private int nReconnectWhenAfterSuccessiveFail; // 连续N次失败后重连
    private int nInterRequestDelayMS; // 网络请求最小延迟：毫秒

    // 基本信息
    private DEFINE.ScanMode scanMode = DEFINE.ScanMode.RespectClientSpecifiedScanRate;
    private int nScanRateMS = 1000;                                  // 扫描频率(毫秒)2种Request模式下生效
    private int nDeviceModel;               // 设备模型

    // write写变量
    public boolean bUsingWriteMultipleCoilsCode;       // 支持15号命令
    public boolean bUsingWriteMultipleRegisgerCode;    // 支持16号命令
    public int nWriteMinInterval;        // 写最少间隔时间(MS)
    public boolean bWriteMatchAddrType;                    // 匹配地址类型(input完全对应input)
    // 运行变量

    // 构造函数 used by JNI call , 修改请注意C端代码
    protected DeviceModbusTCPIPEthernet(long _lPtr, int _index, int _state, String _name, String _description, String _id,
                                        int _port, int _slaveid, boolean _firstWORDLowIn32BitData, boolean _firstDWORDLowIn64BitData,
                                        boolean _swappedFP, boolean _swappedDbl, int _connectTimeout, int _requestTimeoutMS,
                                        int _failAfterSuccessiveTimeouts, int _reconnectWhenAfterSuccessiveFail, int _interRequestDelayMS,
                                        int _scanModeValue, int _scanRateMS, int _deviceModel,
                                        boolean _useCode15, boolean _useCode16, int _writeMinInterval, boolean _writeMatchAddreType) {
        super(_lPtr, _index, _state, _name, _description, _id);
        //strIPAddress = _id;
        nPort = _port;
        nSlaveID = _slaveid;
        bFirstWORDLowIn32BitData = _firstWORDLowIn32BitData;
        bFirstDWORDLowIn64BitData = _firstDWORDLowIn64BitData;
        bSwappedFP = _swappedFP;
        bSwappedDbl = _swappedDbl;
        nConnectTimeout = _connectTimeout;
        nRequestTimeoutMS = _requestTimeoutMS;
        nFailAfterSuccessiveTimeouts = _failAfterSuccessiveTimeouts;
        nReconnectWhenAfterSuccessiveFail = _reconnectWhenAfterSuccessiveFail;
        nInterRequestDelayMS = _interRequestDelayMS;
        scanMode = DEFINE.ScanMode.GetScanModeWithValue(_scanModeValue);
        nScanRateMS = _scanRateMS;
        nDeviceModel = _deviceModel;
        bUsingWriteMultipleCoilsCode = _useCode15;
        bUsingWriteMultipleRegisgerCode = _useCode16;
        nWriteMinInterval = _writeMinInterval;
        bWriteMatchAddrType = _writeMatchAddreType;
    }

    // 构造函数
    protected DeviceModbusTCPIPEthernet(Channel _channel) {
        super(_channel);
    }

    //
    public DEFINE.ScanMode GetScanMode() {
        return scanMode;
    }

    public int GetScanRateMS() {
        return nScanRateMS;
    }


    @Override
    protected void SetDefaultValue() {
        // Identification
        strDeviceName = "DeviceName_ModbusRTU";
        strDeviceDescription = "Device description ModbusRTU";
        strId = "1";    //  名、 描述 , Id

        // Ethernet
        //strIPAddress = "192.168.0.100";
        nPort = 502;
        nSlaveID = 1;

        // data encoding
        bFirstWORDLowIn32BitData = true;       // 在32位数据中第一个WORD为低位 default TRUE
        bFirstDWORDLowIn64BitData = true;      // 64位数据中第一个DWORD为低位 default TRUE

        //scan
        scanMode = DEFINE.ScanMode.RespectClientSpecifiedScanRate;
        nScanRateMS = 1000;                                  // 扫描频率(毫秒)2种Request模式下生效

        // 模拟数据
    }


    @Override
    public int CalculateAllocateByteBufferSize() {
        return 0;
    }


    public void SetSlaveID(int _slaveid) {
        nSlaveID = _slaveid;
    }

    public int GetSlaveID() {
        return nSlaveID;
    }

    public void SetPortNumber(int _port) {
        nPort = _port;
    }

    public int GetPortNumber() {
        return nPort;
    }

    public String GetViewPortNumber() {
        return new StringBuilder("PortNumber:    ").append(nPort).toString();
    }
    public int GetWriteMinInterval()
    {
        return nWriteMinInterval;
    }
    public void SetWriteMinInterval(int _interval)
    {
        nWriteMinInterval = _interval;
    }
    public String GetViewWriteMinInterval() {
        return new StringBuilder("WriteMinInterval:    ").append(nWriteMinInterval).append(" ms").toString();
    }
    public String GetViewSlaveID() {
        return new StringBuilder("SlaveID:    ").append(nSlaveID).toString();
    }


    // 获取关键信息
    @Override
    public String GetViewKeyString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(strId).append(":").append(nPort).append("[").append(nSlaveID).append("]").toString();
    }

    // 获取详细信息
    @Override
    public String GetViewDetailString() {
        StringBuilder builder = new StringBuilder();
        builder.append("通讯协议:   ModbusTCP/IPEthernet");//.append(System.getProperty("line.separator"));
        //builder.append(strId).append(System.getProperty("line.separator"));
        //builder.append(strDeviceDescription);//.append(System.getProperty("line.separator"));
//        builder.append("SwappedFP:    ").append(bSwappedFP).append(System.getProperty("line.separator"));
//        builder.append("SwappedDbl:   ").append(bSwappedDbl).append(System.getProperty("line.separator"));
//        builder.append("FirstWORDLowIn32BitData:    ").append(bFirstWORDLowIn32BitData).append(System.getProperty("line.separator"));
//        builder.append("FirstDWORDLowIn64BitData:   ").append(bFirstDWORDLowIn64BitData);

        return builder.toString();
    }
}
