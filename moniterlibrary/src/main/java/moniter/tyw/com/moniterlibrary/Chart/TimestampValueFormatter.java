package moniter.tyw.com.moniterlibrary.Chart;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;

import moniter.tyw.com.moniterlibrary.Utils.TimeUtils;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.Tag;

public class TimestampValueFormatter extends ValueFormatter {

    public Tag tag;

    public TimestampValueFormatter(Tag _tag) {
        super();
        tag = _tag;
    }

    public void SetTag(Tag _tag) {
        tag = _tag;
    }

    @Override
    public String getFormattedValue(float value) {
        if (tag == null) return "--";
        return tag.GetChart().GetViewXAxisString(value);
    }

    @Override
    public String getBarStackedLabel(float value, BarEntry entry) {
        if(tag==null)return "";
        if (tag.GetChart().chartStyle.IsStyle(DEFINE.ChartStyle.StackMultipleTag)
                ) {
            // 百分比则显示所有数值（优先处理）
            if(tag.GetChart().bPercentValue)
            {
                if(value>0.000001f && entry.getY()>0.0000001f)
                {
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);
                    return new StringBuilder(nf.format(value/entry.getY())).append("%").toString();
                }else
                {
                    return "0%";
                }
            }else if (!tag.GetChart().bDrawWholeStack) {
                float[] vals = entry.getYVals();
                if (vals != null) {
                    // find out if we are on top of the stack
                    if (vals[vals.length - 1] == value) {
                        // return the "sum" across all stack values
                        NumberFormat nf = NumberFormat.getNumberInstance();
                        nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);
                        return new StringBuilder(nf.format(entry.getY())).toString();
                    } else {
                        return ""; // return empty
                    }
                }
            }
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);
        // return the "proposed" value
        return new StringBuilder(nf.format(value)).toString();
    }
}
