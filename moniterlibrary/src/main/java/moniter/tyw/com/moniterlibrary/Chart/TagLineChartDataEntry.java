package moniter.tyw.com.moniterlibrary.Chart;

import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.Date;

import moniter.tyw.com.moniterlibrary.common.DEFINE;

public class TagLineChartDataEntry extends Entry {
    private long timestamp;
    private DEFINE.DataQualityCode quality;
    private DEFINE.ErrorCode errorCode;
    public TagLineChartDataEntry(DEFINE.DataQualityCode _quality, DEFINE.ErrorCode _error, long _timestamp, int x, double y)
    {
        super(x,Double.valueOf(y).floatValue());
        timestamp=_timestamp;
        quality = _quality;
        errorCode = _error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public DEFINE.DataQualityCode GetQuality() {
        return quality;
    }

    public DEFINE.ErrorCode GetErrorCode() {
        return errorCode;
    }
    //    public String getXAxisTimestampString()
//    {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "HH:mm:ss");
//        Date date = new Date(timestamp);
//        // DebugLog.d(""+simpleDateFormat.format(date));
//        return simpleDateFormat.format(date);
//    }
}
