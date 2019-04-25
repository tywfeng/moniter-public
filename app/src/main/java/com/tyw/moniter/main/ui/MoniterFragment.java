package com.tyw.moniter.main.ui;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tyw.moniter.common.Interfaces;
import com.tyw.moniter.common.Options;
import com.tyw.moniter.main.R;
import com.tyw.moniter.main.ui.adapter.ChannelListAdapter;
import com.tyw.moniter.main.ui.adapter.DevicesListViewAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.Device;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link MoniterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoniterFragment extends Fragment implements Interfaces.OnListDevicesListener, Interfaces.OnMoniterDataChangedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // 数据模型
    MoniterDataViewModel mMoniterData;
    // 标题文字
    TextView mTextMoniterTitle;
    // DrawerLayout侧滑组件
    DrawerLayout mDrawerLayout;
    // 侧滑菜单列表
    ListView mSilderChannelList;
    ChannelListAdapter mChannelAdapter;
    // 设备列表适配器
    DevicesListViewAdapter mDevicesAdapter;
    RecyclerView mDevicesRecyclerView;
    // TagFragment列表
    private List<TagsFragment> mListFragmentTags = new ArrayList<TagsFragment>();
    // 提示控件
    private TextView mTextMoniterDevicesHint;
    //线程UI同步
    Handler mHandler = new Handler();
    public MoniterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoniterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoniterFragment newInstance(String param1, String param2) {
        MoniterFragment fragment = new MoniterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_moniter, container, false);
        View viewFragment = inflater.inflate(R.layout.fragment_moniter, container, false);
        // list
        mDevicesRecyclerView = viewFragment.findViewById(R.id.list_devices);
        // 侧滑列表
        mSilderChannelList = viewFragment.findViewById(R.id.list_channel_side);
        mTextMoniterTitle = viewFragment.findViewById(R.id.text_moniter_title);
        mDrawerLayout = viewFragment.findViewById(R.id.drawerlayout_moniter);
        mTextMoniterDevicesHint = viewFragment.findViewById(R.id.text_moniter_devices_hint);
        // 侧滑点击事件
        viewFragment.findViewById(R.id.btn_toggle_channellist).setOnClickListener(listenerToggleChannelList);

        return viewFragment;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mDevicesListener = null;
        mMoniterData.RemoveListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        mMoniterData = ViewModelProviders.of(getActivity()).get(MoniterDataViewModel.class);
        // 根据数据加载列表
        InitViewData();
    }

    private void InitViewData() {
        // channel数据
        if (mSilderChannelList != null) {
            mChannelAdapter = new ChannelListAdapter(getContext());
            mSilderChannelList.setAdapter(mChannelAdapter);
            // channel listview点击事件
            mSilderChannelList.setOnItemClickListener(new ChannelListItemClickListener());
        }
        // 设备数据
        if (mDevicesRecyclerView != null) {
            Context context = mDevicesRecyclerView.getContext();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
            mDevicesRecyclerView.setLayoutManager(layoutManager);
            mDevicesAdapter = new DevicesListViewAdapter(getContext(), this);
            mDevicesAdapter.setHasStableIds(true);
            mDevicesRecyclerView.setAdapter(mDevicesAdapter);
        }
        // 事件监听
        mMoniterData.AddListener(this);

        // 加载自动数据
        boolean bSuccess = mMoniterData.LoadConfig(Options.getDevicesConfigPath(getContext()), true);

        // 数据源显示
        ChannelListDataUpdate();
    }

    // channel list添加数据源
    private void ChannelListDataUpdate() {
        // 清空适配器
        mChannelAdapter.RemoveAllChannel();
        // 判断数据是否加载
        List<Channel> channels = mMoniterData.getChannelList();
        Channel channelFirst = null;
        if (channels.size() > 0) {
            channelFirst = channels.get(0);
            for (int i = 0; i < channels.size(); ++i) {
                mChannelAdapter.AddChannel(channels.get(i));
            }
        }
        // 切换至第一个channel
        mChannelAdapter.SetCheck(0);
        OnSwitchChannel(channelFirst);
    }

    // 切换channel
    private void OnSwitchChannel(Channel channel) {
        ChannelListAdapter.ChannelListItem channelCheckItem = mChannelAdapter.GetCheckedChannel();
        // 当前有设备清空设备列表
        if (channelCheckItem != null) {
            // 移除设备列表view数据
            mDevicesAdapter.RemoveAllDevices();
            // 移除tags-fragment显示
            SwitchTagsFragment(null);
        }
        // 存在设备
        boolean bDeviceExist = false;
        Device deviceFirst = null;
        if (channel != null) {
            //遍历设备
            List<Device> devices = channel.getDevices();
            if (devices != null && devices.size() > 0) {
                bDeviceExist = true;
                deviceFirst = devices.get(0);
                // 添加设备
                for (Device device : devices) {
                    mDevicesAdapter.AddDevice(device, true);
                }
            }
        }
        if (bDeviceExist) {
            // 显示list
            mDevicesRecyclerView.setVisibility(View.VISIBLE);
            // 隐藏提示
            mTextMoniterDevicesHint.setVisibility(View.INVISIBLE);
        } else {
            // 隐藏list
            mDevicesRecyclerView.setVisibility(View.INVISIBLE);
            // 显示提示
            mTextMoniterDevicesHint.setVisibility(View.VISIBLE);
        }
        // 显示第一个设备
        mDevicesAdapter.SetCheck(0);
        SwitchTagsFragment(deviceFirst);
    }

    // 标题栏左侧channel按钮被点击
    private Button.OnClickListener listenerToggleChannelList = new OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean isopen = mDrawerLayout.isDrawerOpen(mSilderChannelList);
            if (isopen) mDrawerLayout.closeDrawer(mSilderChannelList, true);
            else mDrawerLayout.openDrawer(mSilderChannelList, true);
        }
    };
    @Override
    public void OnDeviceStateChangedNotify(final Device _device, int state) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDevicesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void OnLoadConfigComplete() {

    }

    @Override
    public void OnDeviceReadOnceCompleted(Device _device) {

    }

    // channel 列表被点击事件
    private class ChannelListItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //将当前的侧滑菜单关闭，调用DrawerLayout的closeDrawer（）方法即可
            mDrawerLayout.closeDrawer(mSilderChannelList, true);
            // 当前选择不作处理
            if (mChannelAdapter.IsCheck(i)) return;
            //将选中的菜单项置为高亮
            mSilderChannelList.setItemChecked(i, true);
            mChannelAdapter.SetCheck(i);
            //将ActionBar中标题更改为选中的标题项
            //setTitle(menuDrawerAdapter.getItem(i).menuTitle);
            ChannelListAdapter.ChannelListItem checkitem = mChannelAdapter.GetCheckedChannel();
            // 切换显示
            if (checkitem != null) {
                OnSwitchChannel(checkitem.channel);
            }
        }
    }

    @Override
    public void onClickListDeviceItem(DevicesListViewAdapter.DevicesListItem _item) {
        if (_item == null) return;
        // 切换至设备的Fragment
        SwitchTagsFragment(_item.mDevice);
    }

    @Override
    public void onClickListDeviceItemRunOrStop(DevicesListViewAdapter.DevicesListItem item) {
        if (item == null) return;
        // 切换设备开关状态
        mMoniterData.SwitchToggleDevice(item.mDevice);
    }

    // 切换至设备的Fragment
    private void SwitchTagsFragment(Device _device) {
        FragmentManager manager = getFragmentManager();//getSupportFragmentManager();
        if (manager == null) return;
        FragmentTransaction transaction = manager.beginTransaction();
        TagsFragment fragment = null;
        if (_device != null) {
            fragment = (TagsFragment) manager.findFragmentByTag(Long.valueOf(_device.GetPtr()).toString());
        }
        Iterator<TagsFragment> iter = mListFragmentTags.iterator();
        // 遍历处理fragment
        while (iter.hasNext()) {
            TagsFragment _tf = iter.next();
            if (_tf == null) continue;
            Fragment findtagfragment = manager.findFragmentByTag(_tf.getTag());
            // 缓存中未找到，从缓存中移除
            if (findtagfragment == null) {
                iter.remove();
                continue;
            }
            // 同一fragment不作处理
            if (fragment == findtagfragment) continue;
            // 显示则隐藏
            if (!findtagfragment.isHidden())
                transaction.hide(findtagfragment);
        }

        if (fragment == null && _device != null) {
            // 创建新tagfragment
            fragment = new TagsFragment();
            transaction.add(R.id.fragment_stub_tags, fragment, Long.valueOf(_device.GetPtr()).toString());
            mListFragmentTags.add(fragment);
        }
        // 显示目标
        if (_device != null)
            transaction.show(fragment);
        transaction.commit();
    }
}
