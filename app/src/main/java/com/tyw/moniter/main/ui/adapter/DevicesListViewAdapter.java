package com.tyw.moniter.main.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tyw.moniter.common.Interfaces;
import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.DeviceLocationMapActivity;

import java.util.ArrayList;
import java.util.List;

import moniter.tyw.com.moniterlibrary.common.DEFINE;
import moniter.tyw.com.moniterlibrary.common.Device;

public class DevicesListViewAdapter extends RecyclerView.Adapter<DevicesListViewAdapter.ViewHolder> {

    // 列表数据源
    private List<DevicesListItem> listData = new ArrayList<DevicesListItem>();
    // 回调接口
    private final Interfaces.OnListDevicesListener mListener;
    // 图片资源
    private final Drawable drawableRun,drawablePause;

    // 资源上下文
    Context mContext;

    // 数据源类型
    public static class DevicesListItem {
        public DevicesListItem(Device _device) {
            mDevice = _device;
        }

        public boolean bCheck = false;
        public final Device mDevice;
    }

    public Device GetCheckDevice() {
        for (DevicesListItem item : listData) {
            if (item.bCheck) return item.mDevice;
        }
        return null;
    }

    // 选择判断
    public boolean IsCheck(int pos) {
        if (pos >= 0 && pos < listData.size()) return listData.get(pos).bCheck;
        return false;
    }

    public void SetCheck(int pos) {
        for (DevicesListItem item : listData) {
            item.bCheck = false;
        }
        if (pos >= 0 && pos < listData.size()) listData.get(pos).bCheck = true;
        notifyDataSetChanged();
    }

    public DevicesListViewAdapter(Context _context, Interfaces.OnListDevicesListener listener) {
        mListener = listener;
        mContext = _context;
        drawableRun = ContextCompat.getDrawable(mContext, R.drawable.btn_run);
        drawablePause = ContextCompat.getDrawable(mContext, R.drawable.btn_pause);
    }

    public void AddDevice(Device _device, boolean _notifyChanged) {
        DevicesListItem item = new DevicesListItem(_device);
        listData.add(item);
        if (_notifyChanged)
            notifyDataSetChanged();
    }

    public void RemoveAllDevices() {
        listData.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_devices, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = listData.get(position);
        holder.mTextView.setText(holder.mItem.mDevice.GetName());
        if (holder.mItem.bCheck) {
            holder.mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_listitem_device_text_check));
            holder.mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            //holder.mView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_listitem_device_bg_check));
        } else {
            holder.mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_listitem_device_text_uncheck));
            holder.mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            //holder.mView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_listitem_device_bg_uncheck));
        }
        holder.mView.setBackgroundColor(holder.mItem.mDevice.getDeviceState().GetColor());
        if(holder.mItem.mDevice.GetTagCount()>0)
        {
            holder.mBtnRunOrStop.setEnabled(true);
            holder.mBtnRunOrStop.setVisibility(View.VISIBLE);
        }else{
            holder.mBtnRunOrStop.setEnabled(false);
            holder.mBtnRunOrStop.setVisibility(View.INVISIBLE);
        }
        if(holder.mItem.mDevice.getDeviceState().IsState(DEFINE.DeviceState.Undefined)
                || holder.mItem.mDevice.getDeviceState().IsState(DEFINE.DeviceState.Disconnect)
                || holder.mItem.mDevice.getDeviceState().IsState(DEFINE.DeviceState.BeConnectedFailed)
                || holder.mItem.mDevice.getDeviceState().IsState(DEFINE.DeviceState.ConnectFailed)
                || holder.mItem.mDevice.getDeviceState().IsState(DEFINE.DeviceState.ListenerFailed))
        {
            holder.mBtnRunOrStop.setImageDrawable(drawableRun);
        }else{
            holder.mBtnRunOrStop.setImageDrawable(drawablePause);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    for (DevicesListItem item : listData) {
                        item.bCheck = false;
                    }
                    holder.mItem.bCheck = true;
                    notifyDataSetChanged();
                    mListener.onClickListDeviceItem(holder.mItem);
                }
            }
        });

        holder.mBtnRunOrStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    notifyDataSetChanged();
                    mListener.onClickListDeviceItemRunOrStop(holder.mItem);
                }
            }
        });
        holder.mBtnDeviceLocationMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,DeviceLocationMapActivity.class);
                intent.putExtra("device",  holder.mItem.mDevice.GetName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTextView;
        public final View mBackgroundView;
        public final ImageButton mBtnRunOrStop;
        public final View mBtnDeviceLocationMap;
        public DevicesListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView = (TextView) view.findViewById(R.id.listitem_devices_title);
            mBackgroundView = (View) view.findViewById(R.id.frame_listitem_devices);
            mBtnRunOrStop = view.findViewById(R.id.btn_device_run_stop);
            mBtnDeviceLocationMap = view.findViewById(R.id.btn_device_location_map);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText() + "'";
        }
    }
}
