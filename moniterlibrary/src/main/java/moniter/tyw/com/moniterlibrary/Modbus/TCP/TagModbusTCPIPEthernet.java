package moniter.tyw.com.moniterlibrary.Modbus.TCP;

import org.json.JSONObject;

import moniter.tyw.com.moniterlibrary.Modbus.DEFINEModbus;
import moniter.tyw.com.moniterlibrary.Modbus.RTU.DeviceModbusRTU;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.DataType;
import moniter.tyw.com.moniterlibrary.common.Device;
import moniter.tyw.com.moniterlibrary.common.Tag;
import moniter.tyw.com.moniterlibrary.common.TagAlarm;
import moniter.tyw.com.moniterlibrary.common.TagData;

public class TagModbusTCPIPEthernet extends Tag {
    public DEFINEModbus.ModbusAddressType modbusAddressType;
    public int nDataAddress;       // 地址
    public int nBOOLReadBit;      // boolean类型位索引


    // 构造函数used by JNI call , 修改请注意C端代码
    private TagModbusTCPIPEthernet(long _lPtr, int _index, int _dtValue, boolean _swapbyte, int _requestGroup, int _stringSize, int _rows, int _cols,
                                   int _rawsize, int _access,
                                   int _scalingTypeValue, double _rawValHigh, double _rawValLow, int _scaledTypeValue,
                                   double _scaledValHigh, double _scaledValLow, boolean _clampLow, boolean _clampHigh,
                                   String _name, String _description, String _address, String _scaledValUnits, String _alarmJson, String _chartJson,
                                   int _modbusAddressType, int _dataAddress, int _boolReadBit) {
        super(_lPtr, _index, _dtValue, _swapbyte, _requestGroup, _stringSize, _rows, _cols, _rawsize, _access,
                _scalingTypeValue, _rawValHigh, _rawValLow, _scaledTypeValue,
                _scaledValHigh, _scaledValLow, _clampLow, _clampHigh,
                _name, _description, _address, _scaledValUnits, _alarmJson, _chartJson);
        modbusAddressType = DEFINEModbus.ModbusAddressType.GetModbusAddressTypeWithValue(_modbusAddressType);
        nDataAddress = _dataAddress;
        nBOOLReadBit = _boolReadBit;
    }

    protected TagModbusTCPIPEthernet(Device _device) {
        super(_device);
    }

    /**
     * 获取Device （modbusTCP）
     *
     * @return
     */
    protected DeviceModbusTCPIPEthernet GetDeviceModbusTCP() {
        if (deviceParent instanceof DeviceModbusTCPIPEthernet) return ((DeviceModbusTCPIPEthernet) deviceParent);
        return null;
    }

    // 获取关键信息
    @Override
    public String GetViewKeyString() {
        return GetName();
    }

    // 获取值信息
    @Override
    public String GetViewValueString() {
        // 设备未运行时不显示数值
        if (deviceParent != null && !deviceParent.getDeviceState().IsState(DEFINE.DeviceState.Read))
            return "";
        // string builder
        StringBuilder builder = new StringBuilder();
        // good值
        if (DEFINE.DataQualityCode.IsGoodCode(GetReadData().GetQualityCode())) {
            builder.append(GetReadData().GetData().toString()).append(strScaledValueUnits);
            return builder.toString();
        } else if (DEFINE.DataQualityCode.IsWarningCode(GetReadData().GetQualityCode())) {
            // 非正常值
            builder.append('?').append(GetReadData().GetData().toString()).append(strScaledValueUnits);
            return builder.toString();
        }
        return "[bad]";
    }



    // 获取详细信息
    @Override
    public String GetViewDetailString() {
        StringBuilder builder = new StringBuilder();
        // 有有效数据
        if (DEFINE.DataQualityCode.IsGoodCode(GetReadData().GetQualityCode()) ||
                DEFINE.DataQualityCode.IsWarningCode(GetReadData().GetQualityCode())) {
            builder.append("Raw(Hex):").append(GetReadData().GetData().GetRawHexString()).append(System.getProperty("line.separator"));
        }
        if (deviceParent != null && deviceParent.getDeviceState().IsState(DEFINE.DeviceState.Read)
                && mTagAlarm != null) {
            TagAlarm.TagAlarmInfo alarm = GetAlarmWithValue(GetReadData().GetData().toDouble());
            if (alarm != null) {
                builder.append("alarm:  ").append(alarm.GetName()).append(System.getProperty("line.separator"));
            }
        }
        // 有变换信息 : [400001][WORD->WORD]
        if (!DEFINE.ScalingType.None.IsType(scalingType)) {
            builder.append("[").append(strAddress).append("][").append(dataType.GetName())
                    .append("->").append(scaledValueType.GetName()).append("]");

        } else {
            builder.append("[").append(strAddress).append("][").append(dataType.GetName()).append("]");
        }
        if (!strTagDescription.isEmpty())
            builder.append(System.getProperty("line.separator")).append(strTagDescription);
        builder.append(System.getProperty("line.separator")).append("Access:     ").append(dataAccess.GetName());
        if (!DEFINE.ScalingType.None.IsType(scalingType)) {
            builder.append(System.getProperty("line.separator")).append("量程变换:   ").append(scalingType.GetDescription());
            builder.append(System.getProperty("line.separator")).append("原始范围:   ").append(Math.min(dbRawValueLow, dbRawValueHigh)).append('-').append(Math.max(dbRawValueLow, dbRawValueHigh));
            builder.append(System.getProperty("line.separator")).append("目标范围:   ").append(bClampLow ? "(Clamp)" : "").append(Math.min(dbScaledValueLow, dbScaledValueHigh)).append("--").append(Math.max(dbScaledValueLow, dbScaledValueHigh)).append(bClampHigh ? "(Clamp)" : "");
        }
        if (bSwappedByteIn16BitData)
            builder.append(System.getProperty("line.separator")).append("SwappedByteIn16BitData:     ").append(bSwappedByteIn16BitData);

        return builder.toString();
    }

}
