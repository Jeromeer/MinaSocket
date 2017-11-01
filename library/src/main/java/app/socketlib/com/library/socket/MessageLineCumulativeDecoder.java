package app.socketlib.com.library.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * author：JianFeng
 * date：2017/8/17 19:45
 * description：数据解码器,当字符为\n时,一次订阅完成
 */
public class MessageLineCumulativeDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        int startPosition = in.position();
        while (in.hasRemaining()) {
            byte b = in.get();
            if (b == '\n') {//读取到\n时候认为一行已经读取完毕
                int currentPosition = in.position();
                int limit = in.limit();
                in.position(startPosition);
                in.limit(limit);
                IoBuffer buffer = in.slice();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                String message = new String(bytes);
                protocolDecoderOutput.write(message);
                in.position(currentPosition);
                in.limit(limit);
                return true;
            }
        }
        in.position(startPosition);
        return false;
    }
}
