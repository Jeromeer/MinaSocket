package app.socketlib.com.library.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import app.socketlib.com.library.utils.LogUtil;

/**
 * author：JianFeng
 * date：2017/8/10 17:06
 * description：交易的心跳设置
 * 1.isRequest如果返回的正好是客户端发送的心跳请求参数,则底层处理,不回调到业务层
 * 2.isResponse返回的如果正好是服务端的心跳response,则底层处理,不回调到业务层
 * 3.其他的服务端返回数据一律回调到业务层,进行处理
 */
public class KeepAliveMessageFactoryImpm implements KeepAliveMessageFactory {

    private String heartbeatRequest;
    private String heartbeatResponse;

    public KeepAliveMessageFactoryImpm(String heartbeatRequest, String heartbeatResponse) {
        this.heartbeatRequest = heartbeatRequest;
        this.heartbeatResponse = heartbeatResponse;
    }

    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        if (o.equals(heartbeatRequest)) {
            LogUtil.w("客户端进入idle状态,向服务器发送心跳包:" + heartbeatRequest);
            return true;
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        if (o.equals(heartbeatResponse)) {
            LogUtil.w("服务器回复心跳包:" + heartbeatResponse);
            return true;
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        return heartbeatRequest;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        return heartbeatResponse;
    }
}
