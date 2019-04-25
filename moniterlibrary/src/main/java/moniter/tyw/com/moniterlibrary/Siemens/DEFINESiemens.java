package moniter.tyw.com.moniterlibrary.Siemens;

import moniter.tyw.com.moniterlibrary.common.DEFINE;

public class DEFINESiemens extends DEFINE {

    /**
     *  Siemens S7  设备模型
     */
    public enum SiemensS7DeviceModel{
        Unknown(0x00,"Unknown","未知"),
        S7_200(0x01,"S7-200","S7-200"),
        S7_300(0x02,"S7-300","S7-300"),
        S7_400(0x03,"S7-400","S7-400"),
        S7_1200(0x04,"S7-1200","S7-1200"),
        S7_1500(0x05,"S7-1500","S7-1500"),
        S7_300_NetLink(0x06,"S7-300-NetLink","S7-300-NetLink"),
        S7_400_NetLink(0x07,"S7-400-NetLink","S7-400-NetLink");

        SiemensS7DeviceModel(int _value, String _name, String _description)
        {
            modelValue = _value;
            modelName = _name;
            modelDescription = _description;
        }

        public static SiemensS7DeviceModel GetModelWithValue(int _value)
        {
            for(SiemensS7DeviceModel i : SiemensS7DeviceModel.values())
                if(i.IsModel(_value))
                    return i;
            return Unknown;
        }

        public String GetName(){return modelName;}
        public String GetDescription(){return modelDescription;}

        public boolean IsModel(SiemensS7DeviceModel mat){return (modelValue==mat.modelValue);}
        public boolean IsModel(int _value){return (modelValue==_value);}
        // 权限值
        private int modelValue;
        private String modelName,modelDescription;
    }

    /**
     *  Siemens  S7连接类型
     */
    public enum SiemensS7LinkType{
        Unknown(0x00,"Unknown","未知"),
        PG(0x01,"PG","PG"),
        OP(0x02,"OP","OP"),
        S7_BASIC(0x03,"S7-BASIC","S7-BASIC") ;

        SiemensS7LinkType(int _value, String _name, String _description)
        {
            nValue = _value;
            strName = _name;
            strDescription = _description;
        }

        public static SiemensS7LinkType GetLinkTypeWithValue(int _value)
        {
            for(SiemensS7LinkType i : SiemensS7LinkType.values())
                if(i.IsType(_value))
                    return i;
            return Unknown;
        }

        public String GetName(){return strName;}
        public String GetDescription(){return strDescription;}

        public boolean IsType(SiemensS7LinkType mat){return (nValue==mat.nValue);}
        public boolean IsType(int _value){return (nValue==_value);}
        // 权限值
        private int nValue;
        private String strName,strDescription;
    }

    /**
     *  Siemens S7 字节序
     */
    public enum SiemensS7ByteOrder{
        Unknown(0x00,"Unknown","未知"),
        BigEndian(0x01,"BigEndian","BigEndian(S7 default)"),
        LittleEndian(0x02,"LittleEndian","LittleEndian")  ;

        SiemensS7ByteOrder(int _value, String _name, String _description)
        {
            nValue = _value;
            strName = _name;
            strDescription = _description;
        }

        public static SiemensS7ByteOrder GetByteOrderWithValue(int _value)
        {
            for(SiemensS7ByteOrder i : SiemensS7ByteOrder.values())
                if(i.IsByteOrder(_value))
                    return i;
            return Unknown;
        }

        public String GetName(){return strName;}
        public String GetDescription(){return strDescription;}

        public boolean IsByteOrder(SiemensS7ByteOrder mat){return (nValue==mat.nValue);}
        public boolean IsByteOrder(int _value){return (nValue==_value);}
        // 权限值
        private int nValue;
        private String strName,strDescription;
    }

    /**
     *  Siemens S7 内存类型
     */
    public enum SiemensS7MemoryType{
        Undefined(0x00,"Undefined","未知"),
        DigitalInput(0x81,"S7AreaPE (Digital Input)","S7AreaPE (Digital Input)"),
        DigitalOutput(0x82,"S7AreaPA (Digital Output)","S7AreaPA (Digital Output)") ,
        Merkers(0x83,"S7AreaMK (Merkers)","S7AreaMK (Merkers)")  ,
        DB(0x84,"S7AreaDB (Data block)","S7AreaDB (Data block)") ,
        Timers(0x1C,"S7AreaTM (Timers)","S7AreaTM (Timers)") ,
        Counters(0x1D,"S7AreaCT (Counters)","S7AreaCT (Counters)") ;
        SiemensS7MemoryType(int _value, String _name, String _description)
        {
            nValue = _value;
            strName = _name;
            strDescription = _description;
        }

        public static SiemensS7MemoryType GetTypeWithValue(int _value)
        {
            for(SiemensS7MemoryType i : SiemensS7MemoryType.values())
                if(i.IsType(_value))
                    return i;
            return Undefined;
        }

        public String GetName(){return strName;}
        public String GetDescription(){return strDescription;}

        public boolean IsType(SiemensS7MemoryType mat){return (nValue==mat.nValue);}
        public boolean IsType(int _value){return (nValue==_value);}
        // 权限值
        private int nValue;
        private String strName,strDescription;
    }


    /**
     *  Siemens S7 数据类型
     */
    public enum SiemensS7DataType{
        Undefined(0x00,"Undefined","未知"),
        X(0x01,"S7Bool","Bool"),
        Char(0x02,"S7Char","Char"),
        Byte(0x03,"S7Byte","Byte"),
        Word(0x04,"S7Word","Word"),
        INT(0x05,"S7Int","Int"),
        DWORD(0x06,"S7DWord","DWord"),
        DINT(0x07,"S7DInt","DInt"),
        REAL(0x08,"S7Float","Float"),
        STRING(0x09,"S7String","String"),
        DATE(0x0A,"S7Date","Date"),
        DT(0x0B,"S7DT","DT"),
        TIME(0x0C,"S7TIME","TIME"),
        TOD(0x0D,"S7TOD","TOD") ;

        SiemensS7DataType(int _value, String _name, String _description)
        {
            nValue = _value;
            strName = _name;
            strDescription = _description;
        }

        public static SiemensS7DataType GetDataTypeWithValue(int _value)
        {
            for(SiemensS7DataType i : SiemensS7DataType.values())
                if(i.IsDataType(_value))
                    return i;
            return Undefined;
        }

        public String GetName(){return strName;}
        public String GetDescription(){return strDescription;}

        public boolean IsDataType(SiemensS7DataType mat){return (nValue==mat.nValue);}
        public boolean IsDataType(int _value){return (nValue==_value);}
        // 权限值
        private int nValue;
        private String strName,strDescription;
    }

}
