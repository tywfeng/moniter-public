package moniter.tyw.com.moniterlibrary.common;


//import android.support.annotation.NonNull;

import org.json.JSONObject;


public class Tag implements ConfigInterface, Comparable {
    // 小数点保留位数
    public static int MaximumFractionDigits = 2;

    // Identification
    public int nIndex = 0;                        // 索引
    public String strTagName, strTagDescription; // 点名、点描述
    public String strAddress;                   // 数据地址
    public long lTagPtr;                         // 点 JNI调用地址

    public int nRequestGroup = 0;                   // 数据请求所在组(同组一起请求)

    public DataType dataType= DataType.UNDEFINED;                    // 数据类型
    public String strCharsetName;                // 字符集
    public int nStringBytesize;                // 串字节数
    public int nArrayRows = 1, nArrayCols = 1; // 数组尺寸
    public boolean bSwappedByteIn16BitData = false; // 16字节数据交换byte


    // data properties 数据属性
    public DEFINE.DataAccess dataAccess=DEFINE.DataAccess.ReadWrite;         // 读写权限
    private TagData dataReadValue;                // 读取数据
//    public TagData dataWriteValue;               // 写入数据
//    public TagData dataScalingValue;             // 量程变换数值[*使用DOUBLE类型]

    // scaling 量程变换
    public DEFINE.ScalingType scalingType=DEFINE.ScalingType.None;       // 变换类型
    public double dbRawValueHigh, dbRawValueLow;  // 原值范围
    public DataType scaledValueType= DataType.UNDEFINED;            // 结果类型
    public double dbScaledValueHigh, dbScaledValueLow;// 结果范围
    public String strScaledValueUnits;           // 结果单位
    public boolean bClampLow, bClampHigh;      // 局限控制

    // 历史数据

    // 告警信息
    protected  TagAlarm mTagAlarm;

    // 图表属性
    protected TagChart tagChart;

    // 运行变量
    public Device deviceParent;                                // 所属Device

    // 构造函数
    protected Tag(Device _device) {
        deviceParent = _device;
        //SetDefaultValue();
    }

    // 构造函数used by JNI call , 修改请注意C端代码
    protected Tag(long _lPtr, int _index, int _dtValue, boolean _swapbyte, int _requestGroup, int _stringSize, int _rows, int _cols, int _rawsize, int _access,
                  int _scalingTypeValue, double _rawValHigh, double _rawValLow, int _scaledTypeValue,
                  double _scaledValHigh, double _scaledValLow, boolean _clampLow, boolean _clampHigh,
                  String _name, String _description, String _address, String _scaledValUnits, String _alarmJson, String _chartJson) {
        Init(_lPtr, _index, _dtValue, _swapbyte, _requestGroup, _stringSize, _rows, _cols, _rawsize, _access,
                _scalingTypeValue, _rawValHigh, _rawValLow, _scaledTypeValue,
                _scaledValHigh, _scaledValLow, _clampLow, _clampHigh,
                _name, _description, _address, _scaledValUnits, _alarmJson, _chartJson);

    }

    public TagChart GetChart() {
        return tagChart;
    }

