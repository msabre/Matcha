package application.websocket;

import application.services.json.JsonService;
import domain.entity.model.chat.WebSocketEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<WebSocketEntity> {
    @Override
    public WebSocketEntity decode(String s) {
        return (WebSocketEntity) JsonService.getObject(WebSocketEntity.class , s);
    }

    @Override
    public boolean willDecode(String s) {
        try {
            new JSONObject(s);
        } catch (JSONException ex) {
            try {
                new JSONArray(s);
            } catch (JSONException arrayEx) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
