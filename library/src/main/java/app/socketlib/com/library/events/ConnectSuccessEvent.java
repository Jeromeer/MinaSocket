package app.socketlib.com.library.events;

/**
 * author：JianFeng
 * date：2017/8/18 16:13
 * description：socket连接成功,用于通知订阅重新订阅一次
 */
public class ConnectSuccessEvent {
    private int connectType ;

    public ConnectSuccessEvent(int connectType) {
        this.connectType = connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public int getConnectType() {
        return connectType;
    }
}
