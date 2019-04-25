package com.tyw.moniter.main.ui.adapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.View.ExpandLinearLayout;
import com.tyw.moniter.main.ui.WifiFragment.OnListFragmentInteractionListener;
import com.tyw.moniter.main.ui.dummy.DummyContent.DummyItem;
import com.tyw.moniter.wifi.WifiDetail;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class WifiListViewAdapter extends RecyclerView.Adapter<WifiListViewAdapter.ViewHolder> {

    private   List<WifiDetail> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    // drawable
    private final Drawable drawableScanSign, drawableCustomConfigSign, drawableConfigSign;

    public WifiListViewAdapter(Context _context, List<WifiDetail> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = _context;
        // load drawable
        drawableScanSign = ContextCompat.getDrawable(mContext, R.drawable.icon_wifi_scan_sign);
        drawableCustomConfigSign = ContextCompat.getDrawable(mContext, R.drawable.icon_edit_wifi);
        drawableConfigSign = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_wifi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        WifiDetail detail = holder.mItem;
        holder.mTextKeyword.setText(detail.GetViewKeyword());
        if(detail.GetViewState().isEmpty())
        {
            holder.mTextState.setVisibility(View.GONE);
        }else{
            holder.mTextState.setVisibility(View.VISIBLE);
            holder.mTextState.setText(detail.GetViewState());
        }
        holder.mTextDetail.setText(detail.GetViewDetail());
        holder.mExpandDetail.SetExpand(detail.bExpandDetail, false, null);
        if (detail.bCustomConfig) {
            holder.mBtnSign.setImageDrawable(drawableCustomConfigSign);
        }else if (detail.mWifiScanResult != null) {
            holder.mBtnSign.setImageDrawable(drawableScanSign);
        }  else {
            holder.mBtnSign.setImageDrawable(drawableConfigSign);
        }
        holder.mBtnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mItem.bExpandDetail = !holder.mItem.bExpandDetail;
                notifyDataSetChanged();
            }
        });
        // holder.mBtnEdit= mView.findViewById(R.id.btn_wifi_listitem_edit); // btn_wifi_listitem_edit
        if(holder.mItem.bCustomConfig)
        {

            holder.mBtnSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onEditCustomConfig(holder.mItem);
                    }
                }
            });
        }else{
            holder.mBtnSign.setOnClickListener(null);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=mListener)
                {
                    mListener.onConnectConfig(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void RefreshWifi(List<WifiDetail> wifiDetails) {
        mValues = wifiDetails;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTextKeyword; //text_wifi_listitem_keyword
        public final TextView mTextState; //text_wifi_listitem_connect_state
        public final TextView mTextDetail; //text_wifi_listitem_detail
        public final ImageButton mBtnSign;//img_wifi_listitem_sign
        public final ImageButton mBtnDetail;//btn_wifi_listitem_info
        //public final ImageButton mBtnEdit; // btn_wifi_listitem_edit
        public final ExpandLinearLayout mExpandDetail; //expand_wifi_detail
        // data
        public WifiDetail mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTextKeyword = mView.findViewById(R.id.text_wifi_listitem_keyword); //
            mTextState = mView.findViewById(R.id.text_wifi_listitem_connect_state); //text_wifi_listitem_connect_state
            mTextDetail = mView.findViewById(R.id.text_wifi_listitem_detail); //text_wifi_listitem_detail
            mBtnSign = mView.findViewById(R.id.img_wifi_listitem_sign);//img_wifi_listitem_sign
            mBtnDetail = mView.findViewById(R.id.btn_wifi_listitem_info);//btn_wifi_listitem_info
            //mBtnEdit = mView.findViewById(R.id.btn_wifi_listitem_edit); // btn_wifi_listitem_edit
            mExpandDetail = mView.findViewById(R.id.expand_wifi_detail); //expand_wifi_detail
            mExpandDetail.SetExpand(false, false, null);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextDetail.getText() + "'";
        }
    }
}
