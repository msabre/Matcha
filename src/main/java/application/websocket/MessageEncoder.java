package application.websocket;

import application.services.json.JsonService;
import domain.entity.model.chat.WebSocketEntity;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<WebSocketEntity> {

    @Override
    public String encode(WebSocketEntity webSocketEntity) {
        return JsonService.getJsonChat(webSocketEntity);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {

    }
}
