package app.socketlib.com.library.socket;

import android.text.TextUtils;

import org.apache.mina.core.session.IoSession;

import app.socketlib.com.library.listener.SocketResponseListener;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.LogUtil;

/**
 * author：JianFeng
 * date：2017/8/11 13:58
 * description：消息的收发处理
 */
public class SessionManager {
    private IoSession session;
    private volatile static SessionManager mInstance = null;
    private SocketResponseListener listener;


    public static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    public SessionManager() {

    }

    public void setSeesion(IoSession session) {
        this.session = session;
    }

    public void setReceivedResponseListener(SocketResponseListener listener) {
        this.listener = listener;
    }


    /***
     * 写出消息到服务器
     * @param msg
     */
    public void writeToServer(Object msg) {
        if (session != null) {
            LogUtil.i("Clicnt向Server发送了数据:"+msg);
            session.write(msg);
        }
    }


    /***
     * 写出数据到客户端
     * @param msg
     */
    public void writeToClient(String msg) {
        if (!TextUtils.isEmpty(msg) && null != listener) {
            LogUtil.i("服务端向客户端推送了数据:"+msg);
            listener.socketMessageReceived(msg);
        }
    }


    public void closeSession(int closeType) {
        if (closeType == Contants.CONNECT_CLOSE_TYPE && session != null) {
            session.closeOnFlush();
        }
    }

    public void removeSession(int closeType) {
        if (closeType == Contants.CONNECT_CLOSE_TYPE) {
            this.session = null;
        }
    }

}
