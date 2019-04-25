package moniter.tyw.com.moniterlibrary.Chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import moniter.tyw.com.moniterlibrary.common.Tag;

public class GroupNameValueFormatter extends ValueFormatter {
    public Tag tag;

    public GroupNameValueFormatter(Tag _tag) {
        super();
        tag = _tag;
    }

    public void SetTag( Tag _tag){tag=_tag;}

    @Override
    public String getFormattedValue(float value) {
        if(tag==null)return " ";
        return tag.GetChart().GetViewXAxisString(value);
    }
}
