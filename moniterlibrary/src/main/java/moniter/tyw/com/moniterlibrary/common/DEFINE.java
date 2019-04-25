package moniter.tyw.com.moniterlibrary.common;

public class DEFINE {
    /**
     * 通道类型
     */
    public enum ChannelType {

        Unknown(0x00, "Unknown", "unknown"),
        ModbusRTU(0x01, "ModbusRTU", "只支持RTU->TCP数据转发式"),
        ModbusTCPEthernet(0x02, "ModbusTCPEthernet", "标准Modbus TCPIP"),
        SiemensS7TCPIPEthernet(0x03, "SiemensS7TCPIPEthernet", "SiemensS7 TCPIPEthernet");

        ChannelType(int _value, String _name, String _description) {
            channelValue = _value;
            channelName = _name;
            channelDescription = _description;
        }

        public static ChannelType GetTypeWithValue(int _value) {
            for (ChannelType ct : ChannelType.values()) if (ct.IsType(_value)) return ct;
            return Unknown;
        }

        public String GetName() {
            return channelName;
        }

        public String GetDescription() {
            return channelDescription;
        }

        public boolean IsType(ChannelType ct) {
            return (channelValue == ct.channelValue);
        }

        public boolean IsType(int _value) {
            return (channelValue == _value);
        }

        // 权限值
        private int channelValue;
        private String channelName, channelDescription;
    }

    /**
     * 数据权限
     */
    public enum DataAccess {
        ReadOnly(0x01, "ReadOnly", "只读"),
        ReadWrite(0x02, "ReadWrite", "读写"),
        WriteOnly(0x03, "WriteOnly", "只写");

        DataAccess(int _value, String _name, String _description) {
            accessValue = _value;
            accessName = _name;
            accessDescription = _description;
        }

        public static DataAccess GetDataAccessWithValue(int _value) {
            for (DataAccess ct : DataAccess.values()) if (ct.IsAccess(_value)) return ct;
            return ReadOnly;
        }

        public String GetName() {
            return accessName;
        }

        public String GetDescription() {
            return accessDescription;
        }

        public boolean CanRead() {
            if (accessValue == ReadOnly.accessValue || accessValue == ReadWrite.accessValue)
                return true;
            return false;
        }

        public boolean CanWrite() {
            if (accessValue == WriteOnly.accessValue || accessValue == ReadWrite.accessValue)
                return true;
            return false;
        }

        public static DataAccess GetAccess(int _value) {
            for (DataAccess ct : DataAccess.values()) if (ct.IsAccess(_value)) return ct;
            return ReadOnly;
        }

        public boolean IsAccess(DataAccess _da) {
            return (accessValue == _da.accessValue);
        }

        public boolean IsAccess(int _value) {
            return (accessValue == _value);
        }

        // 权限值
        private int accessValue;
        private String accessName, accessDescription;
    }

    /**
     * 采集扫描方式
     */
    public enum ScanMode {
        RespectClientSpecifiedScanRate(0x01, "RespectClientSpecifiedScanRate", " 遵循客户端指定的扫描速率(default)"),
        RequestDataNOFasterThan(0x02, "RequestDataNOFasterThan", " 不超过扫描速率请求数"),
        RequestAllDataAt(0x03, "RequestAllDataAt", "以扫描速率请求所有数据"),
        DemandPollOnly(0x04, "DemandPollOnly", "不扫描，需求轮询");

        ScanMode(int _value, String _name, String _description) {
            scanModeValue = _value;
            scanModeName = _name;
            scanModeDescription = _description;
        }

        public static ScanMode GetScanModeWithValue(int _value) {
            for (ScanMode ct : ScanMode.values()) if (ct.IsMode(_value)) return ct;
            return RespectClientSpecifiedScanRate;
        }

        public String GetName() {
            return scanModeName;
        }

        public String GetDescription() {
            return scanModeDescription;
        }

        public static ScanMode GetMode(int _value) {
            for (ScanMode ct : ScanMode.values()) if (ct.IsMode(_value)) return ct;
            return RespectClientSpecifiedScanRate;
        }

        public boolean IsMode(ScanMode _sm) {
            return (scanModeValue == _sm.scanModeValue);
        }

