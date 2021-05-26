package adapter.port.chat;

import application.services.json.JsonService;
import domain.entity.model.WebSocketMessage;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<WebSocketMessage> {
    @Override
    public WebSocketMessage decode(String s) throws DecodeException {
        return (WebSocketMessage) JsonService.getObject(WebSocketMessage.class , s);
    }

    @Override
    public boolean willDecode(String s) {
        return false;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
