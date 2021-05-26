package adapter.port.chat;

import adapter.controller.MessageController;
import domain.entity.model.WebSocketUser;
import domain.entity.model.WebSocketMessage;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/{userdata}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class)
public class ChatEndpoint {
    private static final List<WebSocketUser> usersList = new ArrayList<>();
    MessageController messageController;

    @OnOpen
    public void onOpen(Session session, @PathParam("userdata") String userdata)
            throws IOException, EncodeException {

        int chatId = -1;
        int userId = -1;
        String[] data = userdata.split("_");

        if (data.length == 2) {
            chatId = Integer.parseInt(data[0]);
            userId = Integer.parseInt(data[1]);
        }
        WebSocketUser newUser = new WebSocketUser(userId, chatId, session);
        usersList.add(newUser);
    }

    @OnMessage
    public void onMessage(final Session session, WebSocketMessage msg)
            throws IOException, EncodeException {

        for (WebSocketUser user : usersList) {
            if (user.getChatId() == msg.getChatId()) {
                user.getSession().getAsyncRemote().sendObject(msg);
                messageController.save(msg);
                break;
            }
        }
    }

    @OnClose
    public void onClose(Session session)
            throws IOException, EncodeException {

        usersList.remove(getWebSocketUser(session));
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }

    private WebSocketUser getWebSocketUser(Session session)
    {
        WebSocketUser wuser = null;
        for (WebSocketUser webSocketUser : usersList) {

            if (webSocketUser
                    .getSession()
                    .getId()
                    .equals(session.getId())) {
                wuser = webSocketUser;
                break;
            }
        }
        return wuser;
    }
}
