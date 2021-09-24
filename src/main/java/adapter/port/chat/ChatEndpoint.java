package adapter.port.chat;

import adapter.controller.MessageController;
import domain.entity.ChatUser;
import domain.entity.Message;
import domain.entity.types.MessageStatus;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/chat/{userdata}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class)
public class ChatEndpoint {
    private static final int MESSAGE_SIZE_PACK = 10;
    private static final List<ChatUser> usersList = new ArrayList<>();

    MessageController messageController;

    @OnOpen
    public void onOpen(Session session, @PathParam("userdata") String userdata) {

        int chatId = -1;
        int userId = -1;
        String[] data = userdata.split("_");

        if (data.length == 2) {
            chatId = Integer.parseInt(data[0]);
            userId = Integer.parseInt(data[1]);
        }
        ChatUser newUser = new ChatUser(userId, chatId, session);
        usersList.add(newUser);

        List<Message> messages = messageController.getFirstNMatches(chatId, MESSAGE_SIZE_PACK);
        if (messages != null) {
            messages.forEach(mess -> onMessage(session, mess));
        }
    }

    @OnMessage
    public void onMessage(final Session session, Message msg) {

        // подумать как получать и передавать разные json'ы
        messageController.save(msg);
        for (ChatUser user : usersList) {
            if (user.getChatId() == msg.getChatId()) {
                user.getSession().getAsyncRemote().sendObject(msg);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        usersList.remove(getWebSocketUser(session));
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }

    private ChatUser getWebSocketUser(Session session)
    {
        ChatUser wuser = null;
        for (ChatUser chatUser : usersList) {

            if (chatUser
                    .getSession()
                    .getId()
                    .equals(session.getId())) {
                wuser = chatUser;
                break;
            }
        }
        return wuser;
    }
}
