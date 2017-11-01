package app.socketlib.com.library.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

import app.socketlib.com.library.events.ConnectClosedEvent;
import app.socketlib.com.library.utils.Bus;
import app.socketlib.com.library.utils.LogUtil;

/**
 * author：JianFeng
 * date：2017/8/10 17:41
 * description：心跳监控
 */
public class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {

    private final int closeType;

    public KeepAliveRequestTimeoutHandlerImpl(int closeType) {
        this.closeType = closeType ;
    }

    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception {
     LogUtil.w("keepAliveRequestTimedOut心跳超时,重新连接:"+closeType);
        Bus.post(new ConnectClosedEvent(closeType));
    }
}
