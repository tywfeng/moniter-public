package moniter.tyw.com.moniterlibrary.Modbus.RTU;

import org.json.JSONObject;

import java.util.ArrayList;

import moniter.tyw.com.moniterlibrary.Modbus.DEFINEModbus;
import moniter.tyw.com.moniterlibrary.Utils.TimeUtils;
import moniter.tyw.com.moniterlibrary.common.TagAlarm;
import moniter.tyw.com.moniterlibrary.common.TagChart;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.DataType;
import moniter.tyw.com.moniterlibrary.common.Tag;
import moniter.tyw.com.moniterlibrary.common.TagData;

/**
 * 地址字符串十进制格式:
 * WORD类型: 410001[3] : 保持寄存器10001位置读取WORD数组3
 */
public class TagModbusRTU extends Tag {

    private DEFINEModbus.ModbusAddressType modbusAddressType;
    // 数据位置
    private int nDataAddress;
    // bool值的数据位
    private int nBOOLReadBit;

    // 构造函数used by JNI call , 修改请注意C端代码
    private TagModbusRTU(long _lPtr, int _index, int _dtValue, boolean _swapbyte,int _requestGroup,int _stringSize, int _rows, int _cols,
                         int _rawsize, int _access,
                         int _scalingTypeValue, double _rawValHigh, double _rawValLow, int _scaledTypeValue,
                         double _scaledValHigh, double _scaledValLow, boolean _clampLow, boolean _clampHigh,
                         String _name, String _description, String _address, String _scaledValUnits, String _alarmJson, String _chartJson,
                         int _modbusAddressType, int _dataAddress, int _boolReadBit) {
        super(_lPtr, _index, _dtValue,_swapbyte, _requestGroup,_stringSize, _rows, _cols,_rawsize, _access,
                _scalingTypeValue, _rawValHigh, _rawValLow, _scaledTypeValue,
                _scaledValHigh, _scaledValLow, _clampLow, _clampHigh,
                _name, _description, _address, _scaledValUnits, _alarmJson, _chartJson);
        modbusAddressType = DEFINEModbus.ModbusAddressType.GetModbusAddressTypeWithValue(_modbusAddressType);
        nDataAddress = _dataAddress;
        nBOOLReadBit = _boolReadBit;
    }

