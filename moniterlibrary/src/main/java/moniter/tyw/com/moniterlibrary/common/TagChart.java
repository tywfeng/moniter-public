package moniter.tyw.com.moniterlibrary.common;

import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moniter.tyw.com.moniterlibrary.Chart.TagBarChartDataEntry;
import moniter.tyw.com.moniterlibrary.Chart.TagLineChartDataEntry;
import moniter.tyw.com.moniterlibrary.Chart.TagPieChartDataEntry;
import moniter.tyw.com.moniterlibrary.Utils.TimeUtils;

import static moniter.tyw.com.moniterlibrary.common.DEFINE.ChartDatasetType.LastRealtimeData;
import static moniter.tyw.com.moniterlibrary.common.DEFINE.ChartDatasetType.RealtimeDataset;

public class TagChart {
    public static final int SINGLE_GROUP_ID = 0;                // 默认组ID
    public static byte CHART_COLOR_ALPHA = (byte)0x70;                // 图表颜色透明度
    // Identification
    protected String strChartName = ""                      // 表/组项 名字 非group时为左上角名字，group时为单条名称
            , strChartDescription = "";                   //    描述
    protected Tag tagParent;                                                    // 所属Tag
    //group
    public int nChartGroup = SINGLE_GROUP_ID;                                     // chart所在组,同一设备内：相同组一起绘制，0为独立组
    public String strGroupName = ""                         // 组名 为group时作为左上角文字
            , strGroupDescription = "";                    //  描述
    public String strGroupComment = "";                                           //组备注

    private int colorKey1 = ColorTemplate.getHoloBlue();//渲染需求颜色（LineChart:主色）（BarChart:主色）
    private int colorKey2 = Color.BLACK; //渲染需求颜色(LineChart:FillColor)
    public DEFINE.ChartType chartType = DEFINE.ChartType.None;                  // 类型
    public DEFINE.ChartStyle chartStyle = DEFINE.ChartStyle.Unknown;            // 样式
    public DEFINE.ChartDatasetType chartDatasetType;                             // 数据源类型

    //        chartType=DEFINE.ChartType.LineChart;
//        chartStyle = DEFINE.ChartStyle.BasicSingleTag;
    public String strStyleDescription = "";                                     // style描述
    //public String strXAxis = "", strYAxis = "";                                 // XY轴名称
    public String strComment = "";                                              //备注


    // 运行绘图表控制信息
    private boolean bChartVisible = false;             // 图表可见性
    public boolean bDrawValue = true;           // 图表中是否显示值
    public boolean bDrawFilled = true;            // 图表是否填充
    public boolean bPointCircles = true;          // 数据点是否显示圆
    public boolean bPinchZoom = false;            // 捏缩放
    public boolean bAutoScale = true;             //
    public boolean bHighlight = true;             // 高亮聚焦(十字线)
    public boolean bDrawMarker = true;            // 显示marker
    public boolean bAutoMoveToRight = true;         // 自动移动到最右
    public boolean bPercentValue = false;          // 百分比显示 (饼状图/栈型柱状图)
    public boolean bDrawWholeStack = false;         // 绘制全栈数值和

    // 线性图绘制模式
    public LineDataSet.Mode lineDatasetMode = LineDataSet.Mode.CUBIC_BEZIER;
    public int nChartVisibleXRangeMaximum = 10;       // X轴看到最大的数
    //public int nXAxisLabelCount = 5;                // X轴Label数量
    public int nYAxisLabelCount = 5;                // Y轴Label数量
    private float fValueTextSize = 10f;                  // 文字大小dp

    // tagdetail可见性
    public boolean bTagDetailVisible = false;
    // 功能按钮：-横轴最大显示数目， 显示数值， 填充 ， 自动右移，绘制模式， +横轴最大显示数目, 保存

    // 图表数据
    ChartData<?> mChartData;
    // 同组图表 临时数据
    private List<TagChart> mSameGroupIgnoreSelfCharts;

