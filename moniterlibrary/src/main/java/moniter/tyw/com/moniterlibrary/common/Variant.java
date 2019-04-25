package moniter.tyw.com.moniterlibrary.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moniter.tyw.com.moniterlibrary.Utils.DebugLog;

public class Variant {
    //字符串字节数
    private int wStringBytesSize = 0;
    //array
    private int arrayRows = 1, arrayCols = 1;
    // 数据类型
    private DataType dataType;
    // 数据转成byte-array
    private byte[] dataSource;

    // 源数据
    public int nRawDataSize;                   // 源数据大小
    public byte[] cbRawData;                      // 源数据

    /**
     * 构造函数
     *
     * @param _dt
     * @param _stringBytesize
     * @param _arrayCols
     * @param _arrayRows
     */
    public Variant(DataType _dt, int _stringBytesize, int _arrayCols, int _arrayRows) {
        Init(_dt, _stringBytesize, _arrayCols, _arrayRows);
    }

    // for JNI cpp Call 改动需注意
    private Variant(int _dtValue, int _stringBytesize, int _arrayCols, int _arrayRows) {
        DataType dt = DataType.GetDataTypeWithValue(_dtValue);
        Init(dt, _stringBytesize, _arrayCols, _arrayRows);
    }

    /**
     * 构造函数
     *
     * @param _variant
     */
    public Variant(Variant _variant) {
        SetVariant(_variant);
    }

    /**
     * 初始化方法
     *
     * @param _dt
     * @param _stringBytesize
     * @param _arrayCols
     * @param _arrayRows
     */
    private void Init(DataType _dt, int _stringBytesize, int _arrayCols, int _arrayRows) {
        dataType = _dt;
        wStringBytesSize = _stringBytesize;
        SetArray(_arrayCols, _arrayRows);
        if (dataSource == null || dataSource.length < Size()) {
            dataSource = new byte[Size()];
        }

    }

    private int GetDataTypeSizeof() {
        if (dataType.IsType(DataType.STRING)) return wStringBytesSize;
        return dataType.Sizeof();
    }

    /**
     * 设置数组参数
     *
     * @param _arrayCols 列数
     * @param _arrayRows 行数
     */
    public void SetArray(int _arrayCols, int _arrayRows) {
        if (_arrayCols <= 0 || _arrayRows <= 0) return;
        arrayRows = _arrayRows;
        arrayCols = _arrayCols;
    }

    public int GetArrayLength() {
        return arrayRows * arrayCols;
    }

    public int GetArrayRows() {
        return arrayRows;
    }

    public int GetArrayCols() {
        return arrayCols;
    }

    public boolean IsArray() {
        return (GetArrayLength() > 1);
    }

    /**
     * 字节总长度
     *
     * @return 字节总长度
     */
    public int Size() {
        if (dataType.IsType(DataType.STRING)) return wStringBytesSize * GetArrayLength();
        return dataType.Sizeof() * GetArrayLength();
    }

