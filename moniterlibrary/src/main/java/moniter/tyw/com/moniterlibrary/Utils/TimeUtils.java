package moniter.tyw.com.moniterlibrary.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前时间戳
     * @return
     */
    public static long GetCurrentTimestamp(){ return System.currentTimeMillis();}

    /**
     * 时间戳转时间字符串
     * @param timestamp
     * @return
     */
    public static String TimestampToDatetimeString(long timestamp){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeFormat);
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);
    }

    /**
     * 按照format格式将时间戳转为时间字符串
     * @param timestamp
     * @param format
     * @return
     */
    public static String TimestampToDatetimeString(long timestamp, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( format);
        Date date = new Date(timestamp);
       // DebugLog.d(""+simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }
}
