package moniter.tyw.com.moniterlibrary.Chart;

import com.github.mikephil.charting.data.PieEntry;

import moniter.tyw.com.moniterlibrary.common.DEFINE;

public class TagPieChartDataEntry extends PieEntry {
    private long timestamp;

    private DEFINE.DataQualityCode quality;
    private DEFINE.ErrorCode errorCode;
    public TagPieChartDataEntry(DEFINE.DataQualityCode _quality, DEFINE.ErrorCode _error, long _timestamp, float value) {
        super(value);
        timestamp = _timestamp;
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
    public TagPieChartDataEntry(DEFINE.DataQualityCode _quality, DEFINE.ErrorCode _error,long _timestamp, double value, String label) {
        super(Double.valueOf(value).floatValue(), label);
        timestamp = _timestamp;
        quality = _quality;
        errorCode = _error;
    }
}
