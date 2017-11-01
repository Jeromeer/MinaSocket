package app.socketlib.com.library.listener;

/**
 * @author：JianFeng
 * @date：2017/10/31 16:56
 * @description：
 */
public interface SocketResponseListener {

    /***
     * 服务端返回消息的回调
     * @param msg
     */
    void socketMessageReceived(String msg);
}