        public boolean IsMode(int _value) {
            return (scanModeValue == _value);
        }

        // 权限值
        private int scanModeValue;
        private String scanModeName, scanModeDescription;
    }

    /**
     * 网络协议
     */
    public enum IPProtocol {
        TCPIP((byte) 0x01, "TCPIP", "TCP/IP协议"),
        UDP((byte) 0x02, "UDP", "UDP协议");

        IPProtocol(byte _value, String _name, String _description) {
            protocolValue = _value;
            protocolName = _name;
            protocolDescription = _description;
        }

        public String GetName() {
            return protocolName;
        }

        public String GetDescription() {
            return protocolDescription;
        }

        public static IPProtocol GetIPProtocolWithValue(byte _value) {
            for (IPProtocol ct : IPProtocol.values()) if (ct.IsIPProtocol(_value)) return ct;
            return TCPIP;
        }

        public boolean IsIPProtocol(IPProtocol _protocol) {
            return (protocolValue == _protocol.protocolValue);
        }

        public boolean IsIPProtocol(byte _value) {
            return (protocolValue == _value);
        }

        // 协议值
        private byte protocolValue;
        private String protocolName, protocolDescription;
    }

    /**
     * 错误代码
     */
    public enum ErrorCode {
        //
        None(0x00, "no error", "None"),
        // Moniter
        MoniterConnectFailure(0x00000001, "MoniterConnectFailure", "连接失败"),
        MoniterReconnectFailure(0x00000002, "MoniterReconnectFailure", "重连失败"),
        MoniterReadFailure(0x00000003, "MoniterReadFailure", "读失败"),
        MoniterWriteFailure(0x00000004, "MoniterWriteFailure", "写失败"),
        MoniterFailAfterSuccessiveTimeouts(0x00000005, "MoniterReadFailure", "连续超时后失败");


        ErrorCode(int _value, String _name, String _description) {
            errorCodeValue = _value;
            errorCodeName = _name;
            errorCodeDescription = _description;
        }

        public static ErrorCode GetErrorCodeWithValue(int value) {
            for (ErrorCode i : ErrorCode.values()) {
                if (i.IsErrorCode(value)) {
                    return i;
                }
            }
            return None;
        }

        public String GetErrorCodeHexString() {
            return new StringBuilder("0x").append(Integer.toHexString(errorCodeValue)).toString();
        }

        public String GetName() {
            return errorCodeName;
        }

        public String GetDescription() {
            return errorCodeDescription;
        }

        public boolean IsErrorCode(ErrorCode _code) {
            return (errorCodeValue == _code.errorCodeValue);
        }

        public boolean IsErrorCode(int _value) {
            return (errorCodeValue == _value);
        }

        // 错误值
        private int errorCodeValue;
        private String errorCodeName, errorCodeDescription;
    }

    /**
     * 数据质量代码
     */
    public enum DataQualityCode {
        // NoQualityNoValue - default
        NoQualityNoValue(0xff, 0xff8d8d8d, "no value", "no value"),
        // BAD
        BadNonSpecific(0x00, 0xffa23737, "BadNonSpecific", "BadNonSpecific"),
        BadConfigError(0x04, 0xffa23737, "BadConfigError", "BadConfigError"),
        BadNotConnected(0x08, 0xffa23737, "BadNotConnected", "BadNotConnected"),
        BadDeviceFailure(0x0C, 0xffa23737, "BadDeviceFailure", "BadDeviceFailure"),
        BadSensorFailure(0x10, 0xffa23737, "BadSensorFailure", "BadSensorFailure"),
        BadLastKnownValue(0x14, 0xffa23737, "BadLastKnownValue", "BadLastKnownValue"),
        BadCommFailure(0x18, 0xffa23737, "BadCommFailure", "BadCommFailure"),
        BadOutOfService(0x1C, 0xffa23737, "BadOutOfService", "BadOutOfService"),
        WaitingForInitialData(0x20, 0xff8d8d8d, "WaitingForInitialData", "WaitingForInitialData"),