    protected TagModbusRTU(DeviceModbusRTU _device) {
        super(_device);
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
            return builder.append(GetReadData().GetData().toString()).append(strScaledValueUnits).toString();
        } else if (DEFINE.DataQualityCode.IsWarningCode(GetReadData().GetQualityCode())) {
            // 非正常值
            return builder.append('?').append(GetReadData().GetData().toString()).append(strScaledValueUnits).toString();
        }
        return "[bad]";

    }


    // 获取详细信息
    @Override
    public String GetViewDetailString() {
        StringBuilder builder = new StringBuilder();
        // 有有效数据
        if(DEFINE.DataQualityCode.IsGoodCode( GetReadData().GetQualityCode())||
                DEFINE.DataQualityCode.IsWarningCode( GetReadData().GetQualityCode()) )
        {
            builder.append("Raw(Hex):").append(GetReadData().GetData().GetRawHexString()).append(System.getProperty("line.separator"));

        }
        if (deviceParent != null && deviceParent.getDeviceState().IsState(DEFINE.DeviceState.Read)
                && mTagAlarm!=null)
        {
            TagAlarm.TagAlarmInfo alarm = GetAlarmWithValue(GetReadData().GetData().toDouble());
            if (alarm != null)
            {
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
            builder.append(System.getProperty("line.separator")).append("目标范围:   ").append(bClampLow?"(Clamp)":"").append(Math.min(dbScaledValueLow, dbScaledValueHigh)).append("--").append(Math.max(dbScaledValueLow, dbScaledValueHigh)).append(bClampHigh?"(Clamp)":"");
        }
        if(bSwappedByteIn16BitData)
            builder.append(System.getProperty("line.separator")).append("SwappedByteIn16BitData:     ").append(bSwappedByteIn16BitData);

        return builder.toString();
    }

    /**
     * 获取Device （modbusRTU）
     *
     * @return
     */
    protected DeviceModbusRTU GetDeviceModbusRTU() {
        if (deviceParent instanceof DeviceModbusRTU) return ((DeviceModbusRTU) deviceParent);
        return null;
    }

//    @Override
//    protected void FormatReadValue(byte[] _bytes,long lTimestampMS, DEFINE.DataQualityCode _quality, DEFINE.ErrorCode _error) {
//        DeviceModbusRTU device = GetDeviceModbusRTU();
//        if (device == null) return;
//
//        // 设置质量、时间戳、错误码
//        dataReadValue.SetTimestampMS(TimeUtils.GetCurrentTimestamp());
//        dataReadValue.SetQualityCode(_quality);
//        dataReadValue.SetErrorCode(_error);
//
//        // 字节序调整-64bit
//        if (device.IsFirstDWORDLowIn64BitData(dataReadValue.GetData().GetType())) {
//            byte tempCopy[] = new byte[4];
//            int nSwapCount = dataReadValue.GetData().GetType().Size() / 8;
//            for (int i = 0; i < nSwapCount; ++i) {
//                System.arraycopy(_bytes, i * 8, tempCopy, 0, 4);
//                System.arraycopy(_bytes, i * 8 + 4, _bytes, i * 8, 4);
//                System.arraycopy(tempCopy, 0, _bytes, i * 8 + 4, 4);
//            }
//        }
//        // 字节序调整-32bit
//        if (device.IsFirstWORDLowIn32BitData(dataReadValue.GetData().GetType())) {
//            byte tempCopy[] = new byte[2];
//            int nSwapCount = dataReadValue.GetData().GetType().Size() / 4;
//            for (int i = 0; i < nSwapCount; ++i) {
//                System.arraycopy(_bytes, i * 4, tempCopy, 0, 2);
//                System.arraycopy(_bytes, i * 4 + 2, _bytes, i * 4, 2);
//                System.arraycopy(tempCopy, 0, _bytes, i * 4 + 2, 2);
//            }
//        }
//
//        int nArrayLen = dataReadValue.GetData().GetType().GetArrayLength();
//        int nCellSize = dataReadValue.GetData().GetType().Sizeof();
//        for (int i = 0; i < nArrayLen; ++i) {
//            int nPos = i * nCellSize;
//            switch (dataReadValue.GetData().GetType()) {
//                case BOOL: {
//                    dataReadValue.GetData().SetData(i, (((_bytes[nPos + (nBOOLReadBit / 8)] >> (nBOOLReadBit % 8)) & 0x01) != 0));
//                }
//                break;
//                case BYTE: {
//                    dataReadValue.GetData().SetData(i, _bytes[nPos]);
//                }
//                break;
//                case BCD: {
//                    int bcd = ((_bytes[nPos] & 0xf0) >> 4) * 1000 + (_bytes[nPos] & 0x0f) * 100 + ((_bytes[nPos + 1] & 0xf0) >> 4) * 10 + (_bytes[nPos + 1] & 0x0f);
//                    if (bcd > 9999)
//                        bcd = 9999;
//                    dataReadValue.GetData().SetData(i, bcd);
//                }
//                break;
//                case LBCD: {
//                    int lbcd = ((_bytes[nPos] & 0xf0) >> 4) * 10000000 + (_bytes[nPos] & 0x0f) * 1000000 +
//                            ((_bytes[nPos + 1] & 0xf0) >> 4) * 100000 + (_bytes[nPos + 1] & 0x0f) * 10000 +
//                            ((_bytes[nPos + 2] & 0xf0) >> 4) * 1000 + (_bytes[nPos + 2] & 0x0f) * 100 +
//                            ((_bytes[nPos + 3] & 0xf0) >> 4) * 10 + (_bytes[nPos + 3] & 0x0f);
//                    if (lbcd > 99999999)
//                        lbcd = 99999999;
//                    dataReadValue.GetData().SetData(i, lbcd);
//                }
//                break;
//                case LONG: {
//                    long lValue = 0L;
//                    for (int m = 0; m < nCellSize; ++m)
//                        lValue |= ((_bytes[nPos + m] & 0xff) << ((nCellSize - m - 1) * 8));
//
//                    dataReadValue.GetData().SetData(i, lValue);
//                }
//                break;
//                case SHORT:
//                case WORD:
//                case DWORD: {
//                    int nValue = 0;
//                    for (int m = 0; m < nCellSize; ++m)
//                        nValue |= ((_bytes[nPos + m] & 0xff) << ((nCellSize - m - 1) * 8));
//                    dataReadValue.GetData().SetData(i, nValue);
//                }
//                break;
//                case FLOAT: {
//                    int nVal = 0;
//                    for (int m = 0; m < nCellSize; ++m) {
//                        nVal |= ((_bytes[nPos + m] & 0xff) << ((nCellSize - 1 - m) * 8));
//                    }
//                    nVal &= 0xffffffff;
//                    dataReadValue.GetData().SetData(i, Float.intBitsToFloat(nVal));
//                }
//                break;
//                case DOUBLE: {
//                    long lVal = 0L;
//                    for (int m = 0; m < nCellSize; ++m) {
//                        lVal |= ((_bytes[nPos + m] & 0xff) << ((nCellSize - 1 - m) * 8));
//                    }
//                    dataReadValue.GetData().SetData(i, Double.longBitsToDouble(lVal));
//                }
//                break;
//                case STRING: {
//                    try {
//                        dataReadValue.GetData().SetData(i, new String(_bytes, nPos, nCellSize, strCharsetName));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//                default:
//                    break;
//            }
//        }
//    }


}
