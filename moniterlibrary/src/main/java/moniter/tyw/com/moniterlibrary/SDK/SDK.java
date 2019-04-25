package moniter.tyw.com.moniterlibrary.SDK;

import moniter.tyw.com.moniterlibrary.common.Channel;
import moniter.tyw.com.moniterlibrary.common.Device;
import moniter.tyw.com.moniterlibrary.common.Tag;
import moniter.tyw.com.moniterlibrary.common.TagData;
import moniter.tyw.com.moniterlibrary.common.Variant;

public class SDK {

    //单例
    private static class SDKHodler {
        static SDK instance = new SDK();
    }

    public static SDK getInstance() {
        return SDKHodler.instance;
    }

    /**
     * Listener for JNI Call : 修改时同步修改C代码端
     */
    public interface OnSCADAListener {
        /**
         * 设备连接事件
         *
         * @param _devicePtr
         * @param _state     定义在C端 enDeviceState
         */
        public void OnDeviceStateEvent(long _devicePtr, int _state);

        /**
         * 设备读取数据回调
         *
         * @param _lDevicePtr
         * @param _lTagPtr
         * @param _tagData
         */
        public void OnDeviceReadDataEvent(long _lDevicePtr, long _lTagPtr, TagData _tagData);

        /**
         * 读取一次结束回调
         * @param _lDevicePtr
         * @param _success
         */
        public void OnDeviceReadOnceCompleted(long _lDevicePtr, boolean _success);
    }


    protected SDK() {

    }

    /**
     * 初始化SDK
     *
     * @return
     */
    public native void Init();

    /***
     *  加载配置: C端加载配置，创建配置相关设备，然后java端获取设备相关信息
     * @param _configPath
     * @return 加载是否成功
     */
    public native boolean LoadConfig(String _configPath);

    /**
     * 获取通道列表
     *
     * @return
     */
    public native Channel[] GetChannelList();

    /**
     * 根据通道索引获取设备列表
     *
     * @param _channelPtr
     * @return
     */
    public native Device[] GetDeviceList(long _channelPtr);

    /**
     * 根据通道及设备获取点列表
     *
     * @param _channelPtr
     * @param _DevicePtr
     * @return
     */
    public native Tag[] GetTagList(long _channelPtr, long _DevicePtr);

    /**
     * 启/停用设备数据采集
     *
     * @param _devicePtr
     * @param _on
     */
    public native void ToggleDeviceAcquisition(long _devicePtr, boolean _on);

    /**
     * 增加设备事件监听
     *
     * @param listener
     */
    public native void AddDeviceListener(OnSCADAListener listener);

    /**
     * 设置设备参数
     * @param _devicePtr
     * @param _key
     * @param _set
     * @return
     */
    public native String SetDeviceParam(long _devicePtr,int _key, int _set);
    public native String SetDeviceParam(long _devicePtr,int _key, String _set);

    /**
     * 设置点参数
     * @param _tagPtr
     * @param _key
     * @param _set
     * @return
     */
    public native String SetTagParam(long _devicePtr,long _tagPtr,int _key, int _set);
    public native String SetTagParam(long _devicePtr,long _tagPtr,int _key, String _set);

    /**
     * 写数据
     * @param _devicePtr
     * @param _tagPtr
     * @param _data
     * @return
     */
    public native boolean WriteData(long _devicePtr,long _tagPtr,int _type,Variant _data);

    static {
        System.loadLibrary("MoniterNativeSDK");
    }
}
