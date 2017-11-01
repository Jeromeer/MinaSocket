package app.socketlib.com.library.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * author：JianFeng
 * date：2017/8/17 17:49
 * description：自定义的编解码工厂类
 */
public class MessageLineFactory implements ProtocolCodecFactory {
    private MessageLineCumulativeDecoder messageLineDecoder;
    private MessageLineEncoder messageLineEncoder;

    public MessageLineFactory() {
        messageLineDecoder = new MessageLineCumulativeDecoder();
        messageLineEncoder = new MessageLineEncoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return messageLineEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return messageLineDecoder;
    }
}
