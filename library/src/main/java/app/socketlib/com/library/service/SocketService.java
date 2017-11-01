package app.socketlib.com.library.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import app.socketlib.com.library.ConnectServiceBinder;
import app.socketlib.com.library.events.ConnectCloseAllEvent;
import app.socketlib.com.library.events.ConnectClosedEvent;
import app.socketlib.com.library.events.ConnectSuccessEvent;
import app.socketlib.com.library.receivers.NetWorkConnectChangedReceiver;
import app.socketlib.com.library.socket.ConnectionManager;
import app.socketlib.com.library.socket.SessionManager;
import app.socketlib.com.library.socket.SocketConfig;
import app.socketlib.com.library.utils.Bus;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.LogUtil;
import app.socketlib.com.library.utils.SocketCommandCacheUtils;


/**
 * @author：JianFeng
 * @date：2017/10/31 15:19
 * @description：
 */
public class SocketService extends Service {

    private ConnectionThread thread;
    private SocketConfig config;
    private NetWorkConnectChangedReceiver connectChangedReceiver;


    private ConnectServiceBinder binder = new ConnectServiceBinder() {
        @Override
        public void sendMessage(String message) {
            super.sendMessage(message);
            SessionManager.getInstance().writeToServer(message);
        }

    };


    @Override
    public void onCreate() {
        super.onCreate();
        //注册监听网络变化的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (null == connectChangedReceiver) {
            connectChangedReceiver = new NetWorkConnectChangedReceiver();
        }
        registerReceiver(connectChangedReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != connectChangedReceiver) {
            unregisterReceiver(connectChangedReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.w("socketService启动");
        Bus.register(this);
        Bundle bundle = intent.getExtras();
        config = (SocketConfig) bundle.get(Contants.STOCK_CONFIG_KEY);
        startHandlerThread();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.w("socketService解绑");
        Bus.unregister(this);
        SocketCommandCacheUtils.getInstance().removeAllCache();
        releaseHandlerThread();
        return super.onUnbind(intent);
    }


    public void startHandlerThread() {
        thread = new ConnectionThread("SocketService", config);
        thread.start();
    }


    public void releaseHandlerThread() {
        if (null != thread) {
            thread.disConnect();
            thread.quit();
            thread = null;
            LogUtil.w("连接被释放,全部重新连接");
        }
    }


    /***
     * 心跳超时,在此重启整个连接
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectClosedEvent event) {
        if (event.getColseType() == Contants.CONNECT_CLOSE_TYPE) {
            LogUtil.w("服务接收到心跳超时,重启整个推送连接");
            releaseHandlerThread();
            startHandlerThread();
        }
    }


    /***
     * 无网络关闭所有连接,不再继续重连
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectCloseAllEvent event) {
        if (event.isCloseAll()) {
            LogUtil.w("无网络,关闭所有连接");
            releaseHandlerThread();
        }
    }


    /***
     * 连接成功之后,在这里重新订阅所有信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectSuccessEvent event) {
        if (event.getConnectType() == Contants.CONNECT_SUCCESS_TYPE) {
            ArrayList<String> cache = SocketCommandCacheUtils.getInstance().getTradeCache();
            LogUtil.w("连接成功,重新订阅所有信息");
            if (null != cache) {
                for (int i = 0; i < cache.size(); i++) {
                    SessionManager.getInstance().writeToServer(cache.get(i));
                }
            }
        }
    }

    class ConnectionThread extends HandlerThread {

        ConnectionManager mManager;

        public ConnectionThread(String name, SocketConfig config) {
            super(name);
            if (null == mManager)
                LogUtil.w("ConnectionThread中子线程被开启");
            mManager = new ConnectionManager(config, Contants.CONNECT_CLOSE_TYPE);
        }

        @Override
        protected void onLooperPrepared() {
            if (null != mManager)
                mManager.connnectToServer();
        }

        public void disConnect() {
            if (null != mManager)
                mManager.disContect();
        }
    }
}
