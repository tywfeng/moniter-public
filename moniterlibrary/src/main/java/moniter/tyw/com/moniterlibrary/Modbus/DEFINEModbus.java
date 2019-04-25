package moniter.tyw.com.moniterlibrary.Modbus;

import moniter.tyw.com.moniterlibrary.common.DEFINE;

public class DEFINEModbus extends DEFINE {
    /**
     * modbus 地址类型
     */
    public enum ModbusAddressType{
        Unknown(0x00,"Unknown","未知"),
        OutputCoils(0x01,"OutputCoils","输出线圈"),
        InputCoils(0x02,"InputCoils","输入线圈"),
        InternalRegisters(0x03,"InternalRegisters","InternalRegisters"),
        HoldingRegisters(0x04,"HoldingRegisters","保持寄存器");

        ModbusAddressType(int _value, String _name, String _description)
        {
            modbusAddressTypeValue = _value;
            modbusAddressTypeName = _name;
            modbusAddressTypeDescription = _description;
        }

        public static ModbusAddressType GetModbusAddressTypeWithValue(int _value)
        {
            for(ModbusAddressType i : ModbusAddressType.values())
                if(i.IsType(_value))
                    return i;
            return Unknown;
        }

        public String GetName(){return modbusAddressTypeName;}
        public String GetDescription(){return modbusAddressTypeDescription;}

        public boolean IsType(ModbusAddressType mat){return (modbusAddressTypeValue==mat.modbusAddressTypeValue);}
        public boolean IsType(int _value){return (modbusAddressTypeValue==_value);}
        // 权限值
        private int modbusAddressTypeValue;
        private String modbusAddressTypeName,modbusAddressTypeDescription;
    }

}