    // init
    protected void Init(long _lPtr, int _index, int _dtValue, boolean _swapbyte, int _requestGroup, int _stringSize, int _rows, int _cols, int _rawsize, int _access,
                        int _scalingTypeValue, double _rawValHigh, double _rawValLow, int _scaledTypeValue,
                        double _scaledValHigh, double _scaledValLow, boolean _clampLow, boolean _clampHigh,
                        String _name, String _description, String _address, String _scaledValUnits, String _alarmJson, String _chartJson) {
        lTagPtr = _lPtr;
        nIndex = _index;
        strTagName = _name;
        strTagDescription = _description;
        strAddress = _address;
        nRequestGroup = _requestGroup;
        //C端过滤Array
        dataType = DataType.GetDataTypeWithValue(_dtValue & ~0x1000);
        bSwappedByteIn16BitData = _swapbyte;
        nStringBytesize = _stringSize;
        nArrayRows = _rows;
        nArrayCols = _cols;
        dataAccess = DEFINE.DataAccess.GetDataAccessWithValue(_access);
        scalingType = DEFINE.ScalingType.GetTypeWithValue(_scalingTypeValue);
        dbRawValueHigh = _rawValHigh;
        dbRawValueLow = _rawValLow;
        scaledValueType = DataType.GetDataTypeWithValue(_scaledTypeValue & ~0x1000);
        dbScaledValueHigh = _scaledValHigh;
        dbScaledValueLow = _scaledValLow;
        strScaledValueUnits = _scaledValUnits;
        bClampLow = _clampLow;
        bClampHigh = _clampHigh;
        int nTurnSize = _stringSize;
        if (scalingType.IsType(DEFINE.ScalingType.None)) {
            if (!dataType.IsType(DataType.STRING)) nTurnSize = dataType.Sizeof();
            dataReadValue = new TagData(dataType, nTurnSize, nArrayRows, nArrayCols);
        } else {
            if (!scaledValueType.IsType(DataType.STRING))
                nTurnSize = scaledValueType.Sizeof();
            dataReadValue = new TagData(scaledValueType, nTurnSize, nArrayRows, nArrayCols);
        }
        // 告警信息
        mTagAlarm = new TagAlarm(this);
        mTagAlarm.SetAlarmIntervalList(_alarmJson);
        // 图表属性
        tagChart = new TagChart(this);
        tagChart.SetValueFromJSON(_chartJson);
    }

    public TagAlarm.TagAlarmInfo GetAlarmWithValue(double _value) {
        if (mTagAlarm == null) return null;
        return mTagAlarm.GetAlarmInfoWithValue(_value);
    }

    public Device GetDevice() {
        return deviceParent;
    }

    public void SetDevice(Device device) {
        deviceParent = device;
    }

    public int GetViewAlarmColor(int defaultColor) {
        // 设备未运行时不显示数值
        if (deviceParent != null && !deviceParent.getDeviceState().IsState(DEFINE.DeviceState.Read))
            return defaultColor;
        if (mTagAlarm == null) return defaultColor;
        TagAlarm.TagAlarmInfo alarm = GetAlarmWithValue(GetReadData().GetData().toDouble());
        if (alarm == null) return defaultColor;
        return alarm.getColor();
    }

    // 获取关键信息
    public String GetViewKeyString() {
        return strTagName;
    }

    // 获取值信息
    public String GetViewValueString() {

        return "";
    }

    public boolean IsCanWrite() {
        // 设备未运行时不可以更改数值
        if (deviceParent != null && !deviceParent.getDeviceState().IsState(DEFINE.DeviceState.Read))
            return false;
        // 无写权限不可以更改
        return dataAccess.CanWrite();

    }
    // 获取详细信息
    public String GetViewDetailString() {

        return "";
    }

    public TagData GetReadData() {
        return dataReadValue;
    }

    public void SetNewReadValue(TagData _data) {
        if (_data == null) return;
        // 更新新值
        dataReadValue.SetData(_data.GetErrorCode(), _data.GetQualityCode(), _data.GetTimestamp(), _data.GetData());
//        // 更新至图表数据项
//        tagChart.SetChartSrcDataUpdate(_data);
    }

