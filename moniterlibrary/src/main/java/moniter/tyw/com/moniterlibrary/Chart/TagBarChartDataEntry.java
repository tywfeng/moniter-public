package moniter.tyw.com.moniterlibrary.Chart;

import com.github.mikephil.charting.data.BarEntry;

import moniter.tyw.com.moniterlibrary.common.DEFINE;

public class TagBarChartDataEntry extends BarEntry {
    private long timestamp;

    private DEFINE.DataQualityCode quality;
    private DEFINE.ErrorCode errorCode;
    private DEFINE.DataQualityCode qualityStack[];
    private DEFINE.ErrorCode errorCodeStack[];

    public TagBarChartDataEntry(DEFINE.DataQualityCode _quality, DEFINE.ErrorCode _error, long _timestamp, int x, double y) {
        super(x, Double.valueOf(y).floatValue());
        timestamp = _timestamp;
        quality = _quality;
        errorCode = _error;
    }

    public TagBarChartDataEntry(DEFINE.DataQualityCode[] _qualityStack, DEFINE.ErrorCode[] _errorStack, long _timestamp, int x, float[] _vals) {
        super(x, _vals);
        timestamp = _timestamp;
        qualityStack = _qualityStack;
        errorCodeStack = _errorStack;
    }

    public DEFINE.DataQualityCode GetQuality() {
        return quality;
    }

    public DEFINE.ErrorCode GetErrorCode() {
        return errorCode;
    }
    public DEFINE.DataQualityCode[] GetQualityStack() {
        return qualityStack;
    }

    public DEFINE.ErrorCode[] GetErrorCodeStack() {
        return errorCodeStack;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