        //UnCertain
        UnCertainNonSpecific(0x40, 0xff8d8d8d, "UnCertainNonSpecific", "UnCertainNonSpecific"),
        UnCertainLastUsableValue(0x44, 0xff8d8d8d, "UnCertainLastUsableValue", "UnCertainLastUsableValue"),
        UnCertainSensorNotAccurate(0x50, 0xff8d8d8d, "UnCertainSensorNotAccurate", "UnCertainSensorNotAccurate"),
        UnCertainEuUnitsExceeded(0x54, 0xff8d8d8d, "UnCertainEuUnitsExceeded", "UnCertainEuUnitsExceeded"),
        UnCertainSubNormal(0x58, 0xff8d8d8d, "UnCertainSubNormal", "UnCertainSubNormal"),
        //Good
        GoodNonSpecific(0xC0, 0xff8d8d8d, "GoodNonSpecific", "GoodNonSpecific"),
        GoodLocalOverride(0xD8, 0xff8d8d8d, "GoodLocalOverride", "GoodLocalOverride"),
        // Limitfield
        LimitfieldNot(0xE0, 0xff95975f, "LimitfieldNot", "量程变换:源数据高低限相同时"),
        LimitfieldLow(0xE1, 0xff95975f, "LimitfieldLow", "量程变换:变换后触发Clamp低限"),
        LimitfieldHigh(0xE2, 0xff95975f, "LimitfieldHigh", "量程变换:变换后触发Clamp高限"),
        LimitfieldConstant(0xE3, 0xff95975f, "LimitfieldConstant", "量程变换:目标高低限相同时");

        DataQualityCode(int _value, int _color, String _name, String _description) {
            qualityValue = _value;
            colorValue = _color;
            qualityName = _name;
            qualityDescription = _description;
        }

        public static DataQualityCode GetQualityCodeWithValue(int value) {
            for (DataQualityCode i : DataQualityCode.values()) {
                if (i.IsQualityCode(value)) {
                    return i;
                }
            }
            return NoQualityNoValue;
        }

        // 可以正常读值的质量码
        public static boolean IsGoodCode(DataQualityCode _quality) {
            return (_quality == GoodNonSpecific || _quality == GoodLocalOverride);

        }

        public static boolean IsWarningCode(DataQualityCode _quality) {
            return (_quality == LimitfieldConstant || _quality == LimitfieldHigh
                    || _quality == LimitfieldLow || _quality == LimitfieldNot);
        }

        public String GetName() {
            return qualityName;
        }

        public String GetDescription() {
            return qualityDescription;
        }

        public boolean IsQualityCode(DataQualityCode _code) {
            return (qualityValue == _code.qualityValue);
        }

        public boolean IsQualityCode(int _value) {
            return (qualityValue == _value);
        }

        public int GetColor() {
            return colorValue;
        }

        // 质量值
        private int qualityValue;
        private int colorValue;
        private String qualityName, qualityDescription;
    }

    /**
     * 量程变换类型
     */
    public enum ScalingType {
        None(0x01, "None", "无变换"),
        Linear(0x02, "Linear", "线性计算"),
        SquareRoot(0x03, "SquareRoot", "开方计算");

        ScalingType(int _value, String _name, String _description) {
            scalingTypeValue = _value;
            scalingTypeName = _name;
            scalingTypeDescription = _description;
        }

        public String GetName() {
            return scalingTypeName;
        }

        public String GetDescription() {
            return scalingTypeDescription;
        }

        public static ScalingType GetTypeWithValue(int _value) {
            for (ScalingType ct : ScalingType.values()) if (ct.IsType(_value)) return ct;
            return None;
        }

        public boolean IsType(ScalingType _st) {
            return (scalingTypeValue == _st.scalingTypeValue);
        }

        public boolean IsType(int _value) {
            return (scalingTypeValue == _value);
        }

        // 类型值
        private int scalingTypeValue;
        private String scalingTypeName, scalingTypeDescription;
    }