    public long GetPtr() {
        return lTagPtr;
    }
//    // 量程变换
//    protected boolean ScalingReadValue() {
//        // BadNotConnected 判断
//        if (!dataReadValue.GetQualityCode().IsQualityCode(DEFINE.DataQualityCode.GoodLocalOverride)
//                && !dataReadValue.GetQualityCode().IsQualityCode(DEFINE.DataQualityCode.GoodNonSpecific)) {
//            dataScalingValue.SetQualityCode(dataReadValue.GetQualityCode());
//            return false;
//        }
//        // BOOL类型不变换
//        if (scalingType == DEFINE.ScalingType.None
//                || dataReadValue.GetData().GetType().IsTypeIgnoreArray(DataType.BOOL)) {
//            dataScalingValue.SetQualityCode(dataReadValue.GetQualityCode());
//            return dataScalingValue.GetData().SetVariant(dataReadValue.GetData());
//        }
//        // 类型过滤STRING and DATE
//        if (dataReadValue.GetData().GetType().IsTypeIgnoreArray(DataType.STRING)
//                || dataReadValue.GetData().GetType().IsTypeIgnoreArray(DataType.DATE)) {
//            dataScalingValue.SetQualityCode(DEFINE.DataQualityCode.BadConfigError);
//            return false;
//        }
//
//        // 质量信息
//        DEFINE.DataQualityCode quality = DEFINE.DataQualityCode.GoodNonSpecific;
//        // 剩余统一处理
//        for (int i = 0; i < dataReadValue.GetData().GetType().GetArrayLength(); ++i) {
//            quality = DEFINE.DataQualityCode.GoodNonSpecific;
//            // 提取数值
//            double dbRawValue = dataReadValue.GetData().toDouble(i);
//            double dbScaledValue = dbRawValue;
//
//            if (dbRawValueHigh == dbRawValueLow) {
//                DebugLog.w("Tag Scaling Option Exception: rawHigh can not equal rawLow");
//                dbScaledValue = dbScaledValueLow;
//                quality = DEFINE.DataQualityCode.LimitfieldNot;
//            } else if (dbScaledValueHigh == dbScaledValueLow) {
//                dbScaledValue = dbScaledValueLow;
//                quality = DEFINE.DataQualityCode.LimitfieldConstant;
//            } else if (scalingType == DEFINE.ScalingType.Linear) {
//                // linear
//                dbScaledValue = (((dbScaledValueHigh - dbScaledValueLow) / (dbRawValueHigh - dbRawValueLow)) * (dbRawValue - dbRawValueLow)) + dbScaledValueLow;
//            } else if (scalingType == DEFINE.ScalingType.SquareRoot) {
//                //squareRoot
//                dbScaledValue = Math.sqrt(((dbRawValue - dbRawValueLow) / (dbRawValueHigh - dbRawValueLow)) * (dbScaledValueHigh - dbScaledValueLow)) + dbScaledValueLow;
//            }
//            // 高质量数据进行Clamp
//            if (quality.IsQualityCode(DEFINE.DataQualityCode.GoodNonSpecific)) {
//                // clamp
//                if (bClampLow && dbScaledValue < dbScaledValueLow) {
//                    dbScaledValue = dbScaledValueLow;
//                    quality = DEFINE.DataQualityCode.LimitfieldLow;
//                } else if (bClampHigh && dbScaledValue > dbScaledValueHigh) {
//                    dbScaledValue = dbScaledValueHigh;
//                    quality = DEFINE.DataQualityCode.LimitfieldHigh;
//                }
//            }
//            // 设置时间戳
//            dataScalingValue.SetTimestampMS(TimeUtils.GetCurrentTimestamp());
//            // 设置质量戳
//            dataScalingValue.SetQualityCode(quality);
//            // 设置数值
//            dataScalingValue.GetData().SetData(i, dbScaledValue);
//        }
//
//        return true;
//    }

    /////////////////////////-getter-///////////////////////////////
    // 获取量程变换单位
    public String GetScaledUnits() {
        return strScaledValueUnits;
    }

    public String GetName() {
        return strTagName;
    }

    public String GetDescription() {
        return strTagDescription;
    }

    /////////////////////////////////////////////////////////////////////
    // 配置相关


    @Override
    public int compareTo(Object o) {
        if (o instanceof Tag) {
            Tag s = (Tag) o;
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
        if (tagChart != null) tagChart.OnConfigLoadCompleted();
    }

}
