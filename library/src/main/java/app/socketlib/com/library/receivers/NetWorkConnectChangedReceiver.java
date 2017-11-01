package app.socketlib.com.library.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.socketlib.com.library.events.ConnectCloseAllEvent;
import app.socketlib.com.library.events.ConnectClosedEvent;
import app.socketlib.com.library.utils.Bus;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.LogUtil;

/**
 * author：JianFeng
 * date：2017/8/22 11:04
 * description：监听网络变化的广播
 */
public class NetWorkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG ="NetWorkConnectChangedReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        LogUtil.w("当前WiFi连接可用 ");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        LogUtil.w( "当前移动网络连接可用 ");
                    }
                    //发送广播通知服务重新启动
                    Bus.post(new ConnectClosedEvent(Contants.CONNECT_CLOSE_TYPE));
                } else {
                    LogUtil.w("当前没有网络连接，请确保你已经打开网络 ");
                    //无网络连接,通知socket,取消每三秒重连接
                    Bus.post(new ConnectCloseAllEvent(true));
                }
            } else {   // not connected to the internet
                LogUtil.w("当前没有网络连接，请确保你已经打开网络 ");
                //无网络连接,通知socket,取消每三秒重连接
                Bus.post(new ConnectCloseAllEvent(true));
            }
        }
    }
}
