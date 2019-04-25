package moniter.tyw.com.moniterlibrary.common;

public enum DataType {

    UNDEFINED(0, 0, "Undefined", "undefined"),
    BOOL(1, 1, "Bool", "true or false"),
    BYTE(2, 1, "Byte", "BYTE 0x00-0xff"),
    CHAR(3, 2, "Char", "CHAR"),                 // CHAR实际1字节
    WORD(4, 4, "Word", "WORD"),                 // WORD实际2字节
    SHORT(5, 2, "Short", "SHORT"),
    DWORD(6, 8, "DWord", "DWORD"),              //DWORD 实际4字节
    LONG(7, 8, "Long", "LONG"),                 //LONG 实际4字节
    FLOAT(8, 4, "Float", "FLOAT"),
    DOUBLE(9, 8, "Double", "DOUBLE"),
    DATE(10, 8, "Date", "DATE"),
    STRING(11, 0, "String", "STRING"),
    BCD(12, 4, "BCD", "HEX:0x1928;BCD: 1*1000+9*100+2*10+8;ranges from 0 to 9999"), // BCD实际2字节，
    LBCD(13, 8, "LBCD", "ranges from 0 to 99999999");           // LBCD实际4字节


    /**
     *
     * @param _index
     * @param _cellBytesize 类型在Android端存储需要字节数
     * @param name
     * @param _description
     */
    DataType(int _index, int _cellBytesize, String name, String _description) {
        index = _index;
        strName = name;
        cellBytesize = _cellBytesize;
        strDescription = _description;
    }
    public static DataType GetDataTypeWithValue(int value)
    {
        for (DataType i : DataType.values()) {
            if (i.IsType(value)) {
                return i;
            }
        }
        return UNDEFINED;
    }

    public int GetEnumValue() {
        return index;
    }

    /**
     * 获取类型名
     *
     * @return 例: Word
     */
    public String GetName() {

        return strName;
    }

    public int Sizeof() {
        return cellBytesize;
    }

    /**
     * 检查类型是否一致
     *
     */
    public boolean IsType(DataType dt) {

        if ( dt.GetEnumValue() == GetEnumValue()) return true;
        return false;
    }

    public boolean IsType(int _value) {
        if (_value == GetEnumValue()) return true;
        return false;
    }

    // DEFINE
    final private int index, cellBytesize;
    private String strName, strDescription;
}
