package moniter.tyw.com.moniterlibrary.common;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moniter.tyw.com.moniterlibrary.Utils.DebugLog;

public class TagAlarm {

    public static byte ALARM_COLOR_ALPHA = (byte)0x70;                // 告警颜色透明度
    // 告警区间JNI配置文件JSON格式为TagAlarmInterval数组
    //TagAlarmInfo format: [{intervalTypeValue,leftIntervalValue,rightIntervalValue,ColorValue,"AlarmName","description"},{},] 例：[{0,0,0,0,"Auto","Auto的描述"},{0,1,1,23,"SWPL","SWPL的描述"}]
    public List<TagAlarmInfo> mAlarmInfoList = new ArrayList<TagAlarmInfo>();

    private Tag mTag;
    public TagAlarm(Tag _tag) {
        mTag=_tag;
    }

    // 设置告警区间
    public void SetAlarmIntervalList(String _json) {
        if(_json==null)return;
        JSONArray jsonArray = null;
        // 清空原数据
        mAlarmInfoList.clear();
        try {
            jsonArray = new JSONArray(_json);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); ++i) {
                    // 遍历提取告警范围
                    if (jsonArray.isNull(i)) continue;
                    JSONObject jsonitem = jsonArray.getJSONObject(i);
                    if (jsonitem == null) continue;
                    TagAlarmInfo alarmItem = TagAlarmInfo.AlarmInvalFromJSON(jsonitem);
                    if (alarmItem == null) continue;
                    // 添加至列表
                    mAlarmInfoList.add(alarmItem);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 获取告警信息
    public TagAlarmInfo GetAlarmInfoWithValue(double _value)
    {
        for(TagAlarmInfo alarm: mAlarmInfoList)
        {
            if(alarm==null || !alarm.IsMatch(_value))continue;
            return alarm;
        }
        return null;
    }

    /**
     * 告警区间类
     */
    public static class TagAlarmInfo {
        TagAlarmInfo(int _typeValue, double _leftValue, double _rightValue, int _colorValue, String _name, String _description) {
            intervalType = DEFINE.IntervalType.GetTypeWithValue(_typeValue);
            leftValue = Math.min(_leftValue, _rightValue);
            rightValue = Math.max(_leftValue, _rightValue);
            alarmColorValue = _colorValue;
            alarmName = _name;
            alarmDescription = _description;
        }

        public String GetName() {
            return alarmName;
        }

        public String GetDescription() {
            return alarmDescription;
        }

        public int getColor() {
            return Color.argb(ALARM_COLOR_ALPHA,alarmColorValue&0xff,alarmColorValue>>8&0xff,alarmColorValue>>16&0xff);
        }

        // (json在线工具http://www.bejson.com/ )
        // 例：cell: {"t":0,"l":1,"r":1,"c":23,"n":"SWPL","d":"SWPL的描述"}
        public static TagAlarmInfo AlarmInvalFromJSON(JSONObject _json) {
            try {
                return new TagAlarmInfo(_json.getInt("t"),
                        _json.getDouble("l"), _json.getDouble("r"),
                        _json.getInt("c"), _json.getString("n"), _json.getString("d"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // 从列表中查找匹配项
        public static TagAlarmInfo GetAlarmInvalWithValue(List<TagAlarmInfo> _list, double _value) {
            for (TagAlarmInfo item : _list) if (item.IsMatch(_value)) return item;
            return null;
        }

        /**
         * 匹配查询
         *
         * @return
         */
        public boolean IsMatch(double _value) {
            switch (intervalType) {
                case leftClosedRightClosed:
                    if (_value >= leftValue && _value <= rightValue) return true;
                    else return false;
                case leftClosedRightOpening:
                    if (_value >= leftValue && _value < rightValue) return true;
                    else return false;
                case leftOpeningRightClosed:
                    if (_value > leftValue && _value <= rightValue) return true;
                    else return false;
                case leftOpeningRightOpening:
                    if (_value > leftValue && _value < rightValue) return true;
                    else return false;
                default:
                    break;
            }
            return false;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder(GetName());
            switch (intervalType) {
            case leftClosedRightClosed:
               sb.append("[").append(leftValue).append(",").append(rightValue).append("]");
               break;
            case leftClosedRightOpening:
                sb.append("[").append(leftValue).append(",").append(rightValue).append(")");
                break;
            case leftOpeningRightClosed:
                sb.append("(").append(leftValue).append(",").append(rightValue).append("]");
                break;
            case leftOpeningRightOpening:
                sb.append("(").append(leftValue).append(",").append(rightValue).append(")");
                break;
            default:
                break;
        }
            sb.append(GetDescription());
            return sb.toString();
        }

        // 类型值
        private DEFINE.IntervalType intervalType = DEFINE.IntervalType.leftClosedRightClosed; // 区间类型
        private double leftValue, rightValue;       // 区间左右值
        private int alarmColorValue;                // 告警颜色
        private String alarmName, alarmDescription; // 名称描述
    }
}
