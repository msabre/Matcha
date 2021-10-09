package adapter.port.chat;

import adapter.controller.JwtController;
import adapter.controller.MessageController;
import config.MyConfiguration;
import domain.entity.JsonWebToken;
import domain.entity.model.chat.*;
import domain.entity.Message;
import domain.entity.model.types.MessageStatus;
import org.apache.commons.lang3.tuple.Pair;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/chat/{userdata}/{token}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class)
public class ChatEndpoint {
    private static final String DELIMITER = "&&";
    private static final String AFTER_LAST = "AFTER_LAST";
    private static final String BY_IDS = "BY_IDS";

    private static final int MESSAGE_SIZE_PACK = 10;
    private static final List<ChatUser> usersList = new ArrayList<>();

    MessageController messageController = MyConfiguration.messageController();
    JwtController jwtController = MyConfiguration.jwtController();

    @OnOpen
    public void onOpen(Session session, @PathParam("userdata") String userdata, @PathParam("token") String token) {

        int chatId = -1;
        int userId = -1;
        String jwt = null;
        String fingerprint = null;

        String[] data = userdata.split(DELIMITER);
        String[] tokenData = token.split(DELIMITER);

        if (data.length == 2) {
            chatId = Integer.parseInt(data[0]);
            userId = Integer.parseInt(data[1]);
        }
        if (tokenData.length == 2) {
            jwt = tokenData[0];
            fingerprint = tokenData[1];
        }

        Pair<Boolean, String> checkDesc = jwtController.checkAccessToken(tokenData[0], tokenData[1]);
        if (!checkDesc.getLeft()) {
            TransportMessage error = new TransportMessage();
            error.setError(new TransportMessage.Error(checkDesc.getRight()));
            session.getAsyncRemote().sendObject(error);
            return;
        }

        JsonWebToken jsonWebToken = new JsonWebToken();
        jsonWebToken.setToken(jwt);
        jsonWebToken.setUserFingerprint(fingerprint);
        jsonWebToken.setUserId(userId);

        ChatUser newUser = new ChatUser(userId, chatId, session, jsonWebToken);
        usersList.add(newUser);

        List<Message> messages = messageController.getFirstNMatches(chatId, MESSAGE_SIZE_PACK);
        if (!messages.isEmpty())
            sendMessageList(chatId, messages);
    }

    @OnMessage
    public void onMessage(TransportMessage msgObj) {
        try {
            if (msgObj.getMessage() != null) {
                Message message = msgObj.getMessage();
                message.setStatus(MessageStatus.RECEIVED);
                message = messageController.save(message);

                TransportMessage transportMessage = new TransportMessage();
                transportMessage.setMessageNotification(createNotification(message));
                send(message.getChatId(), transportMessage);

            } else if (msgObj.getGetMessageRq() != null) {
                TransportMessage.GetMessageRq getMessageRq = msgObj.getGetMessageRq();
                List<Message> messageList;

                switch (getMessageRq.getType()) {
                    case BY_IDS:
                        messageList = messageController.getNByIds(getMessageRq.getChatId(), getMessageRq.getMessageIds());
                        break;
                    case AFTER_LAST:
                        messageList = messageController.getListOfNSizeAfterSpecificId(getMessageRq.getChatId(), getMessageRq.getLastId(), MESSAGE_SIZE_PACK);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + getMessageRq.getType());
                }
                sendMessageList(getMessageRq.getChatId(), messageList);

            } else if (msgObj.getDeleteMessage() != null) {
                TransportMessage.DeleteMessage deleteMessage = msgObj.getDeleteMessage();
                messageController.deleteNByIds(deleteMessage.getChatId(), deleteMessage.getIds());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TransportMessage.MessageNotification createNotification(Message message) {
        TransportMessage.MessageNotification notification = new TransportMessage.MessageNotification();
        notification.setMessageId(message.getId());
        notification.setSenderId(message.getFromId());

        return notification;
    }

    private void sendMessageList(int chatId, List<Message> messageList) {
        TransportMessage transportMessage = new TransportMessage();
        transportMessage.setMessageAnswer(new ArrayList<>(messageList.size()));
        messageList.forEach(m -> transportMessage.getMessageAnswer().add(m));
        send(chatId, transportMessage);
    }

    private void send(int chatId, TransportMessage msgObj) {
        for (ChatUser user : usersList) {
            if (user.getChatId() == chatId)
                user.getSession().getAsyncRemote().sendObject(msgObj);
        }
    }

    @OnClose
    public void onClose(Session session) {
        usersList.remove(getWebSocketUser(session));
    }

    @OnError
    public void onError(Throwable t) {
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