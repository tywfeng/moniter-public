package moniter.tyw.com.moniterlibrary.Chart;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;

import moniter.tyw.com.moniterlibrary.common.Tag;

public class PercentValueFormatter extends PercentFormatter {
    private PieChart mPieChart;

    public PercentValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public PercentValueFormatter(PieChart pieChart) {
        this();
        this.mPieChart = pieChart;
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " %";
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        if (mPieChart != null && mPieChart.isUsePercentValuesEnabled()) {
            // Converted to percent
            return  getFormattedValue(value);
        } else {
            // raw value, skip percent sign
            return mFormat.format(value);
        }
    }
    public void SetChart(PieChart _chart) {
        mPieChart = _chart;
    }
}
