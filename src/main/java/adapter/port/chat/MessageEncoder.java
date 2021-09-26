package adapter.port.chat;

import domain.entity.model.chat.TransportMessage;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<TransportMessage> {

    @Override
    public String encode(TransportMessage transportMessage) {
        return new String(transportMessage.getMessage().getContent());
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {

    }
}
