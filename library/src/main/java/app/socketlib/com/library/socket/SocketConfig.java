package app.socketlib.com.library.socket;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * author：JianFeng
 * date：2017/8/11 13:58
 * description：socket配置相关
 */
public class SocketConfig  implements Parcelable{

    private Context context;
    private String ip;
    private int port;
    private int readBufferSize;
    private int idleTimeOut;
    private int requestInterval;
    private int timeOutCheckInterval;
    private  String heartbeatRequest;
    private  String heartbeatResponse;


    protected SocketConfig(Parcel in) {
        ip = in.readString();
        port = in.readInt();
        readBufferSize = in.readInt();
        idleTimeOut = in.readInt();
        requestInterval = in.readInt();
        timeOutCheckInterval = in.readInt();
        heartbeatRequest=in.readString();
        heartbeatResponse = in.readString();
    }

    public SocketConfig() {
    }

    public static final Creator<SocketConfig> CREATOR = new Creator<SocketConfig>() {
        @Override
        public SocketConfig createFromParcel(Parcel in) {
            return new SocketConfig(in);
        }

        @Override
        public SocketConfig[] newArray(int size) {
            return new SocketConfig[size];
        }
    };

    public Context getContext() {
        return context;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public int getIdleTimeOut() {
        return idleTimeOut;
    }

    public int getRequsetInterval() {
        return requestInterval;
    }

    public int getConnetTimeOutCheckInterval() {
        return timeOutCheckInterval;
    }

    public String getHeartbeatRequest() {
        return heartbeatRequest;
    }

    public String getHeartbeatResponse() {
        return heartbeatResponse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ip);
        parcel.writeInt(port);
        parcel.writeInt(readBufferSize);
        parcel.writeInt(idleTimeOut);
        parcel.writeInt(requestInterval);
        parcel.writeInt(timeOutCheckInterval);
        parcel.writeString(heartbeatRequest);
        parcel.writeString(heartbeatResponse);
    }

    public static class Builder {
        private Context context;
        private String ip = "192.168.1.174";
        private int port = 2345;
        private int readBufferSize = 10240;
        private int idleTimeOut;
        private int requestInterval;
        private int timeOutCheckInterval;
        private  String heartbeatRequest;
        private  String heartbeatResponse;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setReadBufferSize(int readBufferSize) {
            this.readBufferSize = readBufferSize;
            return this;
        }

        public Builder setIdleTimeOut(int idleTimeOut) {
            this.idleTimeOut = idleTimeOut;
            return this;
        }

        public Builder setRequestInterval(int requestInterval) {
            this.requestInterval = requestInterval;
            return this ;
        }

        public Builder setTimeOutCheckInterval(int timeOutCheckInterval) {
            this.timeOutCheckInterval = timeOutCheckInterval;
            return this ;
        }

        public Builder setHeartbeatRequest(String heartbeatRequest) {
            this.heartbeatRequest = heartbeatRequest;
            return this;
        }

        public Builder setHeartbeatResponse(String heartbeatResponse) {
            this.heartbeatResponse = heartbeatResponse;
            return this;
        }

        private void applyConfig(SocketConfig config) {
            config.context = this.context;
            config.ip = this.ip;
            config.port = this.port;
            config.readBufferSize = this.readBufferSize;
            config.idleTimeOut = this.idleTimeOut;
            config.requestInterval = this.requestInterval;
            config.timeOutCheckInterval = this.timeOutCheckInterval;
            config.heartbeatRequest = this.heartbeatRequest;
            config.heartbeatResponse = this.heartbeatResponse ;
        }

        public SocketConfig builder() {
            SocketConfig config = new SocketConfig();
            applyConfig(config);
            return config;
        }
    }
}