    // for JNI cpp Call 改动需注意
    private boolean SetRawData(int _rawsize, byte[] _data) {
        if (_data == null || _rawsize <= 0) return false;
        if (cbRawData == null || cbRawData.length != _data.length) {
            nRawDataSize = _rawsize;
            cbRawData = new byte[nRawDataSize];
        }
        try {
            System.arraycopy(_data, 0, cbRawData, 0, nRawDataSize);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String GetRawHexString() {
        if (nRawDataSize <= 0 || cbRawData == null || cbRawData.length < nRawDataSize) return "";
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < nRawDataSize; ++i) {
            builder.append(" ").append(String.format("%02X", cbRawData[i]));
        }

        return builder.toString();
    }


    /**
     * 重置数据
     *
     * @param _variant 源数据
     */
    public boolean SetVariant(Variant _variant) {
        // init
        Init(_variant.GetType(), _variant.Size(), _variant.GetArrayCols(), _variant.GetArrayRows());

        // 重置
        SetRawData(_variant.nRawDataSize, _variant.cbRawData);

        // copy data
        return SetBytes(0, Size(), _variant.GetBytes(), 0);
    }

    /**
     * @return 数据类型
     */
    public DataType GetType() {
        return dataType;
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, boolean _data) {
        int len = dataType.Sizeof();
        byte _cbData[] = new byte[len];
        _cbData[0] = (byte) (_data ? 0x1 : 0x0);
        return SetBytes(index * len, len, _cbData, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public boolean GetBool(int index) {
        if (index >= dataSource.length / DataType.BOOL.Sizeof()) return false;
        return (dataSource[index] != 0x0);
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, byte _data) {
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_data >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public byte GetByte(int index) {
        if (index >= dataSource.length / DataType.BYTE.Sizeof()) return 0x0;
        return dataSource[index];
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, char _data) {
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_data >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public char GetChar(int index) {
        if (index >= dataSource.length / DataType.CHAR.Sizeof()) return '\0';
        return (char) dataSource[index];
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, short _data) {
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_data >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public short GetShort(int index) {
        short value = 0;
        if (index >= dataSource.length / DataType.SHORT.Sizeof()) return value;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return value;
    }


    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, int _data) {
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_data >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public int GetWord(int index) {
        int value = 0;
        if (index >= dataSource.length / DataType.WORD.Sizeof()) return value;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return value;
    }

    // used by JNI call , 修改请注意C端代码
    public int GetBCD(int index) {
        int value = 0;
        if (index >= dataSource.length / DataType.BCD.Sizeof()) return value;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return value;
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, long _data) {
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_data >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public long GetDWord(int index) {
        long value = 0;
        if (index >= dataSource.length / DataType.DWORD.Sizeof()) return value;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return value;
    }

    // used by JNI call , 修改请注意C端代码
    public long GetLong(int index) {
        long value = 0;
        if (index >= dataSource.length / DataType.LONG.Sizeof()) return value;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return value;
    }

    // used by JNI call , 修改请注意C端代码
    public long GetLBCD(int index) {
        long value = 0;
        if (index >= dataSource.length / DataType.LONG.Sizeof()) return value;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return value;
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, float _data) {
        int _value = Float.floatToRawIntBits(_data);
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_value >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public float GetFloat(int index) {
        float retValue = 0f;
        int value = 0;
        if (index >= dataSource.length / DataType.FLOAT.Sizeof()) return retValue;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return Float.intBitsToFloat(value);
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, double _data) {
        long _value = Double.doubleToRawLongBits(_data);
        int len = dataType.Sizeof();
        byte b_data[] = new byte[len];
        for (int i = 0; i < len; ++i) {
            b_data[i] = (byte) ((_value >> (8 * i)) & 0xff);
        }
        return SetBytes(index * len, len, b_data, 0);
    }

    // used by JNI call , 修改请注意C端代码
    public double GetDouble(int index) {
        double retValue = 0f;
        long value = 0;
        if (index >= dataSource.length / DataType.DOUBLE.Sizeof()) return retValue;
        for (int i = 0; i < dataType.Sizeof(); ++i) {
            value |= ((long) (dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
        }
        return Double.longBitsToDouble(value);
    }

    // used by JNI call , 修改请注意C端代码
    public boolean SetData(int index, String _data) {
        try {
            byte[] bytes = _data.getBytes("US-ASCII");
            int len = Math.min(GetDataTypeSizeof(), bytes.length);
            Arrays.fill(dataSource,(byte)0);
            return SetBytes(index * GetDataTypeSizeof(), len, bytes, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将字符串转为对应数值
     *
     * @param index
     * @param _str
     * @return
     */
    public boolean SetDataWithString(int index, String _str) {
        if (_str == null) return false;
        // 有效串
        boolean bValidString = true;
        try {
            switch (dataType) {
                case BOOL: {
                    int inputValue = Integer.valueOf(_str);
                    SetData(index, (inputValue != 0));
                }
                break;
                case CHAR: {
                    Integer inputValue = Integer.valueOf(_str);
                    SetData(index, (char) inputValue.byteValue());
                }
                break;
                case BYTE: {
                    Integer inputValue = Integer.valueOf(_str);
                    SetData(index, inputValue.byteValue());
                }
                break;
                case SHORT: {
                    short inputValue = Short.valueOf(_str);
                    SetData(index, inputValue);
                }
                break;
                case WORD:
                case BCD: {
                    int inputValue = Integer.valueOf(_str);
                    SetData(index, inputValue);
                }
                break;
                case LONG:
                case DWORD:
                case LBCD: {
                    long inputValue = Long.valueOf(_str);
                    SetData(index, inputValue);
                }
                break;
                case FLOAT: {
                    float inputValue = Float.valueOf(_str);
                    SetData(index, inputValue);
                }
                break;
                case DOUBLE: {
                    double inputValue = Double.valueOf(_str);
                    SetData(index, inputValue);
                }
                break;
                case STRING:
                {
                    SetData(index,_str);
                }break;
                default: {
                    bValidString = false;
                    break;
                }
            }
        } catch (Exception e) {
            bValidString = false;
        }
        return bValidString;
    }

    // 只有使用HexString写tag值时才会用到此变量
    private byte[] cbArrayWithHexString = new byte[1];

    /**
     * 将十六进制格式字符串转为源数据
     *
     * @param str
     * @return
     */
    public boolean SetDataRawWithHexString(String str) {
        if (str == null || str.trim().equals("")) return false;
        boolean bValidString = true;
        try {
            // 正则过滤非0-9 A-F a-f字符
            String noSpaceString = str.replaceAll("[^0-9|a-f|A-F]", "");
            // 非双倍字节格式
            int nBytesLength = noSpaceString.length();
            if (nBytesLength == 0 || nBytesLength % 2 != 0) return false;
            // 计算字节长度
            nBytesLength = nBytesLength / 2;
            cbArrayWithHexString = new byte[nBytesLength];
            for (int i = 0; i < nBytesLength; ++i) {
                String subStr = noSpaceString.substring(i * 2, i * 2 + 2);
                cbArrayWithHexString[i] = Integer.valueOf(Integer.parseInt(subStr, 16)).byteValue();
            }
        } catch (Exception e) {
            bValidString = false;
            e.printStackTrace();
        }
        return bValidString;
    }

    // 获取字节数组（通过Hex格式字符串转化的）
    public byte[] GetFromHexStringDataRaw() {
        return cbArrayWithHexString;
    }

    /**
     * 将数据转为boolean类型
     *
     * @param index 数组索引
     * @return
     */
    public boolean toBoolean(int index) {
        long lVal = toNumber(index);
        return (lVal != 0L);
    }

    public boolean toBoolean() {
        return toBoolean(0);
    }

    /**
     * 将数据转为long类型
     *
     * @param index 数组索引
     * @return
     */
    public long toNumber(int index) {
        switch (dataType) {
            case BOOL:
            case CHAR: {
                return (long) dataSource[index];
            }
            case BYTE: {
                int byteValue = (dataSource[index] & 0xff);
                return (long) byteValue;
            }
            case BCD:
            case WORD: {
                int value = 0;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return (long) value;
            }
            case SHORT: {
                short value = 0;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return (long) value;
            }
            case LBCD:
            case DWORD:
            case LONG: {
                long value = 0L;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return value;
            }
            case FLOAT: {
                int value = 0;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return Float.valueOf(Float.intBitsToFloat(value)).longValue();
            }
            case DOUBLE: {
                long value = 0L;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((long) (dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return Double.valueOf(Double.longBitsToDouble(value)).longValue();
            }

            default:
                break;
        }

        return 0l;
    }

    public long toNumber() {
        return toNumber(0);
    }

    /**
     * 将数据转为浮点型数据
     *
     * @param index 数组索引
     * @return
     */
    public double toDouble(int index) {
        switch (dataType) {
            case BOOL:
            case CHAR: {
                return (double) dataSource[index];
            }
            case BYTE: {
                int byteValue = (dataSource[index] & 0xff);
                return (double) byteValue;
            }
            case BCD:
            case WORD: {
                int value = 0;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return (double) value;
            }
            case SHORT: {
                short value = 0;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return (double) value;
            }
            case LBCD:
            case DWORD:
            case LONG: {
                long value = 0L;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return (double) value;
            }
            case FLOAT: {
                int value = 0;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return (double) Float.intBitsToFloat(value);
            }
            case DOUBLE: {
                long value = 0L;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((long) (dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return Double.longBitsToDouble(value);
            }
            case DATE: {
                long value = 0L;
                for (int i = 0; i < dataType.Sizeof(); ++i) {
                    value |= ((dataSource[index * dataType.Sizeof() + i] & 0xff) << (8 * i));
                }
                return Double.longBitsToDouble(value);
            }

            default:
                break;
        }

        return 0.0f;
    }

    public double toDouble() {
        return toDouble(0);
    }

    /**
     * 将数据转成字符串
     * BOOL-"On"/"Off"
     *
     * @param index 数组索引
     * @return
     */
    public String toString(int index) {
        // 处理字符串型
        if (dataType == DataType.STRING) {
            int nSize = wStringBytesSize;
            if (nSize <= 0) return "";
            char[] jchars = new char[nSize];
            int i = 0;
            try {
                for (; i < nSize; ++i) {
                    jchars[i] = (char) dataSource[index * nSize + i];
                }
                return new StringBuilder().append(jchars).toString();
                // return new String(dataSource, index * dataType.Sizeof(), dataType.Sizeof(), "US-ASCII");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "[Cannot convert bytes to string]";
        }
        // 处理BOOL型
        if (dataType == DataType.BOOL) {
            boolean bVal = toBoolean(index);
            if (bVal) return "On";
            return "Off";
        }
        // 处理浮点型
        if (dataType == DataType.DOUBLE || dataType == DataType.FLOAT) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);
            return nf.format(toDouble());
        }
        // 处理数值型
        return Long.toString(toNumber(index));
    }

    public String toString() {
        return toString(0);
    }

    /**
     * 设置byte[]数据源
     *
     * @param _start    @dataSource 目标位置
     * @param _len      拷贝长度
     * @param _src      源数据
     * @param _srcstart 源数据开始拷贝位置
     * @return
     */
    private boolean SetBytes(int _start, int _len, byte[] _src, int _srcstart) {
        try {
            System.arraycopy(_src, _srcstart, dataSource, _start, _len);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return 返回数据源
     */
    public byte[] GetBytes() {
        return dataSource;
    }

    /**
     * 将数据源转为字符串：byte[]-String
     *
     * @return
     */
    public String bytesToString() {
        StringBuilder sb = new StringBuilder();
        int nLength = Size();
        for (int i = 0; i < nLength; ++i) {
            sb.append(Integer.toHexString(dataSource[i] & 0xff)).append(' ');
        }
        return sb.toString();
    }

}
