package adapter.port.chat;

import application.services.json.JsonService;
import domain.entity.model.WebSocketMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<WebSocketMessage> {

    @Override
    public String encode(WebSocketMessage webSocketMessage) throws EncodeException {
        return JsonService.getJson(webSocketMessage);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {

    }
}
