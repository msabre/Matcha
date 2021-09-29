package adapter.port.chat;

import adapter.controller.MessageController;
import config.MyConfiguration;
import domain.entity.model.chat.ChatUser;
import domain.entity.model.chat.GetMessageRq;
import domain.entity.Message;
import domain.entity.model.chat.MessageNotification;
import domain.entity.model.chat.TransportMessage;
import domain.entity.model.types.MessageStatus;

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

    MessageController messageController = MyConfiguration.messageController();

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
        if (!messages.isEmpty()) {
            int finalChatId = chatId;
            messages.forEach(mess -> {
                TransportMessage transportMessage = new TransportMessage();
                transportMessage.setMessage(mess);
                send(finalChatId, transportMessage);
            });
        }
    }

    @OnMessage
    public void onMessage(final Session session, TransportMessage msgObj) {
        try {
            if (msgObj.getMessage() != null) {
                Message message = msgObj.getMessage();
                message.setStatus(MessageStatus.RECEIVED);
                message = messageController.save(message);

                TransportMessage transportMessage = new TransportMessage();
                transportMessage.setMessageNotification(createNotification(message));
                send(message.getChatId(), transportMessage);

            } else if (msgObj.getGetMessageRq() != null) {
                GetMessageRq getMessageRq = msgObj.getGetMessageRq();
                List<Message> messageList = messageController.getNByIds(getMessageRq.getChatId(), getMessageRq.getMessageIds());
                messageList.forEach(msg -> {
                    TransportMessage transportMessage = new TransportMessage();
                    transportMessage.setMessage(msg);
                    send(getMessageRq.getChatId(), transportMessage);
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MessageNotification createNotification(Message message) {
        MessageNotification notification = new MessageNotification();
        notification.setMessageId(message.getId());
        notification.setSenderId(message.getFromId());

        return notification;
    }

    private void send(int chatId, TransportMessage msgObj) {
        for (ChatUser user : usersList) {
            if (user.getChatId() == chatId) {
                user.getSession().getAsyncRemote().sendObject(msgObj);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        usersList.remove(getWebSocketUser(session));
        // уведомить что вышел
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
