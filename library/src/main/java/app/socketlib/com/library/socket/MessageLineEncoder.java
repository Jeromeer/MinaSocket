package app.socketlib.com.library.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * author：JianFeng
 * date：2017/8/17 17:47
 * description：数据编码器,根据当前系统默认的编码对数据进行编码成二进制发送给服务器
 */
public class MessageLineEncoder implements ProtocolEncoder {
    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        String s = null ;
        if(message instanceof String){
            s = (String) message;
        }
        CharsetEncoder charsetEncoder = (CharsetEncoder) ioSession.getAttribute("encoder");
        if(null == charsetEncoder){
            charsetEncoder = Charset.defaultCharset().newEncoder();
            ioSession.setAttribute("encoder",charsetEncoder);
        }

        if(null!=s){
            IoBuffer buffer = IoBuffer.allocate(s.length());
            buffer.setAutoExpand(true);//设置是否可以动态扩展大小
            buffer.putString(s,charsetEncoder);
            buffer.flip();
            protocolEncoderOutput.write(buffer);
        }
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {

    }
}