    /**
     * 设备连接状态类型
     */
    public enum DeviceState {
        Unknown(0xff, 0xff8d8d8d, "Unknown", "未知"),
        Undefined(0x00, 0xff8d8d8d, "未启用", "未连接"),
        Connecting(0x01, 0xff00fff9, "连接中", "连接中..."),
        WaitingToBeConnected(0x02, 0xff6bd0e4, "等待被连接", "等待被连接"),
        BeConnectedSuccess(0x03, 0xff3bee7f, "被连接成功", "被连接成功"),
        BeConnectedFailed(0x04, 0xffcf1636, "被连接失败", "被连接失败"),
        ConnectSuccess(0x05, 0xff3bee7f, "连接成功", "连接成功"),
        ConnectFailed(0x06, 0xffcf1636, "连接失败", "连接失败"),
        ReadyReconnect(0x07, 0xffbfa144, "准备重连", "重连中..."),
        Read(0x08, 0xff3bee7f, "读取状态", "正在读取..."),
        Disconnect(0x09, 0xffbf2923, "断开连接", "已断开"),
        ListenerFailed(0x10, 0xffbf2923, "端口监听失败", "端口监听失败");

        DeviceState(int _value, int _colorVal, String _name, String _description) {
            stateValue = _value;
            colorValue = _colorVal;
            stateName = _name;
            stateDescription = _description;
        }

        public String GetName() {
            return stateName;
        }

        public String GetDescription() {
            return stateDescription;
        }

        public static DeviceState GetStateWithValue(int _value) {
            for (DeviceState ct : DeviceState.values())
                if (ct.IsState(_value)) return ct;
            return Undefined;
        }

        public boolean IsState(DeviceState _ds) {
            return (stateValue == _ds.stateValue);
        }

        public boolean IsState(int _value) {
            return (stateValue == _value);
        }

        public int GetColor() {
            //DebugLog.d("GetColor:"+colorValue);
            return colorValue;
        }

        // 类型值
        private int stateValue, colorValue;
        private String stateName, stateDescription;
    }

    /**
     * 图表类型
     */
    public enum ChartType {
        None(0x00, "None", "无图表"),
        LineChart(0x01, "LineChart", "线性图表"), // Style:BasicSingleTag，
        CombinedC(0x02, "CombinedC", "组合图表"),
        BarChart(0x03, "BarChart", "柱形图表"),//Style:BasicSingleTag，
        HorizontalBarChart(0x04, "HorizontalBarChart", "水平条形图表"),
        PieChart(0x05, "PieChart", "饼形图表"),
        ScatterChart(0x06, "ScatterChart", "分散式图表"),
        CandleStickChart(0x07, "CandleStickChart", "烛台图表"),
        BubbleChart(0x08, "BubbleChart", "气泡图表"),
        RadarChart(0x09, "RadarChart", "雷达图表");

        ChartType(int _value, String _name, String _description) {
            chartTypeValue = _value;
            chartTypeName = _name;
            chartTypeDescription = _description;
        }

        public String GetName() {
            return chartTypeName;
        }

        public String GetDescription() {
            return chartTypeDescription;
        }

        public static ChartType GetTypeWithValue(int _value) {
            for (ChartType ct : ChartType.values()) if (ct.IsType(_value)) return ct;
            return None;
        }

        public int GetValue() {
            return chartTypeValue;
        }

        public boolean IsType(ChartType _ct) {
            return (chartTypeValue == _ct.chartTypeValue);
        }

        public boolean IsType(int _value) {
            return (chartTypeValue == _value);
        }

        // 类型值
        private int chartTypeValue;
        private String chartTypeName, chartTypeDescription;
    }

    /**
     * 图表样式
     */
    public enum ChartStyle {
        Unknown(0x00, "Unknown", "未知样式"),
        BasicSingleTag(0x01, "常规单一点数据集", "常规单一点数据集"),
        BasicMultipleTag(0x02, "常规多点数据集", "常规多点数据集"),
        StackMultipleTag(0x03, "栈型多点数据集", "栈型多点数据集");

        ChartStyle(int _value, String _name, String _description) {
            styleValue = _value;
            styleName = _name;
            styleDescription = _description;
        }

        public String GetName() {
            return styleName;
        }

        public String GetDescription() {
            return styleDescription;
        }

        public static ChartStyle GetStyleWithValue(int _value) {
            for (ChartStyle s : ChartStyle.values()) if (s.IsStyle(_value)) return s;
            return Unknown;
        }

