package com.tyw.moniter.main.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tyw.moniter.main.R;

import java.util.ArrayList;
import java.util.List;

import moniter.tyw.com.moniterlibrary.common.Channel;

public class ChannelListAdapter extends BaseAdapter {
    // 数据源
    private List<ChannelListItem> mMenuItems = new ArrayList<ChannelListItem>();
    private Context context;

    public void AddChannel(Channel _channel) {
        ChannelListItem item = new ChannelListItem(_channel);
        mMenuItems.add(item);
        notifyDataSetChanged();
    }

    public void RemoveAllChannel() {
        mMenuItems.clear();
        notifyDataSetChanged();
    }

    // 类型
    public static class ChannelListItem {
        public ChannelListItem(Channel _channel) {
            bCheck = false;
            channel = _channel;
        }

        public boolean bCheck;
        public final Channel channel;
    }

    // 获取选中的channel
    public ChannelListItem GetCheckedChannel() {
        for (ChannelListItem item : mMenuItems) {
            if (item.bCheck) return item;
        }
        return null;
    }
    // 选择判断
    public boolean IsCheck(int pos)
    {
        if(pos>=0&&pos<mMenuItems.size()) return mMenuItems.get(pos).bCheck;
        return false;
    }
    // setcheck
    public void SetCheck(int pos) {
        for (ChannelListItem item : mMenuItems) {
            item.bCheck = false;
        }
        if (pos >= 0 && pos < mMenuItems.size()) {
            mMenuItems.get(pos).bCheck = true;
        }
        notifyDataSetChanged();
    }

    public ChannelListAdapter(Context _context) {
        context = _context;
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mMenuItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChannelListItem item = mMenuItems.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_channel, viewGroup, false);
        }
        if (item != null) {
            TextView textName = view.findViewById(R.id.text_channel_name);
            textName.setText(item.channel.GetName());
            View bgView = view.findViewById(R.id.frame_listitem_channel_bg);
            if (item.bCheck)
                bgView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_listitem_channel_bg_check));
            else
                bgView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_listitem_channel_bg_uncheck));
        }
        return view;
    }
}
