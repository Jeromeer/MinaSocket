package app.socketlib.com.library.events;

/**
 * author：JianFeng
 * date：2017/8/22 13:51
 * description：关闭所有socket的连接
 */
public class ConnectCloseAllEvent {
    boolean closeAll;

    public ConnectCloseAllEvent(boolean closeAll) {
        this.closeAll = closeAll;
    }

    public boolean isCloseAll() {
        return closeAll;
    }

    public void setCloseAll(boolean closeAll) {
        this.closeAll = closeAll;
    }
}
