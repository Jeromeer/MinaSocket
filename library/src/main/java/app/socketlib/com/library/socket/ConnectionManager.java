package app.socketlib.com.library.socket;


import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

import app.socketlib.com.library.events.ConnectClosedEvent;
import app.socketlib.com.library.events.ConnectSuccessEvent;
import app.socketlib.com.library.utils.Bus;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.LogUtil;

/**
 * author：JianFeng
 * date：2017/8/11 13:58
 * description：Socket连接的管理类
 */
public class ConnectionManager {
    private final int closeType;
    private SocketConfig mConfig;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    private enum ConnectStatus {
        DISCONNECTED,//连接断开
        CONNECTED//连接成功
    }

    private ConnectStatus status = ConnectStatus.DISCONNECTED;

    public ConnectStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectStatus status) {
        this.status = status;
    }

    public ConnectionManager(SocketConfig config, int closeType) {
        this.mConfig = config;
        this.closeType = closeType;
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getSessionConfig().setKeepAlive(true);//设置心跳
        //设置超过多长时间客户端进入IDLE状态
        mConnection.getSessionConfig().setBothIdleTime(mConfig.getIdleTimeOut());
        mConnection.setConnectTimeoutCheckInterval(mConfig.getConnetTimeOutCheckInterval());//设置连接超时时间
        mConnection.getFilterChain().addLast("Logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MessageLineFactory()));
        mConnection.setDefaultRemoteAddress(mAddress);
        //设置心跳监听的handler
        KeepAliveRequestTimeoutHandler heartBeatHandler = new KeepAliveRequestTimeoutHandlerImpl(closeType);
        KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpm(mConfig.getHeartbeatRequest(),mConfig.getHeartbeatResponse());
        //设置心跳
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE, heartBeatHandler);
        //是否回发
        heartBeat.setForwardEvent(false);
        //设置心跳间隔
        heartBeat.setRequestInterval(mConfig.getRequsetInterval());
        mConnection.getFilterChain().addLast("heartbeat", heartBeat);
        mConnection.setHandler(new DefaultIoHandler());
    }

    /**
     * 与服务器连接
     *
     * @return
     */
    public void connnectToServer() {
        int count = 0; //连接达到10次,则不再重连
        if (null != mConnection) {
            while (getStatus() == ConnectStatus.DISCONNECTED) {
                try {
                    Thread.sleep(3000);
                    ConnectFuture future = mConnection.connect();
                    future.awaitUninterruptibly();// 等待连接创建成功
                    mSession = future.getSession();
                    if (mSession.isConnected()) {
                        setStatus(ConnectStatus.CONNECTED);
                        SessionManager.getInstance().setSeesion(mSession);
                        Bus.post(new ConnectSuccessEvent(Contants.CONNECT_SUCCESS_TYPE));
                        LogUtil.e("connnectToServer中,Socket连接成功!");
                        break;
                    }
                } catch (Exception e) {
                    count++;
                    LogUtil.e("connnectToServer中,Socket连接失败,每3秒连接一次!");
                    if (count == 10) {
                        Bus.post(new ConnectClosedEvent(closeType));
                    }
                }
            }
        }
    }

    /**
     * 断开连接
     */
    public void disContect() {
        setStatus(ConnectStatus.CONNECTED);
        mConnection.getFilterChain().clear();
        mConnection.dispose();
        SessionManager.getInstance().closeSession(closeType);
        SessionManager.getInstance().removeSession(closeType);
        mConnection = null;
        mSession = null;
        mAddress = null;
    }

    /***
     * Socket的消息接收处理和各种连接状态的监听在这里
     */
    private class DefaultIoHandler extends IoHandlerAdapter {

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            SessionManager.getInstance().writeToClient(message.toString());

        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            LogUtil.e("session关闭,发送事件,重新连接");
            setStatus(ConnectStatus.DISCONNECTED);
            Bus.post(new ConnectClosedEvent(closeType));
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
        }

        @Override
        public void inputClosed(IoSession session) throws Exception {
            super.inputClosed(session);
            LogUtil.e("inputClosed,发送事件,重新连接");
            Bus.post(new ConnectClosedEvent(closeType));
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            super.sessionIdle(session, status);
            if (null != session) {
                session.closeNow();
            }
        }
    }
}
