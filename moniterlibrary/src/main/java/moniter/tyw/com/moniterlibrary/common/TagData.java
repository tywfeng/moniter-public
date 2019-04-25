package moniter.tyw.com.moniterlibrary.common;

/**
 * Tag数据
 */
public class TagData {

    private DEFINE.ErrorCode errorCode = DEFINE.ErrorCode.None;                 // 错误码
    private DEFINE.DataQualityCode qualityCode = DEFINE.DataQualityCode.NoQualityNoValue;         // 质量码
    private long timestamp = 0L;                             // 时间戳
    private Variant dataValue;                          // 数据项


    public TagData(DataType _dt, int _stringBytesize, int _arrayCols, int _arrayRows) {
        Init( _dt,  _stringBytesize,  _arrayCols,  _arrayRows);
    }
    // used by JNI call , 修改请注意C端代码
    private TagData(int _dtValue,int _quality, int _code, long _timestamp, int _stringBytesize, int _arrayCols, int _arrayRows ) {
        SetQualityCode(_quality);
        SetErrorCode(_code);
        SetTimestampMS(_timestamp);
        Init(DataType.GetDataTypeWithValue(_dtValue),_stringBytesize,  _arrayCols,  _arrayRows);
    }
    private void Init(DataType _dt, int _stringBytesize, int _arrayCols, int _arrayRows)
    {
        dataValue = new Variant(_dt, _stringBytesize, _arrayCols, _arrayRows);
    }
    public void SetData(DEFINE.ErrorCode _errorCode, DEFINE.DataQualityCode _qualityCode, long _timestamp, Variant _dataValue) {
        errorCode = _errorCode;
        qualityCode = _qualityCode;
        timestamp = _timestamp;
        dataValue.SetVariant(_dataValue);
    }

    // used by JNI call , 修改请注意C端代码
    public Variant GetData() {
        return dataValue;
    }

    public void SetDataValue(Variant dataValue) {
        this.dataValue = dataValue;
    }

    public long GetTimestamp() {
        return timestamp;
    }


    public DEFINE.DataQualityCode GetQualityCode() {
        return qualityCode;
    }

    public void SetQualityCode(DEFINE.DataQualityCode qualityCode) {
        this.qualityCode = qualityCode;
    }


    public DEFINE.ErrorCode GetErrorCode() {
        return errorCode;
    }

    public void SetErrorCode(DEFINE.ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    //
    public void SetData(int _errorCode, int _qualityCode, long _timestamp, Variant _dataValue) {
        SetErrorCode(_errorCode);
        SetQualityCode(_errorCode);
        SetTimestampMS(_timestamp);
        dataValue.SetVariant(_dataValue);
    }
    //
    public void SetTimestampMS(long timestamp) {
        this.timestamp = timestamp;
    }

    //
    public void SetQualityCode(int code) {
        qualityCode = DEFINE.DataQualityCode.GetQualityCodeWithValue(code);
    }

    //
    public void SetErrorCode(int code) {
        errorCode = DEFINE.ErrorCode.GetErrorCodeWithValue(code);
    }

}
