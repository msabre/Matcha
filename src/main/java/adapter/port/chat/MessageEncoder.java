package adapter.port.chat;

import domain.entity.Message;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(Message message) {
        return message.getContent();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {

    }
}
