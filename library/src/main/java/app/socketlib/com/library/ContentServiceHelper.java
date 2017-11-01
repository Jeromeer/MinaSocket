package app.socketlib.com.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import app.socketlib.com.library.socket.SocketConfig;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.SocketCommandCacheUtils;

/**
 * @author：JianFeng
 * @date：2017/10/31 15:29
 * @description：服务连接的管理类,服务绑定,解绑,数据发送等在此进行
 */
public class ContentServiceHelper {
    private static ConnectServiceBinder socketBinder;//服务的binder


    private static ServiceConnection serviceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketBinder = (ConnectServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketBinder = null;
        }
    };

    //绑定服务
    public static boolean bindService(Context context, SocketConfig socketConfig) {
        Intent intent = new Intent(Contants.ACTION_SERVICE_CONTENT);
        intent.setPackage(context.getPackageName());
        Bundle bundle = new Bundle();
        bundle.putParcelable(Contants.STOCK_CONFIG_KEY, socketConfig);
        intent.putExtras(bundle);
        SocketCommandCacheUtils.getInstance().initCache(); //初始化command缓存
        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //解绑服务
    public static void unBindService(Context context) {
        if (null != serviceConnection) {
            context.unbindService(serviceConnection);
            SocketCommandCacheUtils.getInstance().removeAllCache();//清除所有command缓存
        }

    }

    /***
     * 发送客户端数据到service
     * @param msg
     */
    public static void sendClientMsg(String msg){
        sendMessage(socketBinder,msg);
    }



    private static void sendMessage(ConnectServiceBinder connectBinder, String msg) {
        if (null != connectBinder) {
            try {
                SocketCommandCacheUtils.getInstance().addCache(msg);
                connectBinder.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