        public boolean IsStyle(ChartStyle _s) {
            return (styleValue == _s.styleValue);
        }

        public boolean IsStyle(int _value) {
            return (styleValue == _value);
        }

        public int GetValue() {
            return styleValue;
        }

        // 类型值
        private int styleValue;
        private String styleName, styleDescription;
    }


    /**
     * 数据源类型
     */
    public enum ChartDatasetType {
        Unknown(0x00, "Unknown", "未知样式"),
        RealtimeDataset(0x01, "实时数据集", "实时数据集"),
        //LastInterval(0x02, "最近一次区间数值", "最近一次区间数值"), // 设计为单点区间 数值累加?
        LastRealtimeData(0x03, "最后一次数据", "最后一次数据"),
        //SumData(0x04, "求和数据", "求和数据")
        ;

        ChartDatasetType(int _value, String _name, String _description) {
            datasetValue = _value;
            datasetName = _name;
            datasetDescription = _description;
        }

        public String GetName() {
            return datasetName;
        }

        public String GetDescription() {
            return datasetDescription;
        }

        public static ChartDatasetType GetTypeWithValue(int _value) {
            for (ChartDatasetType s : ChartDatasetType.values()) if (s.IsType(_value)) return s;
            return Unknown;
        }

        public boolean IsType(ChartDatasetType _s) {
            return (datasetValue == _s.datasetValue);
        }

        public boolean IsType(int _value) {
            return (datasetValue == _value);
        }

        public int GetValue() {
            return datasetValue;
        }

        // 类型值
        private int datasetValue;
        private String datasetName, datasetDescription;
    }

    /**
     * 区间类型
     */

    public enum IntervalType {
        leftClosedRightClosed(0x00, "L闭R闭", "[]"),
        leftClosedRightOpening(0x01, "L闭R开", "[)"),
        leftOpeningRightClosed(0x02, "L开R闭", "(]"),
        leftOpeningRightOpening(0x03, "L开R开", "()");

        IntervalType(int _value, String _name, String _description) {
            typeValue = _value;
            typeName = _name;
            typeDescription = _description;
        }

        public String GetName() {
            return typeName;
        }

        public String GetDescription() {
            return typeDescription;
        }

        public static IntervalType GetTypeWithValue(int _value) {
            for (IntervalType ct : IntervalType.values()) if (ct.IsType(_value)) return ct;
            return leftClosedRightClosed;
        }

        public boolean IsType(IntervalType _ct) {
            return (typeValue == _ct.typeValue);
        }

        public boolean IsType(int _value) {
            return (typeValue == _value);
        }

        // 类型值
        private int typeValue;
        private String typeName, typeDescription;
    }
    /**
     * 写点值类型分类
     */
    public enum WriteTagValueType {
        Unknown(0x00,"Unknown","Unknown"),
        OnlyScaledValue(0x01, "OnlyScaledValue使用量程变换后的数据类型写入", "OnlyScaledValue[if(ScalingType==None)return false]"),
        OnlySrcValue(0x02, "OnlySrcValue使用原数据类型写入", "Only use SrcValue"),
        AutoScaledOrSrcValue(0x03, "AutoScaledOrSrcValue优先量程变换后的数据类型写入", "if(ScalingType!=None)used ScaledValue, else used SrcValue"),
        OnlyRawHex(0x04, "OnlyRawHex十六进制字符串转为数据源", "used raw hex format:<12 34 56 78>  or <12345678>,长度会根据实际tag类型自动截断或补位") ;

        WriteTagValueType(int _value, String _name, String _description) {
            typeValue = _value;
            typeName = _name;
            typeDescription = _description;
        }

        public String GetName() {
            return typeName;
        }

        public String GetDescription() {
            return typeDescription;
        }

        public static WriteTagValueType GetTypeWithValue(int _value) {
            for (WriteTagValueType ct : WriteTagValueType.values()) if (ct.IsType(_value)) return ct;
            return Unknown;
        }

        public boolean IsType(WriteTagValueType _ct) {
            return (typeValue == _ct.typeValue);
        }

        public boolean IsType(int _value) {
            return (typeValue == _value);
        }
        public int GetValue(){return typeValue;}

