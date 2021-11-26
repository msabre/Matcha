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
import java.util.Optional;

@ServerEndpoint(value = "/chat/{userId}/{token}/{fingerprint}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class)
public class ChatEndpoint {
    private static final int MESSAGE_SIZE_PACK = 10;
    private static final List<ChatUser> usersList = new ArrayList<>();

    MessageController messageController = MyConfiguration.messageController();
    JwtController jwtController = MyConfiguration.jwtController();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String user, @PathParam("token") String token,
                       @PathParam("fingerprint") String fingerprint) {

//        int chatId = Optional.ofNullable(chat).map(Integer::parseInt).orElse(-1);
        int userId = Optional.ofNullable(user).map(Integer::parseInt).orElse(-1);

        Pair<Boolean, String> checkDesc = jwtController.checkAccessToken(token, fingerprint);
        if (!checkDesc.getLeft()) {
            TransportMessage error = new TransportMessage();
            error.setAnswer(new TransportMessage.Answer(checkDesc.getRight()));
            session.getAsyncRemote().sendObject(error);
            return;
        }

        JsonWebToken jsonWebToken = new JsonWebToken();
        jsonWebToken.setToken(token);
        jsonWebToken.setUserFingerprint(fingerprint);
        jsonWebToken.setUserId(userId);

        ChatUser newUser = new ChatUser(userId, session, jsonWebToken);
        usersList.add(newUser);
    }

    @OnMessage(maxMessageSize = 3072000)
    public void onMessage(TransportMessage msgObj, Session session) {
        try {
            int userId = Optional.ofNullable(session.getPathParameters().get("userId")).map(Integer::parseInt).orElse(-1);
            int chatId = msgObj.getChatId();

            if (msgObj.getMessage() != null) {
                Message message = msgObj.getMessage();
                message.setStatus(MessageStatus.RECEIVED);
                message = messageController.save(message);

                TransportMessage transportMessage = new TransportMessage();
                transportMessage.setMessageNotification(createNotification(message));
                send(chatId, transportMessage);

            } else if (msgObj.getGetMessageRq() != null) {
                TransportMessage.GetMessageRq getMessageRq = msgObj.getGetMessageRq();
                List<Message> messageList;

                switch (getMessageRq.getType()) {
                    case GET_FIRST_PACK:
                        messageList = messageController.getFirstNMatches(chatId, userId, MESSAGE_SIZE_PACK);
                        break;
                    case BY_IDS:
                        messageList = messageController.getNByIds(chatId, getMessageRq.getMessageIds());
                        break;
                    case AFTER_LAST:
                        messageList = messageController.getListOfNSizeAfterSpecificId(chatId, userId, getMessageRq.getLastId(), MESSAGE_SIZE_PACK);
                        break;
                    default:
                        send(chatId, newAnswer("WRONG"));
                        return;
                }
                sendMessageList(chatId, messageList);

            } else if (msgObj.getDeleteMessage() != null) {
                TransportMessage.DeleteMessage deleteMessage = msgObj.getDeleteMessage();

                switch (deleteMessage.getType()) {
                    case BY_IDS:
                        messageController.deleteByIdsForUser(chatId, userId, deleteMessage.getIds());
                        break;
                    case ALL:
                        messageController.deleteAllForUser(chatId, userId);
                        break;
                    default:
                        send(chatId, newAnswer("WRONG"));
                        return;
                }
                send(chatId, newAnswer("SUCCESS"));

            }
            else if (msgObj.getDeliveryNotification() != null) {
                TransportMessage.DeliveryNotification deliveryNotification = msgObj.getDeliveryNotification();

                if (messageController.markAsRead(deliveryNotification.getIds()))
                    send(chatId, newAnswer("SUCCESS"));
                else
                    send(chatId, newAnswer("WRONG"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TransportMessage newAnswer(String text) {
        TransportMessage answer = new TransportMessage();
        answer.setAnswer(new TransportMessage.Answer(text));
        return answer;
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
        msgObj.setChatId(chatId);
        for (ChatUser user : usersList) {
            if (user.getUserId() == chatId)
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