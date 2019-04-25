package com.tyw.moniter.main.ui.View;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.tyw.moniter.main.R;

import java.text.NumberFormat;

import androidx.core.content.ContextCompat;
import moniter.tyw.com.moniterlibrary.Chart.TagBarChartDataEntry;
import moniter.tyw.com.moniterlibrary.Chart.TagLineChartDataEntry;
import moniter.tyw.com.moniterlibrary.Chart.TagPieChartDataEntry;
import moniter.tyw.com.moniterlibrary.Utils.TimeUtils;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.Tag;
import moniter.tyw.com.moniterlibrary.common.TagChart;

public class ChartMarkerView01 extends MarkerView {
    private final TextView tvContent;
    private TagChart chart;
    private Context mContext;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ChartMarkerView01(TagChart _chart, Context context, int layoutResource) {
        super(context, layoutResource);
        mContext = context;
        tvContent = findViewById(R.id.tvContent);
        chart = _chart;
    }

    // 更新图表
    public void UpdateChart(TagChart _chart) {
        chart = _chart;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            // 线形图表
            if (e instanceof TagLineChartDataEntry) {
                TagLineChartDataEntry tagEntry = (TagLineChartDataEntry) e;
                StringBuilder builder = new StringBuilder();
                builder.append(TimeUtils.TimestampToDatetimeString(tagEntry.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);
                //builder.append(nf.format(e.getY()));
                // 警告码
                // 警告颜色+信息
                if (!DEFINE.DataQualityCode.IsGoodCode(tagEntry.GetQuality())) {
                    int color = tagEntry.GetQuality().GetColor();
                    builder.append(mContext.getString(R.string.chartmarker_warning_single_item_parthtml,
                            (Integer.toHexString((color & 0xff) | (color & 0xff00) | (color & 0xff0000)))
                            , nf.format(e.getY()), tagEntry.GetQuality().GetName()));
                } else {
                    builder.append(mContext.getString(R.string.chartmarker_normal_single_item_parthtml, nf.format(e.getY())));
                }
                tvContent.setText(Html.fromHtml(builder.toString()));
            } else if (e instanceof TagBarChartDataEntry) {
                // 柱形图表
                TagBarChartDataEntry tagEntry = (TagBarChartDataEntry) e;
                StringBuilder builder = new StringBuilder();
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);

                // 栈型显示所有数据
                if (chart != null && chart.chartStyle.IsStyle(DEFINE.ChartStyle.StackMultipleTag)) {
                    DEFINE.DataQualityCode qualitys[] = tagEntry.GetQualityStack();
                    String[] labels = chart.GetBarData().getDataSetByIndex(0).getStackLabels();
                    float[] vals = tagEntry.getYVals();
                    builder.append(TimeUtils.TimestampToDatetimeString(tagEntry.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                    if (vals == null) {
                        builder.append(System.getProperty("line.separator")).append(nf.format(e.getY())).append("[unknown?]");
                    } else {
                        int index = 0;
                        int maxIndex = Math.max(1, labels.length);
                        for (float val : vals) {
                            if (!DEFINE.DataQualityCode.IsGoodCode(qualitys[index])) {
                                int color = qualitys[index].GetColor();
                                builder.append(mContext.getString(R.string.chartmarker_stack_warning_item_parthtml,
                                        (Integer.toHexString((color & 0xff) | (color & 0xff00) | (color & 0xff0000))),
                                        labels[Math.min(maxIndex, index)]
                                        , nf.format(val), qualitys[index].GetName()));
                            } else {
                                builder.append(mContext.getString(R.string.chartmarker_normal_stack_item_parthtml,
                                        labels[Math.min(maxIndex, index)], nf.format(val)));
                            }
                            index++;
                        }
                    }
                } else {
                    builder.append(TimeUtils.TimestampToDatetimeString(tagEntry.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
                    //builder.append();
                    // 警告颜色+信息
                    if (!DEFINE.DataQualityCode.IsGoodCode(tagEntry.GetQuality())) {
                        int color = tagEntry.GetQuality().GetColor();
                        builder.append(mContext.getString(R.string.chartmarker_warning_single_item_parthtml,
                                (Integer.toHexString((color & 0xff) | (color & 0xff00) | (color & 0xff0000)))
                                , nf.format(e.getY()), tagEntry.GetQuality().GetName()));
                    } else {
                        builder.append(mContext.getString(R.string.chartmarker_normal_single_item_parthtml, nf.format(e.getY())));
                    }
                }
                tvContent.setText(Html.fromHtml(builder.toString()));
            } else if (e instanceof TagPieChartDataEntry) {
                // 饼状图
                TagPieChartDataEntry tagEntry = (TagPieChartDataEntry) e;
                StringBuilder builder = new StringBuilder();
                builder.append(TimeUtils.TimestampToDatetimeString(tagEntry.getTimestamp(), "yyyy-MM-dd HH:mm:ss")).append(System.getProperty("line.separator"));
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(Tag.MaximumFractionDigits);
                // 警告颜色+信息
                if (!DEFINE.DataQualityCode.IsGoodCode(tagEntry.GetQuality())) {
                    int color = tagEntry.GetQuality().GetColor();
                    builder.append(mContext.getString(R.string.chartmarker_warning_single_item_parthtml,
                            (Integer.toHexString((color & 0xff) | (color & 0xff00) | (color & 0xff0000)))
                            , nf.format(e.getY()), tagEntry.GetQuality().GetName()));
                } else {
                    builder.append(mContext.getString(R.string.chartmarker_normal_single_item_parthtml, nf.format(e.getY())));
                }
                tvContent.setText(Html.fromHtml(builder.toString()));
            } else {

                tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
            }
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
