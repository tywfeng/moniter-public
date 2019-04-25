package moniter.tyw.com.moniterlibrary.Siemens.S7;

import moniter.tyw.com.moniterlibrary.Siemens.DEFINESiemens;
import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.DataType;
import moniter.tyw.com.moniterlibrary.common.Device;

public class DeviceSiemensS7TCPIPEthernet extends Device {

    private DEFINESiemens.SiemensS7DeviceModel deviceModel;

    // data encoding
    // 在32位数据中第一个WORD为低位
    public boolean bFirstWORDLowIn32BitData = true;
    // 64位数据中第一个DWORD为低位 default TRUE 包括:DOUBLE
    public boolean bFirstDWORDLowIn64BitData = true;
    public boolean bSwappedFP; // 浮点数高低位反序 default TRUE

    // trming 时间限制
    private int nConnectTimeout; // seconds
    private int nRequestTimeoutMS; // millseconds
    private int nFailAfterSuccessiveTimeouts; // 连续N次超时后失败
    private int nReconnectWhenAfterSuccessiveFail; // 连续N次失败后重连
    private int nInterRequestDelayMS; // 网络请求最小延迟：毫秒

    // S7 Comm. Parameters
    private int wLocalTSAP; // S7-200
    private int wRemoteTSAP; // S7-200

    DEFINESiemens.SiemensS7LinkType linkType; // S7-300/400/1200/1500  enum enSiemensS7LinkType

    // CPU Settings
    private int wRack; // S7-300/400/1200/1500  取值范围[0,7]
    private int wSlot; // S7-300/400/1200/1500 取值范围[1,31]

    // byte order
    DEFINESiemens.SiemensS7ByteOrder byteOrder; // enum enSiemensS7ByteOrder

    // Communication Parameters
    private int wPortNumber; // default 102 (NetLink default 1099)
    private int wMPI_ID; // for NetLink only (S7-300-NetLink / S7-400-NetLink)

    // 基本信息
    private DEFINE.ScanMode scanMode = DEFINE.ScanMode.RespectClientSpecifiedScanRate;
    private int nScanRateMS = 1000;                                  // 扫描频率(毫秒)2种Request模式下生效

    // 构造函数 used by JNI call , 修改请注意C端代码
    protected DeviceSiemensS7TCPIPEthernet(long _lPtr, int _index, int _state, String _name, String _description, String _id,
                                           int _deviceModel, int _port, int _mpi, int _localTSAP, int _remoteTSAP, int _rack, int _slot, int _linkType,
                                           int _byteOrder, boolean _firstWORDLowIn32BitData, boolean _firstDWORDLowIn64BitData,
                                           boolean _swappedFP, int _connectTimeout, int _requestTimeoutMS,
                                           int _failAfterSuccessiveTimeouts, int _reconnectWhenAfterSuccessiveFail, int _interRequestDelayMS,
                                           int _scanModeValue, int _scanRateMS) {
        super(_lPtr, _index, _state, _name, _description, _id);
        //strIPAddress = _id;
        wLocalTSAP = _localTSAP;
        wRemoteTSAP = _remoteTSAP;
        linkType = DEFINESiemens.SiemensS7LinkType.GetLinkTypeWithValue(_linkType);
        wRack = _rack;
        wSlot = _slot;
        byteOrder = DEFINESiemens.SiemensS7ByteOrder.GetByteOrderWithValue(_byteOrder);
        wPortNumber = _port;
        wMPI_ID = _mpi;
        deviceModel = DEFINESiemens.SiemensS7DeviceModel.GetModelWithValue(_deviceModel);

        bFirstWORDLowIn32BitData = _firstWORDLowIn32BitData;
        bFirstDWORDLowIn64BitData = _firstDWORDLowIn64BitData;
        bSwappedFP = _swappedFP;

        nConnectTimeout = _connectTimeout;
        nRequestTimeoutMS = _requestTimeoutMS;
        nFailAfterSuccessiveTimeouts = _failAfterSuccessiveTimeouts;
        nReconnectWhenAfterSuccessiveFail = _reconnectWhenAfterSuccessiveFail;
        nInterRequestDelayMS = _interRequestDelayMS;

        scanMode = DEFINE.ScanMode.GetScanModeWithValue(_scanModeValue);
        nScanRateMS = _scanRateMS;
    }

    // 构造函数
    protected DeviceSiemensS7TCPIPEthernet(Channel _channel) {
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

    }

    @Override
    public int CalculateAllocateByteBufferSize() {
        return 0;
    }

    // 获取关键信息
    @Override
    public String GetViewKeyString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(strId).append(":").append(wPortNumber).append("[").
                append(deviceModel.GetName()).append("][").
                append(linkType.GetName()).append("]").toString();
    }

    // 获取详细信息
    @Override
    public String GetViewDetailString() {
        StringBuilder builder = new StringBuilder();
        builder.append("通讯协议:   SiemensS7 TCPIPEthernet");//.append(System.getProperty("line.separator"));
        //builder.append(strId).append(System.getProperty("line.separator"));
        //builder.append(strDeviceDescription);//.append(System.getProperty("line.separator"));
//        builder.append("SwappedFP:    ").append(bSwappedFP).append(System.getProperty("line.separator"));
//        builder.append("SwappedDbl:   ").append(bSwappedDbl).append(System.getProperty("line.separator"));
//        builder.append("FirstWORDLowIn32BitData:    ").append(bFirstWORDLowIn32BitData).append(System.getProperty("line.separator"));
//        builder.append("FirstDWORDLowIn64BitData:   ").append(bFirstDWORDLowIn64BitData);

        return builder.toString();
    }
    public void SetPortNumber(int _port) {
        wPortNumber = _port;
    }

    public int GetPortNumber() {
        return wPortNumber;
    }

    public String GetViewPortNumber() {
        return new StringBuilder("PortNumber:    ").append(wPortNumber).toString();
    }
}
