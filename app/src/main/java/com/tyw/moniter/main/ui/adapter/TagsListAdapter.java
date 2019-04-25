package com.tyw.moniter.main.ui.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import moniter.tyw.com.moniterlibrary.SDK.SDK;
import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.View.ChartMarkerView01;
import com.tyw.moniter.main.ui.View.ExpandLinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import moniter.tyw.com.moniterlibrary.Chart.ChartDataValueFormatter;
import moniter.tyw.com.moniterlibrary.Chart.GroupNameValueFormatter;
import moniter.tyw.com.moniterlibrary.Chart.PercentValueFormatter;
import moniter.tyw.com.moniterlibrary.Chart.TimestampValueFormatter;

import moniter.tyw.com.moniterlibrary.Utils.DebugLog;
import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.DataType;
import moniter.tyw.com.moniterlibrary.common.Device;
import moniter.tyw.com.moniterlibrary.common.Tag;
import moniter.tyw.com.moniterlibrary.common.TagChart;
import moniter.tyw.com.moniterlibrary.common.Variant;

public class TagsListAdapter extends RecyclerView.Adapter<TagsListAdapter.ViewHolder> {

    // context
    private Context mContext;
    // 数据列表
    private List<TagsListItem> listData = new ArrayList<>();
    // 默认无告警色
    private int mNoAlarmColor;
    // 图表缓存
    final Drawable drawableChartTypeLine, drawableChartTypePie, drawableChartTypeBar,
            drawableFillOn, drawableFillOff, drawableShowValueOn, drawableShowValueOff, drawablePercentOn, drawablePercentOff,
            drawableWholeStackValueOn, drawableWholeStackValueOff,
            drawableAutoMoveToRightOn, drawableAutoMoveToRightOff, drawableAscRang, drawableDescRang, drawableDrawamodeLinear, drawableDrawmodeCubic, drawableDrawmodeHcubic, drawableDrawableStep, drawableSaveToGallery;

    public void SetTags(List<Tag> _tags) {
        if (_tags == null) return;
        listData.clear();

        for (Tag tag : _tags) {
            TagsListItem item = new TagsListItem(tag);
            listData.add(item);
        }
        notifyDataSetChanged();
    }

    // 数据类
    public static class TagsListItem {
        public TagsListItem(Tag _tag) {
            mTag = _tag;
        }

        // 状态、数据
        //public boolean mIsExpandTagDetail;      // 展开状态
        //public boolean mIsExpandChart;          // 展开状态
        public Tag mTag;
    }

    public TagsListAdapter(Context _context) {
        mContext = _context;
        drawableChartTypeLine = ContextCompat.getDrawable(mContext, R.drawable.btn_charttype_line);
        drawableChartTypePie = ContextCompat.getDrawable(mContext, R.drawable.btn_charttype_pie);
        drawableChartTypeBar = ContextCompat.getDrawable(mContext, R.drawable.btn_charttype_bar);
        drawableFillOn = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_fill_on);
        drawableFillOff = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_fill_off);
        drawableShowValueOn = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_showvalue_on);
        drawableShowValueOff = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_showvalue_off);
        drawablePercentOn = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_percent_on);
        drawablePercentOff = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_percent_off);
        drawableWholeStackValueOn = ContextCompat.getDrawable(mContext, R.drawable.btn_chartsetwholestackvalue_on);
        drawableWholeStackValueOff = ContextCompat.getDrawable(mContext, R.drawable.btn_chartsetwholestackvalue_off);
        drawableAutoMoveToRightOn = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_automovetoright_on);
        drawableAutoMoveToRightOff = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_automovetoright_off);
        drawableAscRang = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_ascxrang);
        drawableDescRang = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_descxrang);
        drawableDrawamodeLinear = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_drawmode_linear);
        drawableDrawmodeCubic = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_drawmode_cubic);
        drawableDrawmodeHcubic = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_drawmode_horizontalcubic);
        drawableDrawableStep = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_drawmode_step);
        drawableSaveToGallery = ContextCompat.getDrawable(mContext, R.drawable.btn_chartset_savetogallery);
        mNoAlarmColor = ContextCompat.getColor(mContext, R.color.color_alarm_none);
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return super.getItemId(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_tags, parent, false);
        //DebugLog.d("CreateViewHolder call:" + (++nCreateViewHolder));
        return new ViewHolder(view);
    }

    int nCreateViewHolder = 0;