    public ChartData<?> GetChartData() {
        return mChartData;
    }


    // 获取X轴对应值的字符串（时间/序号/告警名）
    public String GetViewXAxisString(float xAxisValue) {
        // 根据不同分类更新数据
        switch (chartType) {
            case LineChart:        // 线形图
            {
                LineData linedata = (LineData) mChartData;
                // 实时数据显示时间戳
                if (RealtimeDataset.IsType(chartDatasetType)) {
                    if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle) ||
                            DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                        // 因为时间相同，所以支取0位置时间即可
                        ILineDataSet set = linedata.getDataSetByIndex(0);
                        Entry entry = set.getEntryForXValue(xAxisValue, Float.NaN);
                        if (entry == null || !(entry instanceof TagLineChartDataEntry))
                            return "--";
                        TagLineChartDataEntry tcde = (TagLineChartDataEntry) entry;
                        return TimeUtils.TimestampToDatetimeString(tcde.getTimestamp(), "HH:mm:ss");
                    }
                } else if (LastRealtimeData.IsType(chartDatasetType) && chartStyle.IsStyle(chartStyle.BasicMultipleTag)) {
                    // 显示表名 点图实时重叠
                    // 获取列表
                    if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                        break;
                    int index = (int) xAxisValue;
                    if (index >= 0 && index < mSameGroupIgnoreSelfCharts.size())
                        return mSameGroupIgnoreSelfCharts.get(index).strChartName;
                    return "";
                }
            }
            break;
            case BarChart: {
                BarData barData = (BarData) mChartData;
                // 实时数据显示时间戳
                if (RealtimeDataset.IsType(chartDatasetType)) {
                    // 单点及栈型获取方式相同
                    if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)
                            || DEFINE.ChartStyle.StackMultipleTag.IsStyle(chartStyle)) {
                        // 因为时间相同，所以支取0位置时间即可
                        IBarDataSet set = barData.getDataSetByIndex(0);
                        Entry entry = set.getEntryForXValue(xAxisValue, Float.NaN);
                        if (entry == null || !(entry instanceof TagBarChartDataEntry)) return "--";
                        TagBarChartDataEntry tcde = (TagBarChartDataEntry) entry;
                        return TimeUtils.TimestampToDatetimeString(tcde.getTimestamp(), "HH:mm:ss");
                    } else if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                        // 获取列表
                        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                            return "";
                        int setIndex = ((int) xAxisValue) % mSameGroupIgnoreSelfCharts.size();
                        IBarDataSet set = barData.getDataSetByIndex(setIndex);
                        Entry entry = set.getEntryForXValue(xAxisValue, Float.NaN);
                        if (entry == null || !(entry instanceof TagBarChartDataEntry)) return "--";
                        TagBarChartDataEntry tcde = (TagBarChartDataEntry) entry;
                        return TimeUtils.TimestampToDatetimeString(tcde.getTimestamp(), "HH:mm:ss");
                    }
                } else if (LastRealtimeData.IsType(chartDatasetType) && chartStyle.IsStyle(chartStyle.BasicMultipleTag)) {
                    // 显示表名 点图实时重叠
                    // 获取列表
                    if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                        break;
                    int index = (int) xAxisValue;
                    if (index >= 0 && index < mSameGroupIgnoreSelfCharts.size())
                        return mSameGroupIgnoreSelfCharts.get(index).strChartName;
                    return "";
                }
            }
            break;
            case PieChart: {
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    return strChartName;
                }
            }
            break;
            default:
                break;
        }
        return " ";
    }


    // 从tag中提取最后一次数据
    public void UpdateChartSrcDataFromTagLastData() {
        if (tagParent == null) return;
        SetChartSrcDataUpdate(tagParent.GetReadData());
    }

    // 新数据
    public void SetChartSrcDataUpdate(TagData _tagdata) {
        if (mChartData == null || _tagdata == null || _tagdata.GetData() == null) return;

        // 根据不同分类更新数据
        switch (chartType) {
            case LineChart:        // 线形图
            {
                LineData linedata = (LineData) mChartData;
                // 单一数据
                if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)) {
                    if (RealtimeDataset.IsType(chartDatasetType)) {
                        linedata.addEntry(new TagLineChartDataEntry(_tagdata.GetQualityCode(),_tagdata.GetErrorCode(),
                                _tagdata.GetTimestamp(), linedata.getEntryCount(), _tagdata.GetData().toDouble()), 0);
                        if (IsVisibleChart())
                            linedata.notifyDataChanged();
                    }
                    break;
                } else if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    // 实时数据累计
                    if (RealtimeDataset.IsType(chartDatasetType)) {
                        // 获取列表
                        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                            break;
                        int setIndex = 0;
                        // 同一时间多点在同一X轴
                        int nCurrentXPos = linedata.getDataSetByIndex(0).getEntryCount();
                        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                            linedata.addEntry(new TagLineChartDataEntry(chart.GetTag().GetReadData().GetQualityCode(),
                                    chart.GetTag().GetReadData().GetErrorCode(),chart.GetTag().GetReadData().GetTimestamp(),
                                    nCurrentXPos, chart.GetTag().GetReadData().GetData().toDouble()), setIndex++);
                        }
                        if (IsVisibleChart())
                            linedata.notifyDataChanged();
                    } else if (LastRealtimeData.IsType(chartDatasetType)) {
                        // 只显示最后一个值
                        // 清空历史
                        for (int i = 0; i < linedata.getDataSetCount(); ++i)
                            linedata.getDataSetByIndex(i).clear();
                        // 获取列表
                        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                            break;
                        int setIndex = 0, posIndex = 0;
                        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                            linedata.addEntry(new TagLineChartDataEntry(chart.GetTag().GetReadData().GetQualityCode(),
                                    chart.GetTag().GetReadData().GetErrorCode(),chart.GetTag().GetReadData().GetTimestamp(),
                                    posIndex++, chart.GetTag().GetReadData().GetData().toDouble()), setIndex++);
                        }
                        if (IsVisibleChart())
                            linedata.notifyDataChanged();
                    }
                    break;
                }
            }
            break;
            case BarChart:         // 柱形图
            {
                BarData barData = (BarData) mChartData;
                if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)) {
                    if (RealtimeDataset.IsType(chartDatasetType)) {
                        barData.addEntry(new TagBarChartDataEntry(_tagdata.GetQualityCode(),_tagdata.GetErrorCode(),
                                _tagdata.GetTimestamp(), mChartData.getEntryCount(), _tagdata.GetData().toDouble()), 0);
                        if (IsVisibleChart())
                            barData.notifyDataChanged();
                    }
                    break;
                } else if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    // 常规多点 + 实时数据
                    if (RealtimeDataset.IsType(chartDatasetType)) {
                        // 获取列表
                        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                            break;
                        int setIndex = 0;
                        int nCurrentXPos = barData.getEntryCount();//barData.getDataSetByIndex(0).getEntryCount();
                        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                            barData.addEntry(new TagBarChartDataEntry(chart.GetTag().GetReadData().GetQualityCode(),
                                    chart.GetTag().GetReadData().GetErrorCode(),chart.GetTag().GetReadData().GetTimestamp(),
                                    nCurrentXPos++, chart.GetTag().GetReadData().GetData().toDouble()), setIndex++);
                        }
                        if (IsVisibleChart())
                            barData.notifyDataChanged();
                    } else if (LastRealtimeData.IsType(chartDatasetType)) {
                        // 常规多点 + 只显示最后一个值处理
                        // 清空历史
                        for (int i = 0; i < barData.getDataSetCount(); ++i)
                            barData.getDataSetByIndex(i).clear();
                        // 获取列表
                        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                            break;
                        int setIndex = 0, posIndex = 0;
                        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                            barData.addEntry(new TagBarChartDataEntry(chart.GetTag().GetReadData().GetQualityCode(),
                                    chart.GetTag().GetReadData().GetErrorCode(),chart.GetTag().GetReadData().GetTimestamp(),
                                    posIndex++, chart.GetTag().GetReadData().GetData().toDouble()), setIndex++);
                        }
                        if (IsVisibleChart())
                            barData.notifyDataChanged();
                    }
                    break;
                } else if (DEFINE.ChartStyle.StackMultipleTag.IsStyle(chartStyle)) {
                    // 栈型柱形图表数据, 只有datasetIndex==0的信息
                    if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                        break;
                    // 栈型 + 实时数据
                    if (RealtimeDataset.IsType(chartDatasetType)) {
                        float values[] = new float[mSameGroupIgnoreSelfCharts.size()];
                        DEFINE.DataQualityCode qualitys[]=new DEFINE.DataQualityCode[mSameGroupIgnoreSelfCharts.size()];
                        DEFINE.ErrorCode errorCodes[] = new DEFINE.ErrorCode[mSameGroupIgnoreSelfCharts.size()];
                        int index = 0;
                        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                            qualitys[index]=chart.GetTag().GetReadData().GetQualityCode();
                            errorCodes[index]=chart.GetTag().GetReadData().GetErrorCode();
                            values[index] = Double.valueOf(chart.GetTag().GetReadData().GetData().toDouble()).floatValue();
                            index++;
                        }
                        BarDataSet set = (BarDataSet) barData.getDataSetByIndex(0);
                        if (set != null) {
                            set.addEntry(new TagBarChartDataEntry(qualitys,errorCodes, GetTag().GetReadData().GetTimestamp(),
                                    barData.getEntryCount(), values));
                        }
                        if (IsVisibleChart())
                            barData.notifyDataChanged();
                        break;
                    }
                }

            }
            break;
            case PieChart:         // 饼状图
            {
                PieData pieData = (PieData) mChartData;
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    // 常规多点 + 最后一次数值
                    if (LastRealtimeData.IsType(chartDatasetType)) {
                        // 清空历史
                        for (int i = 0; i < pieData.getDataSetCount(); ++i)
                            pieData.getDataSetByIndex(i).clear();
                        // 获取列表
                        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                            break;
                        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                            pieData.addEntry(new TagPieChartDataEntry(chart.GetTag().GetReadData().GetQualityCode(),
                                    chart.GetTag().GetReadData().GetErrorCode(),chart.GetTag().GetReadData().GetTimestamp(),
                                    chart.GetTag().GetReadData().GetData().toDouble(), chart.GetTag().GetName()), 0);
                        }
                        if (IsVisibleChart())
                            pieData.notifyDataChanged();
                    }
                    break;
                }

            }
            break;

            default:
                break;
        }
    }

    // 构造函数
    protected TagChart(Tag _tag) {
        tagParent = _tag;
        //SetDefaultValue();

//        int[] COLORS = {
//                Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
//                Color.rgb(118, 174, 175), Color.rgb(42, 109, 130)
//                , Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
//                Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)
//                , Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
//                Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)
//                , Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
//                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
//                , Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
//                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
//        };
//        // 测试数值
//        SetAttri(1,(((testgroup) % 3)==0)? DEFINE.ChartType.LineChart.GetValue(): DEFINE.ChartType.BarChart.GetValue(), RealtimeDataset.GetValue(),
//                DEFINE.ChartStyle.StackMultipleTag.GetValue(),/* Color.RED, Color.GREEN*/COLORS[testgroup], COLORS[testgroup], "groupname" + testgroup,
//                "groupDescription" + testgroup, "groupComment" + testgroup, "Comment" + testgroup, "StyleDescription" + testgroup
//                ,   "Name" + testgroup, "Description" + testgroup);
//        testgroup++;
    }

    // public static int testgroup = 0;

    // 设置属性值
    private void SetAttri(int _group, int _type, int _datasettype, int _style, int _color1, int _color2, String _groupName,
                          String _groupDescription, String _groupComment,
                          String _strComment, String _styleDescription, String _name, String _description) {


        chartType = DEFINE.ChartType.GetTypeWithValue(_type);
        chartStyle = DEFINE.ChartStyle.GetStyleWithValue(_style);
        chartDatasetType = DEFINE.ChartDatasetType.GetTypeWithValue(_datasettype);

        strChartName = _name;
        strChartDescription = _description;
        strComment = _strComment;
        strStyleDescription = _styleDescription;
        colorKey1 = _color1;
        colorKey2 = _color2;

        nChartGroup = _group;
        strGroupName = _groupName;
        strGroupDescription = _groupDescription;
        strGroupComment = _groupComment;
    }

    public int GetMainColor() {
        return Color.argb(CHART_COLOR_ALPHA,colorKey1&0xff,colorKey1>>8&0xff,colorKey1>>16&0xff);
    }

    public int GetSubColor() {
        return Color.argb(CHART_COLOR_ALPHA,colorKey2&0xff,colorKey2>>8&0xff,colorKey2>>16&0xff);
    }

    private void CreateChartData() {
        // 根据不同分类更新数据
        switch (chartType) {
            case LineChart:        // 线形图
            {
                if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)) {
                    mChartData = new LineData(createBasicSingleLineSet());
                    //mChartData.setValueTextSize(19.0f);
                } else if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    mChartData = new LineData(createBasicMultipleLineSet());
                    //mChartData.setValueTextSize(19.0f);
                }
            }
            break;
            case BarChart:         // 柱形图
            {
                if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)) {
                    mChartData = new BarData(createBasicSingleBarSet());
                } else if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    mChartData = new BarData(createBasicMultipleBarSet());
                } else if (DEFINE.ChartStyle.StackMultipleTag.IsStyle(chartStyle)) {
                    mChartData = new BarData(createStackMultipleBarSet());
                }
            }
            break;
            case PieChart:         // 饼状图
            {
                // 多点饼状图
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    mChartData = new PieData(createPieSet());
                    mChartData.setValueTextColor(Color.DKGRAY);
                }

            }
            break;

            default:
                break;
        }
    }

    // 创建线形图数据集
    private LineDataSet createBasicSingleLineSet() {
        fValueTextSize = 10f;
        LineDataSet set = new LineDataSet(null, tagParent.GetName());
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(GetMainColor());
        set.setCircleColor(GetMainColor());
        set.setLineWidth(4f);
        set.setCircleRadius(12f);
        set.setFillAlpha(65);
        set.setFillColor(GetSubColor());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.DKGRAY);
        set.setValueTextSize(29f);
        set.setDrawValues(bDrawValue);
        set.setDrawFilled(bDrawFilled);
        set.setDrawCircles(bPointCircles);
        set.setMode(lineDatasetMode);
        return set;
    }

    // 创建线形图数据集
    private List<ILineDataSet> createBasicMultipleLineSet() {
        fValueTextSize = 9f;
        List<ILineDataSet> sets = new ArrayList<>();
        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
            return sets;
        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
            LineDataSet set = new LineDataSet(null, chart.GetTag().GetName());
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(chart.GetMainColor());
            set.setCircleColor(chart.GetMainColor());
            set.setLineWidth(4f);
            set.setCircleRadius(12f);
            set.setFillAlpha(65);
            set.setFillColor(chart.GetSubColor());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setValueTextColor(Color.DKGRAY);
            set.setValueTextSize(29f);
            set.setDrawValues(bDrawValue);
            set.setDrawFilled(bDrawFilled);
            set.setDrawCircles(bPointCircles);
            set.setMode(lineDatasetMode);
            sets.add(set);
        }
        return sets;
    }

    private BarDataSet createBasicSingleBarSet() {
        fValueTextSize = 10f;
        ArrayList<BarEntry> values = new ArrayList<>();
        BarDataSet set = new BarDataSet(values, tagParent.GetName());
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(GetMainColor());
        set.setDrawIcons(false);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.DKGRAY);
        set.setDrawValues(bDrawValue);
        set.setValueTextSize(29f);
        return set;
    }

    private List<IBarDataSet> createBasicMultipleBarSet() {
        fValueTextSize = 10f;
        List<IBarDataSet> sets = new ArrayList<>();
        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
            return sets;
        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
            ArrayList<BarEntry> values = new ArrayList<>();
            BarDataSet set = new BarDataSet(values, chart.GetTag().GetName());
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(chart.GetMainColor());
            set.setDrawIcons(false);
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setValueTextColor(Color.DKGRAY);
            set.setDrawValues(bDrawValue);
            set.setValueTextSize(29f);
            sets.add(set);
        }
        return sets;
    }

    // 柱形-栈型数据集
    private List<IBarDataSet> createStackMultipleBarSet() {
        fValueTextSize = 10f;
        List<IBarDataSet> sets = new ArrayList<>();
        if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
            return sets;
        int[] colors = new int[mSameGroupIgnoreSelfCharts.size()];
        String[] labels = new String[mSameGroupIgnoreSelfCharts.size()];
        float fValues[] = new float[mSameGroupIgnoreSelfCharts.size()];
        DEFINE.DataQualityCode qualitys[] = new DEFINE.DataQualityCode[mSameGroupIgnoreSelfCharts.size()];
        DEFINE.ErrorCode errorCodes[]= new DEFINE.ErrorCode[mSameGroupIgnoreSelfCharts.size()];
        int index = 0;
        for (TagChart chart : mSameGroupIgnoreSelfCharts) {
            qualitys[index]=DEFINE.DataQualityCode.NoQualityNoValue;
            errorCodes[index]=DEFINE.ErrorCode.None;
            colors[index] = chart.GetMainColor();
            labels[index] = chart.strChartName;
            index++;
        }
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new TagBarChartDataEntry(qualitys,errorCodes,0, 0, fValues));
        BarDataSet set = new BarDataSet(values, strGroupName);
        set.setColors(colors);
        set.setStackLabels(labels);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawIcons(false);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.DKGRAY);
        set.setDrawValues(bDrawValue);
        sets.add(set);
        return sets;
    }

    private PieDataSet createPieSet() {
        fValueTextSize = 12f;
        //ArrayList<PieEntry> entries = new ArrayList<>();
        PieDataSet set = new PieDataSet(null, strChartName);
        set.setDrawIcons(false);
        set.setSliceSpace(3f);
        set.setIconsOffset(new MPPointF(0, 40));
        set.setSelectionShift(5f);
        // 颜色信息
        ArrayList<Integer> colors = new ArrayList<>();
        if (mSameGroupIgnoreSelfCharts != null && mSameGroupIgnoreSelfCharts.size() > 0) {
            for (TagChart chart : mSameGroupIgnoreSelfCharts)
                colors.add(chart.GetMainColor());
        }
        //set.setValueTextSize(29f);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        set.setColors(colors);

        return set;
    }

    // 解析json获取属性(json在线工具http://www.bejson.com/ )
    // 例:{"g":0,"t":1,"dst":1,"s":1,"c1":123,"c2":320,"sd":"styleDescription","gn":"groupname","gd":"groupDescription","gc":"groupComment","xan":"XAxisName","yan":"YAxisName","c":"Comment","n":"Name","d":"Description"}
    public void SetValueFromJSON(String _json) {
        if (_json == null) return;
        JSONObject json = null;
        try {
            json = new JSONObject(_json);
            //None类型 无需提取参数设置
            if (json.has("t") && !DEFINE.ChartType.None.IsType(json.getInt("t")))
                if (json.has("t") && !DEFINE.ChartType.None.IsType(json.getInt("t")))
                    SetAttri(json.getInt("g"), json.getInt("t"), json.getInt("dst"),
                            json.getInt("s"), json.getInt("c1"),
                            json.getInt("c2"), json.getString("gn"), json.getString("gd"),
                            json.getString("gc"), json.getString("c"), json.getString("sd"),
                            json.getString("n"), json.getString("d")
                    );
                else chartType = DEFINE.ChartType.None;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 图表是否有效
    public boolean IsChartValid() {
        if (chartType.IsType(DEFINE.ChartType.None)) return false;
        return true;
    }

    public String GetName() {
        return strChartName;
    }

    public String GetDescription() {
        return strChartDescription;
    }

    // 图标左上角文字
    public String GetViewChartGroupName() {
        return strGroupName;
    }

    // 图表右上角文字
    public String GetViewChartGroupDescription() {
        return strGroupDescription;
    }

    // 图表中文字
    public String GetViewChartInnerDescription() {
        switch (chartType) {
            case LineChart:        // 线形图
            case BarChart:         // 柱形图
            case PieChart:         // 饼状图
            {
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)) {
                    return strComment;
                }
            }
            default:
                break;
        }
        return "";
    }

    protected void SetDefaultValue() {
    }

    public Tag GetTag() {
        return tagParent;
    }

    // 获取线形图数据
    public LineData GetLineData() {
        if (mChartData != null && mChartData instanceof LineData) return (LineData) mChartData;
        return null;
    }

    // 获取柱形图数据
    public BarData GetBarData() {
        if (mChartData != null && mChartData instanceof BarData) return (BarData) mChartData;
        return null;
    }

    // 获取饼状图数据
    public PieData GetPieData() {
        if (mChartData != null && mChartData instanceof PieData) return (PieData) mChartData;
        return null;
    }

    public void AscXRangeMaximum() {
        nChartVisibleXRangeMaximum++;
    }

    public void DescXRangeMaximum() {
        if (nChartVisibleXRangeMaximum > 2) nChartVisibleXRangeMaximum--;
    }

    // 切换tagdetail显示
    public boolean ToggleTagDetailVisible() {
        bTagDetailVisible = !bTagDetailVisible;
        return bTagDetailVisible;
    }

    // 切换图表显示
    public boolean ToggleChartVisible() {
        bChartVisible = !bChartVisible;
        boolean bVisible = IsVisibleChart();
        if (bVisible) mChartData.notifyDataChanged();
        return bVisible;
    }

    // 图表可见性
    public boolean IsVisibleChart() {
        // 直控隐藏
        if (!bChartVisible || chartType.IsType(DEFINE.ChartType.None) || mChartData == null)
            return false;
        // 按照过滤组合
        switch (chartType) {
            case LineChart:        // 线形图
            {
                // 支持 单点+实时数据
                if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset))
                    return true;
                // 支持 常规多点+实时数据
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset))
                    return true;
                // 支持 常规多点+最后一次数值
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.LastRealtimeData))
                    return true;
            }
            break;
            case BarChart:         // 柱形图
            {
                // 支持 单点+实时数据
                if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset))
                    return true;
                // 支持 常规多点+实时数据
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset))
                    return true;
                // 支持 常规多点+最后一次数值
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.LastRealtimeData))
                    return true;
                // 支持 栈型多点+实时数据
                if (DEFINE.ChartStyle.StackMultipleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset))
                    return true;
            }
            break;
            case PieChart:         // 饼状图
            {
                // 支持 常规多点+最后一次数值
                if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle)
                        && chartDatasetType.IsType(DEFINE.ChartDatasetType.LastRealtimeData))
                    return true;
            }
            default:
                break;
        }
        return false;
    }

    // 开关图表显示数值
    public boolean ToggleChartShowValue() {
        bDrawValue = !bDrawValue;
        return bDrawValue;
    }

    // 开关图表百分比
    public boolean ToggleChartPercent() {
        bPercentValue = !bPercentValue;
        // 栈型同时开启全栈
        if (bPercentValue && chartStyle.IsStyle(DEFINE.ChartStyle.StackMultipleTag)) {
            bDrawWholeStack = true;
        }
        return bPercentValue;
    }

    // 开关图表全栈显示
    public boolean ToggleWholeStackValue() {
        bDrawWholeStack = !bDrawWholeStack;
        // 栈型同时关闭百分比
        if (!bDrawWholeStack && chartStyle.IsStyle(DEFINE.ChartStyle.StackMultipleTag)) {
            bPercentValue = false;
        }

        return bDrawWholeStack;
    }

    // 开关图表填充
    public boolean ToggleChartFill() {
        bDrawFilled = !bDrawFilled;
        return bDrawFilled;
    }

    // 开关自动右移
    public boolean ToggleAutoMoveToRight() {
        bAutoMoveToRight = !bAutoMoveToRight;
        return bAutoMoveToRight;
    }

    // 切换绘制模式
    public void SwitchNextDrawMode() {
        if (lineDatasetMode == LineDataSet.Mode.CUBIC_BEZIER) {
            lineDatasetMode = LineDataSet.Mode.LINEAR;
        } else if (lineDatasetMode == LineDataSet.Mode.LINEAR) {
            lineDatasetMode = LineDataSet.Mode.HORIZONTAL_BEZIER;
        } else if (lineDatasetMode == LineDataSet.Mode.HORIZONTAL_BEZIER) {
            lineDatasetMode = LineDataSet.Mode.STEPPED;
        } else if (lineDatasetMode == LineDataSet.Mode.STEPPED) {
            lineDatasetMode = LineDataSet.Mode.CUBIC_BEZIER;
        }
    }
    /////////////////////////////////////////////////////////////////////
    // 配置相关


    // 获取Y轴最小值（根据量程变换信息）
    public float GetViewAxisMinumValue() {
        if (DEFINE.ChartStyle.BasicMultipleTag.IsStyle(chartStyle) ||
                DEFINE.ChartStyle.StackMultipleTag.IsStyle(chartStyle)) {
            // 同组多点取最小值
            float fMinValue = 0.0f;
            // 获取列表
            if (mSameGroupIgnoreSelfCharts == null || mSameGroupIgnoreSelfCharts.size() == 0)
                return 0f;
            for (TagChart chart : mSameGroupIgnoreSelfCharts) {
                float fGetValue = 0f;
                // 实时数据类型 根据量程变换信息确定最小值
                if (!tagParent.scalingType.IsType(DEFINE.ScalingType.None)) {
                    fGetValue = Double.valueOf(tagParent.dbScaledValueLow).floatValue();
                }
                fMinValue = Math.min(fMinValue, fGetValue);
            }
            return fMinValue;
        }
        if (DEFINE.ChartStyle.BasicSingleTag.IsStyle(chartStyle)) {
            // 实时数据类型 根据量程变换信息确定最小值
            if (tagParent.scalingType.IsType(DEFINE.ScalingType.None)) {
                return 0f;
            } else {
                return Double.valueOf(tagParent.dbScaledValueLow).floatValue();
            }
        }
        return 0f;
    }

    // 是否为同一组图表
    public boolean IsSameGroup(TagChart _chart, boolean _ignoreSelf) {
        if (_chart == null) return false;
        if (!_ignoreSelf && _chart == this) return true;
        if (_chart.nChartGroup == SINGLE_GROUP_ID || nChartGroup == SINGLE_GROUP_ID)
            return false;
        return (_chart.nChartGroup == nChartGroup);
    }


    // 配置加载完毕(此时channel，device，tag都已关联完毕)
    public void OnConfigLoadCompleted() {
        // 获取同组图表
        mSameGroupIgnoreSelfCharts = tagParent.GetDevice().GetTagsWithSameChartGroup(tagParent, false);
        // 创建图表数据
        CreateChartData();
    }

    public float GetValueTextSize() {
        return fValueTextSize;
    }

}