        // 类型值
        private int typeValue;
        private String typeName, typeDescription;
    }
    /**
     * 基本框架分类
     */

    public enum BasicFrameType {
        None(0xff, "None", "无"),
        Common(0x00, "Common", "公用"),
        Channel(0x01, "Channel", "通道"),
        Device(0x02, "Device", "设备"),
        Tag(0x03, "Tag", "点");

        BasicFrameType(int _value, String _name, String _description) {
            typeValue = _value;
            typeName = _name;
            typeDescription = _description;
        }

        public String GetName() {
            return typeName;
        }

        public String GetDescription() {
            return typeDescription;
        }

        public static BasicFrameType GetTypeWithValue(int _value) {
            for (BasicFrameType ct : BasicFrameType.values()) if (ct.IsType(_value)) return ct;
            return None;
        }

        public boolean IsType(BasicFrameType _ct) {
            return (typeValue == _ct.typeValue);
        }

        public boolean IsType(int _value) {
            return (typeValue == _value);
        }

        // 类型值
        private int typeValue;
        private String typeName, typeDescription;
    }

    /**
     * 动态修改参数分类
     */
    public static String strSetParamRetSuccess = "success";
    public enum DynamicSetParamType {
        Unknown(0x00, BasicFrameType.None, "Unknown", "Unknown"),
        SwappedFP(0x01, BasicFrameType.Device, "SwappedFP", "SwappedFP"),
        SwappedDbl(0x02, BasicFrameType.Device, "SwappedDbl", "SwappedDbl"),
        FirstWORDLowIn32BitData(0x03, BasicFrameType.Device, "FirstWORDLowIn32BitData", "FirstWORDLowIn32BitData"),
        FirstDWORDLowIn64BitData(0x04, BasicFrameType.Device, "FirstDWORDLowIn64BitData", "FirstDWORDLowIn64BitData"),
        SwappedByteIn16BitData(0x05, BasicFrameType.Tag, "SwappedByteIn16BitData", "SwappedByteIn16BitData"),
        ResetTagAddress(0x06, BasicFrameType.Tag, "ResetTagAddress", "ResetTagAddress"),
        ResetDeviceID(0x07, BasicFrameType.Device, "ResetDeviceID", "ResetDeviceID"),
        ResetConnectIPAddress(0x08, BasicFrameType.Device, "ResetConnectIPAddress", "ResetConnectIPAddress"),
        ResetPortNumber(0x09, BasicFrameType.Device, "ResetPortNumber", "ResetPortNumber"),
        ResetSlaveID(0x10, BasicFrameType.Device, "ResetSlaveID", "ResetSlaveID"),
        ModbusWriteCoilsUsingCode15(0x11, BasicFrameType.Device, "UsingWriteCode15", "ModbusWriteCoilsUsingCode15"),
        ModbusWriteRegUsingCode16(0x12, BasicFrameType.Device, "UsingWriteCode16", "ModbusWriteRegUsingCode16"),
        WriteMinIntervalMS(0x13, BasicFrameType.Device, "WriteMinIntervalMS", "WriteMinIntervalMS"),
        ModbusWriteMatchAddrType(0x14, BasicFrameType.Device, "WriteMatchAddrType", "ModbusWriteMatchAddrType");

        DynamicSetParamType(int _value, BasicFrameType _type, String _name, String _description) {
            typeValue = _value;
            frameType = _type;
            typeName = _name;
            typeDescription = _description;
        }

        public String GetName() {
            return typeName;
        }

        public String GetDescription() {
            return typeDescription;
        }

        public BasicFrameType GetFrameType() {
            return frameType;
        }

        public static DynamicSetParamType GetTypeWithValue(int _value) {
            for (DynamicSetParamType ct : DynamicSetParamType.values())
                if (ct.IsType(_value)) return ct;
            return Unknown;
        }

        public boolean IsType(DynamicSetParamType _ct) {
            return (typeValue == _ct.typeValue);
        }

        public boolean IsType(int _value) {
            return (typeValue == _value);
        }

        public int GetValue() {
            return typeValue;
        }

        // 类型值
        private int typeValue;
        private BasicFrameType frameType;
        private String typeName, typeDescription;
    }
}