//
//    private int nCreateViewHolder = 0;
//    private int nBindViewHolder = 0;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //DebugLog.d("onBindViewHolder call:" );
        holder.mItem = listData.get(position);
        Tag tag = holder.mItem.mTag;
        if (tag == null) return;
        TagChart chart = holder.mItem.mTag.GetChart();
        holder.mTextTagName.setText(tag.GetViewKeyString());
        holder.mTextTagValue.setText(tag.GetViewValueString());
        holder.mTextTagValue.setTextColor(tag.GetReadData().GetQualityCode().GetColor());
        holder.mTextTagDetail.setText(tag.GetViewDetailString());
        // 点击数值事件
        if (tag.IsCanWrite()) {
            holder.mBtnTagWrite.setVisibility(View.VISIBLE);
            holder.mBtnTagWrite.setEnabled(true);
            holder.mBtnTagWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get device
                    final Device device = holder.mItem.mTag.GetDevice();

                    if (device == null) return;
                    // 包含的类型
                    StringBuilder strBuilder = new StringBuilder(holder.mItem.mTag.dataType.GetName());
                    if (!DEFINE.ScalingType.None.IsType(holder.mItem.mTag.scalingType))
                        strBuilder.append("->").append(holder.mItem.mTag.scaledValueType.GetName());
                    String includeTypeName = strBuilder.toString();
                    // new input view
                    final View inputView = LayoutInflater.from(mContext).inflate(R.layout.tagwrite_input_view, null);
                    final TextView textMsg = inputView.findViewById(R.id.text_tagwrite_inputview_msg);
                    final TextView textTitle = inputView.findViewById(R.id.text_tagwrite_inputview_title);
                    final EditText editInput = inputView.findViewById(R.id.edit_tagwrite_target_string);
                    final RadioButton radioOnlySrcValue = inputView.findViewById(R.id.radio_tagwrite_valuetype_OnlySrcValue);
                    final RadioButton radioOnlyScaledValue = inputView.findViewById(R.id.radio_tagwrite_valuetype_OnlyScaledValue);
                    final RadioButton radioAutoScaledOrSrcValue = inputView.findViewById(R.id.radio_tagwrite_valuetype_AutoScaledOrSrcValue);
                    final RadioButton radioOnlySrcRawHex = inputView.findViewById(R.id.radio_tagwrite_valuetype_OnlySrcRawHex);
                    // set radio text
                    radioOnlySrcValue.setText(DEFINE.WriteTagValueType.OnlySrcValue.GetName());
                    radioOnlyScaledValue.setText(DEFINE.WriteTagValueType.OnlyScaledValue.GetName());
                    radioAutoScaledOrSrcValue.setText(DEFINE.WriteTagValueType.AutoScaledOrSrcValue.GetName());
                    radioOnlySrcRawHex.setText(DEFINE.WriteTagValueType.OnlyRawHex.GetName());
                    textTitle.setText(mContext.getString(R.string.title_writetag_new_value));
                    textMsg.setText(mContext.getString(R.string.msg_writetag_new_value, holder.mItem.mTag.GetName(), holder.mItem.mTag.strAddress, includeTypeName));
                    // default check
                    if (!DEFINE.ScalingType.None.IsType(holder.mItem.mTag.scalingType))
                        radioAutoScaledOrSrcValue.setChecked(true);
                    else {
                        radioOnlyScaledValue.setEnabled(false);
                        radioOnlySrcValue.setChecked(true);
                    }
                    // dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(inputView).setCancelable(false)
                            .setPositiveButton(R.string.dowrite, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String strInput = editInput.getText().toString();
                                    if (!strInput.isEmpty()) {
                                        // get write type
                                        DEFINE.WriteTagValueType writeType = DEFINE.WriteTagValueType.Unknown;
                                        DataType datatype = DataType.UNDEFINED;
                                        if (radioOnlySrcValue.isChecked()) {
                                            writeType = DEFINE.WriteTagValueType.OnlySrcValue;
                                            datatype = holder.mItem.mTag.dataType;
                                        } else if (radioOnlyScaledValue.isChecked()) {
                                            writeType = DEFINE.WriteTagValueType.OnlyScaledValue;
                                            datatype = holder.mItem.mTag.scaledValueType;
                                        } else if (radioAutoScaledOrSrcValue.isChecked()) {
                                            writeType = DEFINE.WriteTagValueType.AutoScaledOrSrcValue;
                                            datatype = (DEFINE.ScalingType.None.IsType(holder.mItem.mTag.scalingType) ?
                                                    holder.mItem.mTag.dataType : holder.mItem.mTag.scaledValueType);
                                        } else if (radioOnlySrcRawHex.isChecked()) {
                                            writeType = DEFINE.WriteTagValueType.OnlyRawHex;
                                        }
                                        if (writeType.IsType(DEFINE.WriteTagValueType.Unknown) ||
                                                (!writeType.IsType(DEFINE.WriteTagValueType.OnlyRawHex)
                                                        && datatype.IsType(DataType.UNDEFINED)))
                                            return;
                                        // 调用sdk设置地址参数
                                        Variant variant = new Variant(datatype, 0, 1, 1);
                                        if (datatype.IsType(DataType.STRING))
                                        {
                                            variant.SetVariant(new Variant(datatype, holder.mItem.mTag.GetReadData().GetData().Size()
                                                    , 1, 1));
                                        }
                                        // 将字符串转为值
                                        boolean bValidString = false;
                                        // 设置值
                                        switch (writeType) {
                                            case AutoScaledOrSrcValue:
                                            case OnlySrcValue:
                                            case OnlyScaledValue: {
                                                bValidString = variant.SetDataWithString(0, editInput.getText().toString());
                                            }
                                            break;
                                            case OnlyRawHex: {
                                                bValidString = variant.SetDataRawWithHexString(editInput.getText().toString());
                                            }
                                            break;
                                            default:
                                                break;
                                        }

                                        if (!bValidString) {
                                            Toast.makeText(mContext, mContext.getString(R.string.hint_edittext_cannot_CastStringToValue)
                                                    , Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        // 提交写入请求
                                        boolean bRet = SDK.getInstance().WriteData(device.GetPtr(), holder.mItem.mTag.GetPtr()
                                                , writeType.GetValue(), variant);
                                        // success
                                        if (bRet) {
                                            Toast.makeText(mContext, mContext.getString(R.string.hint_writetag_WritePending)
                                                    , Toast.LENGTH_SHORT).show();

                                        } else {
                                            // failed reason
                                            Toast.makeText(mContext, mContext.getString(R.string.hint_writetag_CannotWirte)
                                                    , Toast.LENGTH_LONG).show();
                                        }

                                    }
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
        } else {
            holder.mBtnTagWrite.setVisibility(View.INVISIBLE);
            holder.mBtnTagWrite.setEnabled(false);
            holder.mBtnTagWrite.setOnClickListener(null);
        }
        // 告警颜色
        holder.mAlarmView.setBackgroundColor(tag.GetViewAlarmColor(mNoAlarmColor));
        // 折叠图表按钮图形
        switch (chart.chartType) {
            case LineChart:
                holder.mBtnToggleChart.setEnabled(true);
                holder.mBtnToggleChart.setImageDrawable(drawableChartTypeLine);
                break;
            case PieChart:
                holder.mBtnToggleChart.setEnabled(true);
                holder.mBtnToggleChart.setImageDrawable(drawableChartTypePie);
                break;
            case BarChart:
                holder.mBtnToggleChart.setEnabled(true);
                holder.mBtnToggleChart.setImageDrawable(drawableChartTypeBar);
                break;
            default:
                holder.mBtnToggleChart.setEnabled(false);
                holder.mBtnToggleChart.setImageDrawable(null);
        }
        // 折叠图表按钮事件
        if (!chart.chartType.IsType(DEFINE.ChartType.None))
            holder.mBtnToggleChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.mItem.mTag.GetChart().IsChartValid()) return;
                    boolean on = holder.mItem.mTag.GetChart().ToggleChartVisible();
                    holder.mExpandChart.SetExpand(on, false, null);
                    if (on && holder.mItem.mTag.GetChart().GetChartData() != null) {
                        holder.mItem.mTag.GetChart().GetChartData().notifyDataChanged();
                    }
                    notifyDataSetChanged();
                }
            });
        // 折叠tag详情按钮
        holder.mBtnToggleTagDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = holder.mItem.mTag.GetChart().ToggleTagDetailVisible();
                holder.mExpandTagDetail.SetExpand(on, false, null);
                notifyDataSetChanged();
            }
        });
        //
        boolean isChartVisible = chart.IsVisibleChart();
        if (isChartVisible) {
            OnChartRefreshView(holder, holder.mItem, position);

            // 图表功能按钮
            holder.mBtnDescXRangeMaximum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().DescXRangeMaximum();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnAscXRangeMaximum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().AscXRangeMaximum();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnShowValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.mItem.mTag.GetChart().ToggleChartShowValue();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnPercent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().ToggleChartPercent();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnWholeStackValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().ToggleWholeStackValue();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnFillChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().ToggleChartFill();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnAutoMoveToRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().ToggleAutoMoveToRight();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnDrawMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mItem.mTag.GetChart().SwitchNextDrawMode();
                    notifyDataSetChanged();
                }
            });
            holder.mBtnSaveToGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Chart chart = null;
                        switch (holder.mItem.mTag.GetChart().chartType) {
                            case LineChart: {
                                chart = holder.mLineChart;
                            }
                            break;
                            case BarChart: {
                                chart = holder.mBarChart;
                            }
                            break;
                            case PieChart: {
                                chart = holder.mPieChart;
                            }
                            break;

                            default:
                                break;
                        }

                        if (chart != null && chart.saveToGallery(holder.mItem.mTag.GetName() + "_" + System.currentTimeMillis(), 70))
                            Toast.makeText(mContext, mContext.getString(R.string.savetogallery_success),
                                    Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, mContext.getString(R.string.savetogallery_failed),
                                    Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.hint_nopermission_cannot_savetogallery), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            holder.mLineChart.setVisibility(View.GONE);
            holder.mBarChart.setVisibility(View.GONE);
            holder.mPieChart.setVisibility(View.GONE);

            holder.mBubbleChart.setVisibility(View.GONE);
            holder.mCandleStickChart.setVisibility(View.GONE);
            holder.mCombinedChart.setVisibility(View.GONE);
            holder.mHorizontalBarChart.setVisibility(View.GONE);

            holder.mRadarChart.setVisibility(View.GONE);
            holder.mScatterChart.setVisibility(View.GONE);
        }

        // 图表展开/折叠
        holder.mExpandTagDetail.SetExpand(chart.bTagDetailVisible, false, null);
        holder.mExpandChart.SetExpand(isChartVisible, false, null);

        // tagdetail 点击事件 修改地址
        holder.mTextTagDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get device
                final Device device = holder.mItem.mTag.GetDevice();
                if (device == null) return;
                // new address edittext
                final EditText editInput = new EditText(mContext);
                editInput.setText(holder.mItem.mTag.strAddress);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.title_editaddress_new_address))
                        .setView(editInput).setCancelable(false)
                        .setMessage(mContext.getString(R.string.msg_editaddress_new_address, holder.mItem.mTag.GetName()))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String strInput = editInput.getText().toString();
                                if (!strInput.isEmpty()) {
                                    // 调用sdk设置地址参数
                                    String strRet = SDK.getInstance().SetTagParam(device.GetPtr(), holder.mItem.mTag.GetPtr()
                                            , DEFINE.DynamicSetParamType.ResetTagAddress.GetValue(), strInput);
                                    // success
                                    if (DEFINE.strSetParamRetSuccess.equals(strRet)) {
                                        Toast.makeText(mContext, mContext.getString(R.string.msg_changeparam_success_with_key, DEFINE.DynamicSetParamType.ResetTagAddress.GetName()), Toast.LENGTH_SHORT).show();
                                        holder.mItem.mTag.strAddress = strInput;
                                        holder.mTextTagDetail.setText(holder.mItem.mTag.GetViewDetailString());
                                    } else {
                                        // failed reason
                                        Toast.makeText(mContext, strRet, Toast.LENGTH_LONG).show();
                                    }

                                }
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });

        // swappedByteIn16bitData
        // 事件
        holder.mCheckSwappedByteIn16BitData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                if (b == holder.mItem.mTag.bSwappedByteIn16BitData) return;
                final Device device = holder.mItem.mTag.GetDevice();
                if (device == null) return;
                String strMsg = b ? mContext.getString(R.string.msg_makesure_enable_control, holder.mItem.mTag.GetName(), DEFINE.DynamicSetParamType.SwappedByteIn16BitData.GetName())
                        : mContext.getString(R.string.msg_makesure_disable_control, holder.mItem.mTag.GetName(), DEFINE.DynamicSetParamType.SwappedByteIn16BitData.GetName());
                //dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(strMsg)
                        .setTitle(mContext.getString(R.string.title_changeparam_alert))
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                compoundButton.setChecked(!b);
                                dialogInterface.dismiss();
                            }
                        }).setCancelable(true)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                compoundButton.setChecked(!b);
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String strRet = SDK.getInstance().SetTagParam(device.GetPtr(), holder.mItem.mTag.GetPtr()
                                        , DEFINE.DynamicSetParamType.SwappedByteIn16BitData.GetValue(), b ? 1 : 0);
                                // success
                                if (DEFINE.strSetParamRetSuccess.equals(strRet)) {
                                    Toast.makeText(mContext, mContext.getString(R.string.msg_changeparam_success_with_key, DEFINE.DynamicSetParamType.SwappedByteIn16BitData.GetName()), Toast.LENGTH_LONG).show();
                                    // 更改值
                                    holder.mItem.mTag.bSwappedByteIn16BitData = b;
                                } else {
                                    Toast.makeText(mContext, strRet, Toast.LENGTH_LONG).show();
                                    compoundButton.setChecked(!b);
                                }
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        // 设置
        holder.mCheckSwappedByteIn16BitData.setChecked(holder.mItem.mTag.bSwappedByteIn16BitData);
    }


    // 更新ChartView

    /**
     * 前提是chart可见
     *
     * @param holder
     * @param mItem
     * @param position
     */
    private void OnChartRefreshView(ViewHolder holder, TagsListItem mItem, int position) {
        //DebugLog.d("OnChartRefreshView");
        if (mItem == null || holder == null) return;
        TagChart tagChart = mItem.mTag.GetChart();
        if (tagChart == null) return;
        holder.mTextChartGroupName.setText(tagChart.GetViewChartGroupName());
        holder.mTextChartGroupDescription.setText(tagChart.GetViewChartGroupDescription());
        holder.mTextChartGroupComment.setText(tagChart.strGroupComment);

        //////////////////////////////////////////////////////////////////////
        // ---------------------控制按钮图形---------------------------//
        //////////////////////////////////////////////////////////////////////
        switch (tagChart.chartType) {
            case LineChart:        // 线形图
            {
                // 启用相关控制
                holder.mBtnDescXRangeMaximum.setVisibility(View.VISIBLE);
                holder.mBtnAscXRangeMaximum.setVisibility(View.VISIBLE);
                holder.mBtnShowValue.setVisibility(View.VISIBLE);
                holder.mBtnShowValue.setImageDrawable(tagChart.bDrawValue ? drawableShowValueOn : drawableShowValueOff);
                holder.mBtnFillChart.setVisibility(View.VISIBLE);
                holder.mBtnFillChart.setImageDrawable(tagChart.bDrawFilled ? drawableFillOn : drawableFillOff);
                holder.mBtnAutoMoveToRight.setVisibility(View.VISIBLE);
                holder.mBtnAutoMoveToRight.setImageDrawable(tagChart.bAutoMoveToRight ? drawableAutoMoveToRightOn : drawableAutoMoveToRightOff);
                holder.mBtnDrawMode.setVisibility(View.VISIBLE);
                switch (tagChart.lineDatasetMode) {
                    case LINEAR:
                        holder.mBtnDrawMode.setImageDrawable(drawableDrawamodeLinear);
                        break;
                    case CUBIC_BEZIER:
                        holder.mBtnDrawMode.setImageDrawable(drawableDrawmodeCubic);
                        break;
                    case STEPPED:
                        holder.mBtnDrawMode.setImageDrawable(drawableDrawableStep);
                        break;
                    case HORIZONTAL_BEZIER:
                        holder.mBtnDrawMode.setImageDrawable(drawableDrawmodeHcubic);
                        break;
                }
                holder.mBtnSaveToGallery.setVisibility(View.VISIBLE);
                // 禁用
                holder.mBtnPercent.setVisibility(View.GONE);
                holder.mBtnWholeStackValue.setVisibility(View.GONE);
            }
            break;
            case BarChart:         // 柱形图
            {
                // 启用相关控制
                holder.mBtnDescXRangeMaximum.setVisibility(View.VISIBLE);
                holder.mBtnAscXRangeMaximum.setVisibility(View.VISIBLE);
                holder.mBtnShowValue.setVisibility(View.VISIBLE);
                holder.mBtnShowValue.setImageDrawable(tagChart.bDrawValue ? drawableShowValueOn : drawableShowValueOff);
                holder.mBtnAutoMoveToRight.setVisibility(View.VISIBLE);
                holder.mBtnAutoMoveToRight.setImageDrawable(tagChart.bAutoMoveToRight ? drawableAutoMoveToRightOn : drawableAutoMoveToRightOff);
                holder.mBtnSaveToGallery.setVisibility(View.VISIBLE);
                // 栈型可控百分比
                if (tagChart.chartStyle.IsStyle(DEFINE.ChartStyle.StackMultipleTag)) {
                    holder.mBtnPercent.setVisibility(View.VISIBLE);
                    holder.mBtnPercent.setImageDrawable(tagChart.bPercentValue ? drawablePercentOn : drawablePercentOff);
                    holder.mBtnWholeStackValue.setVisibility(View.VISIBLE);
                    holder.mBtnWholeStackValue.setImageDrawable(tagChart.bDrawWholeStack ? drawableWholeStackValueOff : drawableWholeStackValueOn);
                } else {
                    holder.mBtnPercent.setVisibility(View.GONE);
                    holder.mBtnWholeStackValue.setVisibility(View.GONE);
                }
                holder.mBtnFillChart.setVisibility(View.GONE);
                holder.mBtnDrawMode.setVisibility(View.GONE);

            }
            break;
            case PieChart:         // 饼状图
            {
                // 启用相关控制
                holder.mBtnShowValue.setVisibility(View.VISIBLE);
                holder.mBtnShowValue.setImageDrawable(tagChart.bDrawValue ? drawableShowValueOn : drawableShowValueOff);
                holder.mBtnPercent.setVisibility(View.VISIBLE);
                holder.mBtnPercent.setImageDrawable(tagChart.bPercentValue ? drawablePercentOn : drawablePercentOff);
                holder.mBtnSaveToGallery.setVisibility(View.VISIBLE);
                // 隐藏
                holder.mBtnDescXRangeMaximum.setVisibility(View.GONE);
                holder.mBtnAscXRangeMaximum.setVisibility(View.GONE);
                holder.mBtnWholeStackValue.setVisibility(View.GONE);
                holder.mBtnFillChart.setVisibility(View.GONE);
                holder.mBtnAutoMoveToRight.setVisibility(View.GONE);
                holder.mBtnDrawMode.setVisibility(View.GONE);
            }
            break;

            default: {
                // 隐藏所有控制
                holder.mBtnDescXRangeMaximum.setVisibility(View.GONE);
                holder.mBtnAscXRangeMaximum.setVisibility(View.GONE);
                holder.mBtnShowValue.setVisibility(View.GONE);
                holder.mBtnPercent.setVisibility(View.GONE);
                holder.mBtnWholeStackValue.setVisibility(View.GONE);
                holder.mBtnFillChart.setVisibility(View.GONE);
                holder.mBtnAutoMoveToRight.setVisibility(View.GONE);
                holder.mBtnDrawMode.setVisibility(View.GONE);
                holder.mBtnSaveToGallery.setVisibility(View.GONE);
            }
            break;
        }

        //////////////////////////////////////////////////////////////////////
        // ---------------------线形图---------------------------//
        //////////////////////////////////////////////////////////////////////
        LineData lineData = tagChart.GetLineData();
        if (tagChart.chartType.IsType(DEFINE.ChartType.LineChart) && lineData != null) {
            holder.mLineChart.setVisibility(View.VISIBLE);
            float fAxisMinimum = tagChart.GetViewAxisMinumValue();
            // apply styling
            // holder.chart.setValueTypeface(mTf);
            holder.mLineChart.getDescription().setEnabled(true);
            holder.mLineChart.getDescription().setText(tagChart.GetViewChartInnerDescription());
            holder.mLineChart.setDrawGridBackground(false);

            // 图例
            Legend l = holder.mLineChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setForm(Legend.LegendForm.SQUARE);
            l.setFormSize(9f);
            l.setTextSize(11f);
            l.setXEntrySpace(4f);
            l.setWordWrapEnabled(true);
            //X轴
            XAxis xAxis = holder.mLineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);

            // 横轴显示时间戳
            if (tagChart.chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset)) {
                TimestampValueFormatter tvf = null;
                ValueFormatter vf = holder.mLineChart.getXAxis().getValueFormatter();
                if (vf == null || !(vf instanceof TimestampValueFormatter)) {
                    tvf = new TimestampValueFormatter(holder.mItem.mTag);
                    holder.mLineChart.getXAxis().setValueFormatter(tvf);
                } else
                    tvf = (TimestampValueFormatter) vf;
                // 更新数据源
                tvf.SetTag(holder.mItem.mTag);
            } else if (tagChart.chartDatasetType.IsType(DEFINE.ChartDatasetType.LastRealtimeData)) {
                // 横轴显示组/块名
                GroupNameValueFormatter gnvf = null;
                ValueFormatter vf = holder.mLineChart.getXAxis().getValueFormatter();
                if (vf == null || !(vf instanceof GroupNameValueFormatter)) {
                    gnvf = new GroupNameValueFormatter(holder.mItem.mTag);
                    xAxis.setValueFormatter(gnvf);
                } else
                    gnvf = (GroupNameValueFormatter) vf;
                gnvf.SetTag(holder.mItem.mTag);
            }

            YAxis leftAxis = holder.mLineChart.getAxisLeft();
            //leftAxis.setTypeface(mTf);
            leftAxis.setLabelCount(tagChart.nYAxisLabelCount, false);
            leftAxis.setAxisMinimum(fAxisMinimum); // this replaces setStartAtZero(true)

            YAxis rightAxis = holder.mLineChart.getAxisRight();
            //rightAxis.setTypeface(mTf);
            rightAxis.setLabelCount(tagChart.nYAxisLabelCount, false);
            rightAxis.setDrawGridLines(false);
            rightAxis.setAxisMinimum(fAxisMinimum); // this replaces setStartAtZero(true)

            // set data
            if (holder.mLineChart.getData() != lineData)
                holder.mLineChart.setData(lineData);
            // 绘制设置
            lineData.setValueTextSize(tagChart.GetValueTextSize());
            List<ILineDataSet> sets = lineData.getDataSets();
            lineData.setHighlightEnabled(tagChart.bHighlight);
            holder.mLineChart.setPinchZoom(tagChart.bPinchZoom);
            holder.mLineChart.setAutoScaleMinMaxEnabled(tagChart.bAutoScale);
            holder.mLineChart.setDrawMarkers(tagChart.bDrawMarker);
            if (sets != null) {
                for (ILineDataSet iSet : sets) {
                    LineDataSet lineSet = (LineDataSet) iSet;
                    if (lineSet == null) continue;
                    lineSet.setDrawValues(tagChart.bDrawValue);
                    lineSet.setDrawFilled(tagChart.bDrawFilled);
                    lineSet.setDrawCircles(tagChart.bPointCircles);
                    lineSet.setMode(tagChart.lineDatasetMode);
                }
            }
            // markerview
            if (tagChart.bDrawMarker) {
                ChartMarkerView01 cmv = null;
                IMarker im = holder.mLineChart.getMarker();
                if (im == null || !(im instanceof ChartMarkerView01)) {
                    cmv = new ChartMarkerView01(tagChart, mContext, R.layout.chart_marker_view);
                    cmv.setChartView(holder.mLineChart);
                    holder.mLineChart.setMarker(cmv);
                } else
                    cmv = (ChartMarkerView01) holder.mLineChart.getMarker();
                // 更新数据源
                cmv.UpdateChart(tagChart);
            }

            if (lineData.getEntryCount() >= tagChart.nChartVisibleXRangeMaximum)
                holder.mLineChart.setVisibleXRangeMaximum(tagChart.nChartVisibleXRangeMaximum);
            // move to the latest entry
            if (tagChart.bAutoMoveToRight)
                holder.mLineChart.moveViewToX(lineData.getEntryCount());
            holder.mLineChart.notifyDataSetChanged();
        } else {
            holder.mLineChart.setVisibility(View.GONE);
        }
        //////////////////////////////////////////////////////////////////////
        // ---------------------柱形图---------------------------//
        //////////////////////////////////////////////////////////////////////
        BarData barData = tagChart.GetBarData();
        if (tagChart.chartType.IsType(DEFINE.ChartType.BarChart) && barData != null) {
            float fAxisMinimum = tagChart.GetViewAxisMinumValue();

            holder.mBarChart.setVisibility(View.VISIBLE);
            holder.mBarChart.setDrawBarShadow(false);
            // 栈型数值在下
            if (tagChart.chartStyle.IsStyle(DEFINE.ChartStyle.StackMultipleTag)) {
                holder.mBarChart.setDrawValueAboveBar(false);
            } else {
                holder.mBarChart.setDrawValueAboveBar(true);
            }
            holder.mBarChart.getDescription().setEnabled(false);
            holder.mBarChart.getDescription().setText(tagChart.GetViewChartInnerDescription());
            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            //holder.mBarChart.setMaxVisibleValueCount(60);
            holder.mBarChart.setDrawGridBackground(false);
            // chart.setDrawYLabels(false);
            XAxis xAxis = holder.mBarChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(5, false);
            // 实时数据
            if (tagChart.chartDatasetType.IsType(DEFINE.ChartDatasetType.RealtimeDataset)) {
                // 横轴显示时间戳
                TimestampValueFormatter tvf = null;
                ValueFormatter vf = holder.mBarChart.getXAxis().getValueFormatter();
                if (vf == null || !(vf instanceof TimestampValueFormatter)) {
                    tvf = new TimestampValueFormatter(holder.mItem.mTag);
                    xAxis.setValueFormatter(tvf);
                } else
                    tvf = (TimestampValueFormatter) vf;
                // 更新数据源
                tvf.SetTag(holder.mItem.mTag);
            } else if (tagChart.chartDatasetType.IsType(DEFINE.ChartDatasetType.LastRealtimeData)) {
                // 横轴显示组/块名
                GroupNameValueFormatter gnvf = null;
                ValueFormatter vf = holder.mBarChart.getXAxis().getValueFormatter();
                if (vf == null || !(vf instanceof GroupNameValueFormatter)) {
                    gnvf = new GroupNameValueFormatter(holder.mItem.mTag);
                    xAxis.setValueFormatter(gnvf);
                } else
                    gnvf = (GroupNameValueFormatter) vf;
                // 更新数据源
                gnvf.SetTag(holder.mItem.mTag);
            }
            YAxis leftAxis = holder.mBarChart.getAxisLeft();
            leftAxis.setLabelCount(8, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(fAxisMinimum); // this replaces setStartAtZero(true)

            YAxis rightAxis = holder.mBarChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(fAxisMinimum); // this replaces setStartAtZero(true)
            // 图例
            Legend l = holder.mBarChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setForm(Legend.LegendForm.SQUARE);
            l.setFormSize(9f);
            l.setTextSize(11f);
            l.setXEntrySpace(4f);
            l.setWordWrapEnabled(true);
            // set data
            if (holder.mBarChart.getData() != barData) {
                holder.mBarChart.setData(barData);
            }
            // 绘制设置 barData
            barData.setHighlightEnabled(tagChart.bHighlight);
            barData.setValueTextSize(tagChart.GetValueTextSize());
            holder.mBarChart.setPinchZoom(tagChart.bPinchZoom);
            holder.mBarChart.setAutoScaleMinMaxEnabled(tagChart.bAutoScale);
            holder.mBarChart.setDrawMarkers(tagChart.bDrawMarker);
            barData.setDrawValues(tagChart.bDrawValue);
            // 数据显示格式
            BarDataSet dataset = (BarDataSet) barData.getDataSetByIndex(0);
            if (dataset != null) {
                ChartDataValueFormatter cvf = null;
                ValueFormatter vf = dataset.getValueFormatter();
                if (vf == null || !(vf instanceof ChartDataValueFormatter)) {
                    cvf = new ChartDataValueFormatter(holder.mItem.mTag);
                    barData.setValueFormatter(cvf);
                } else
                    cvf = (ChartDataValueFormatter) vf;
                cvf.SetTag(holder.mItem.mTag);
            }

            // markerview
            if (tagChart.bDrawMarker) {
                IMarker im = holder.mBarChart.getMarker();
                ChartMarkerView01 cmv = null;
                if (im == null || !(im instanceof ChartMarkerView01)) {
                    cmv = new ChartMarkerView01(tagChart, mContext, R.layout.chart_marker_view);
                    cmv.setChartView(holder.mBarChart);
                    holder.mBarChart.setMarker(cmv);
                } else
                    cmv = (ChartMarkerView01) holder.mBarChart.getMarker();
                // 更新数据源
                cmv.UpdateChart(tagChart);
            }
            if (barData.getEntryCount() >= tagChart.nChartVisibleXRangeMaximum)
                holder.mBarChart.setVisibleXRangeMaximum(tagChart.nChartVisibleXRangeMaximum);
            // move to the latest entry
            if (tagChart.bAutoMoveToRight)
                holder.mBarChart.moveViewToX(barData.getEntryCount());
            holder.mBarChart.notifyDataSetChanged();

        } else

        {
            holder.mBarChart.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////////////////////////
        // ---------------------饼状图---------------------------//
        //////////////////////////////////////////////////////////////////////
        PieData pieData = tagChart.GetPieData();
        if (tagChart.chartType.IsType(DEFINE.ChartType.PieChart) && pieData != null)

        {
            DebugLog.d("饼状图refresh");
            float fAxisMinimum = tagChart.GetViewAxisMinumValue();

            holder.mPieChart.setVisibility(View.VISIBLE);
            holder.mPieChart.getDescription().setEnabled(false);
            //holder.mPieChart.getDescription().setText(tagChart.GetViewChartInnerDescription());
            holder.mPieChart.setExtraOffsets(5, 10, 5, 5);
            holder.mPieChart.setDragDecelerationFrictionCoef(0.95f);
            holder.mPieChart.setCenterText(tagChart.GetViewChartInnerDescription());
            holder.mPieChart.setDrawHoleEnabled(true);
            holder.mPieChart.setHoleColor(Color.WHITE);
            holder.mPieChart.setTransparentCircleColor(Color.WHITE);
            holder.mPieChart.setTransparentCircleAlpha(110);
            holder.mPieChart.setHoleRadius(58f);
            holder.mPieChart.setTransparentCircleRadius(61f);
            holder.mPieChart.setDrawCenterText(true);
            // holder.mPieChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            holder.mPieChart.setRotationEnabled(true);
            holder.mPieChart.setHighlightPerTapEnabled(true);
            // 图例
            Legend l = holder.mPieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setWordWrapEnabled(true);
            // entry label styling
            holder.mPieChart.setDrawEntryLabels(true);
            holder.mPieChart.setEntryLabelColor(Color.rgb(135, 147, 137));
            holder.mPieChart.setEntryLabelTextSize(12f);
            // set data
            if (holder.mPieChart.getData() != pieData)
                holder.mPieChart.setData(pieData);
            //percent value
            PercentValueFormatter pvf = null;
            ValueFormatter vf = pieData.getDataSet().getValueFormatter();
            if (vf == null || !(vf instanceof PercentValueFormatter)) {
                pvf = new PercentValueFormatter(holder.mPieChart);
                pieData.setValueFormatter(pvf);
            } else
                pvf = (PercentValueFormatter) vf;
            // 更新数据源
            pvf.SetChart(holder.mPieChart);

            // 绘制设置 pieData
            pieData.setHighlightEnabled(tagChart.bHighlight);
            pieData.setValueTextSize(tagChart.GetValueTextSize());
            holder.mPieChart.setDrawMarkers(tagChart.bDrawMarker);
            holder.mPieChart.setUsePercentValues(tagChart.bPercentValue);
            pieData.setDrawValues(tagChart.bDrawValue);
            // markerview
            if (tagChart.bDrawMarker) {
                ChartMarkerView01 cmv = null;
                IMarker im = holder.mPieChart.getMarker();
                if (im == null || !(im instanceof ChartMarkerView01)) {
                    cmv = new ChartMarkerView01(tagChart, mContext, R.layout.chart_marker_view);
                    cmv.setChartView(holder.mPieChart);
                    holder.mPieChart.setMarker(cmv);
                } else
                    cmv = (ChartMarkerView01) holder.mPieChart.getMarker();
                // 更新数据源
                cmv.UpdateChart(tagChart);
            }
            pieData.notifyDataChanged();
            holder.mPieChart.notifyDataSetChanged();

        } else

        {
            holder.mPieChart.setVisibility(View.GONE);
        }

//        // 展开/隐藏
//        holder.mExpandChart.SetExpand(tagChart.bChartVisible, false, null);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 数据
        public TagsListItem mItem;
        // 控件变量
        public final View mView;
        public final View mAlarmView;
        public final ImageButton mBtnToggleChart;  //btn_tag_chart
        public final ImageButton mBtnTagWrite; //btn_tag_write
        public final View mBtnToggleTagDetail; //frame_tag_toggle_detail
        public final TextView mTextTagName;//text_tag_name
        public final TextView mTextTagValue;//text_tag_value
        public final TextView mTextTagDetail;//text_tag_detail
        public final TextView mTextChartGroupName;//text_chart_name
        public final TextView mTextChartGroupDescription;//text_chart_descrtption
        public final TextView mTextChartGroupComment;//text_chart_descrtption
        public final CheckBox mCheckSwappedByteIn16BitData; //


        // 图表控件
        public final ExpandLinearLayout mExpandTagDetail;// expand_tagdetail
        public final ExpandLinearLayout mExpandChart;// expand_chat
        public final BarChart mBarChart; // chart_bar
        public final BubbleChart mBubbleChart; //chart_bubble
        public final CandleStickChart mCandleStickChart; // chart_candle_stick
        public final CombinedChart mCombinedChart; //chart_combine
        public final HorizontalBarChart mHorizontalBarChart; //chart_horizontal_bar
        public final LineChart mLineChart; //chart_line
        public final PieChart mPieChart; //chart_pie
        public final RadarChart mRadarChart; //chart_radar
        public final ScatterChart mScatterChart; //chart_scatter
        // 设置图表控件按钮
        public final ImageButton mBtnDescXRangeMaximum;
        public final ImageButton mBtnAscXRangeMaximum;
        public final ImageButton mBtnShowValue;
        public final ImageButton mBtnPercent;
        public final ImageButton mBtnWholeStackValue;
        public final ImageButton mBtnFillChart;
        public final ImageButton mBtnAutoMoveToRight;
        public final ImageButton mBtnDrawMode;
        public final ImageButton mBtnSaveToGallery;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAlarmView = view.findViewById(R.id.frame_alarm_color);
            // chart control button
            mBtnDescXRangeMaximum = view.findViewById(R.id.btn_tagchart_set_desc_XRangeMaximum);
            mBtnAscXRangeMaximum = view.findViewById(R.id.btn_tagchart_set_asc_XRangeMaximum);
            mBtnShowValue = view.findViewById(R.id.btn_tagchart_set_ShowValue);
            mBtnPercent = view.findViewById(R.id.btn_tagchart_set_percent);
            mBtnWholeStackValue = view.findViewById(R.id.btn_tagchart_set_wholeStackValue);
            mBtnFillChart = view.findViewById(R.id.btn_tagchart_set_Fill);
            mBtnAutoMoveToRight = view.findViewById(R.id.btn_tagchart_set_AutoMoveToRight);
            mBtnDrawMode = view.findViewById(R.id.btn_tagchart_set_DrawMode);
            mBtnSaveToGallery = view.findViewById(R.id.btn_tagchart_set_SaveToGallery);
            //
            mBtnTagWrite = view.findViewById(R.id.btn_tag_write);  //btn_tag_write
            mBtnToggleChart = view.findViewById(R.id.btn_tag_chart);  //btn_tag_chart
            mBtnToggleTagDetail = view.findViewById(R.id.frame_tag_toggle_detail);  //frame_tag_toggle_detail
            mTextTagName = view.findViewById(R.id.text_tag_name);//text_tag_name
            mTextTagValue = view.findViewById(R.id.text_tag_value);//text_tag_value
            mTextTagDetail = view.findViewById(R.id.text_tag_detail);//text_tag_detail
            mCheckSwappedByteIn16BitData = view.findViewById(R.id.checkbox_tagitem_swappedByteIn16BitData);
            mTextChartGroupName = view.findViewById(R.id.text_chart_groupname);//text_chart_name
            mTextChartGroupDescription = view.findViewById(R.id.text_chart_groupdescrtption);//text_chart_descrtption
            mTextChartGroupComment = view.findViewById(R.id.text_chart_groupcomment);//text_chart_descrtption
            mExpandTagDetail = view.findViewById(R.id.expand_tagdetail);// expand_tagdetail
            mExpandChart = view.findViewById(R.id.expand_chat);// expand_chat
            mBarChart = view.findViewById(R.id.chart_bar); // chart_bar
            mBubbleChart = view.findViewById(R.id.chart_bubble); //chart_bubble
            mCandleStickChart = view.findViewById(R.id.chart_candle_stick); // chart_candle_stick
            mCombinedChart = view.findViewById(R.id.chart_combine); //chart_combine
            mHorizontalBarChart = view.findViewById(R.id.chart_horizontal_bar); //chart_horizontal_bar
            mLineChart = view.findViewById(R.id.chart_line); //chart_line
            mPieChart = view.findViewById(R.id.chart_pie); //chart_pie
            mRadarChart = view.findViewById(R.id.chart_radar); //chart_radar
            mScatterChart = view.findViewById(R.id.chart_scatter); //chart_scatter

            mExpandChart.SetExpand(false, false, null);
            mExpandTagDetail.SetExpand(false, false, null);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextTagName.getText() + "'";
        }

    }
}
