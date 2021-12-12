
package application.websocket;

import adapter.controller.JwtController;
import adapter.controller.MessageController;
import config.MyConfiguration;
import domain.entity.JsonWebToken;
import domain.entity.LikeAction;
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

@ServerEndpoint(value = "/{userId}/{token}/{fingerprint}",
                decoders = MessageDecoder.class,
                encoders = MessageEncoder.class)
public class WebSocketEndpoint {
    private static final int MESSAGE_SIZE_PACK = 10;
    private static final List<ChatUser> usersList = new ArrayList<>();

    MessageController messageController = MyConfiguration.messageController();
    JwtController jwtController = MyConfiguration.jwtController();
//    UserController userController = MyConfiguration.userController();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String user, @PathParam("token") String token,
                       @PathParam("fingerprint") String fingerprint) {

        int userId = Optional.ofNullable(user).map(Integer::parseInt).orElse(-1);

        Pair<Boolean, String> checkDesc = jwtController.checkAccessToken(token, fingerprint);
        if (!checkDesc.getLeft()) {
            session.getAsyncRemote().sendObject(newAnswer(checkDesc.getRight()));
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
    public void onMessage(WebSocketEntity webSocketEntity, Session session) {
        try {
            int userId = Optional.ofNullable(session.getPathParameters().get("userId")).map(Integer::parseInt).orElse(-1);

            switch (webSocketEntity.getWebSocketType()) {
                case CHAT:
                    TransportMessage transportMessage = webSocketEntity.getTransportMessage();
                    messageProcess(transportMessage, userId);
                    break;
                case LIKE_NOTIFICATION:
                    LikeAction likeAction = webSocketEntity.getLikeAction();
                    Optional.ofNullable(likeAction).ifPresent(act -> act.setFromUsr(userId));
                    likeProcess(webSocketEntity);
                    break;
                default:
                    send(userId, newAnswer("UNEXPECTED TYPE PARAMETER"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void likeProcess(WebSocketEntity webSocketEntity) {
        LikeAction likeAction = webSocketEntity.getLikeAction();
        if (likeAction.getAction() == null || likeAction.getToUsr() < 0)
            return;

        switch (likeAction.getAction()) {
            case LIKE:
            case TAKE_LIKE:
            case VISIT:
            case MATCH:
                send(likeAction.getToUsr(), webSocketEntity);
                break;
            default:
                send(likeAction.getFromUsr(), newAnswer("UNEXPECTED ACTION PARAMETER"));
        }
    }

//    private void likeProcess(LikeAction likeAction) {
//        if (likeAction.getAction() == null || likeAction.getToUsr() < 0)
//            return;
//
//        switch (likeAction.getAction()) {
//            case LIKE:
//                boolean isMatch = userController.putMatchOrLike(likeAction.getFromUsr(), likeAction.getToUsr());
//                if (isMatch) {
//                    send(likeAction.getFromUsr(), newAnswer("MATCH"));
//                    return;
//                }
//                WebSocketEntity likeNotification = new WebSocketEntity();
//                likeNotification.setLikeAction(likeAction);
//                send(likeAction.getToUsr(), likeNotification);
//                break;
//            case DISLIKE:
//                userController.disLike(likeAction.getFromUsr(), likeAction.getToUsr());
//                break;
//            case TAKE_LIKE:
//                userController.deleteLike(likeAction.getFromUsr(), likeAction.getToUsr());
//                break;
//            default:
//                send(likeAction.getFromUsr(), newAnswer("UNEXPECTED ACTION PARAMETER"));
//                return;
//        }
//        newAnswer("SUCCESS");
//    }

    private void messageProcess(TransportMessage transportMessage, int userId) {
        int chatId = transportMessage.getChatId();

        if (transportMessage.getMessage() != null) {
            Message message = transportMessage.getMessage();
            message.setStatus(MessageStatus.RECEIVED);
            message = messageController.save(message);

            TransportMessage answerMessage = new TransportMessage();
            answerMessage.setChatId(chatId);
            answerMessage.setMessageNotification(createNotification(message));
            WebSocketEntity webSocketEntity = new WebSocketEntity();
            webSocketEntity.setTransportMessage(answerMessage);
            send(message.getToId() , webSocketEntity);

        } else if (transportMessage.getGetMessageRq() != null) {
            TransportMessage.GetMessageRq getMessageRq = transportMessage.getGetMessageRq();
            List<Message> messageList;

            switch (getMessageRq.getType()) {
                case GET_FIRST_PACK:
                    messageList = messageController.getFirstNMatches(chatId, userId, MESSAGE_SIZE_PACK);
                    break;
                case BY_IDS:
                    messageList = messageController.getNByIds(chatId, getMessageRq.getMessageIds());
                    break;
                case BEFORE_FIRST:
                    messageList = messageController.getListOfNSizeBeforeSpecificId(chatId, userId, getMessageRq.getSpecificId(), MESSAGE_SIZE_PACK);
                    break;
                case AFTER_LAST:
                    messageList = messageController.getListOfNSizeAfterSpecificId(chatId, userId, getMessageRq.getSpecificId(), MESSAGE_SIZE_PACK);
                    break;
                default:
                    send(userId, newAnswer("WRONG"));
                    return;
            }
            sendMessageList(chatId, userId, messageList);

        } else if (transportMessage.getDeleteMessage() != null) {
            TransportMessage.DeleteMessage deleteMessage = transportMessage.getDeleteMessage();

            switch (deleteMessage.getType()) {
                case BY_IDS:
                    messageController.deleteByIdsForUser(chatId, userId, deleteMessage.getIds());
                    break;
                case ALL:
                    messageController.deleteAllForUser(chatId, userId);
                    break;
                default:
                    send(userId, newAnswer("WRONG"));
                    return;
            }
            send(userId, newAnswer("SUCCESS"));

        }
        else if (transportMessage.getDeliveryNotification() != null) {
            TransportMessage.DeliveryNotification deliveryNotification = transportMessage.getDeliveryNotification();

            if (messageController.markAsRead(deliveryNotification.getIds()))
                send(userId, newAnswer("SUCCESS"));
            else
                send(userId, newAnswer("WRONG"));
        }
    }

    private WebSocketEntity newAnswer(String text) {
        WebSocketEntity webSocketEntity = new WebSocketEntity();
        webSocketEntity.getAnswer().setText(text);
        return webSocketEntity;
    }

    private TransportMessage.MessageNotification createNotification(Message message) {
        TransportMessage.MessageNotification notification = new TransportMessage.MessageNotification();
        notification.setMessageId(message.getId());
        notification.setSenderId(message.getFromId());

        return notification;
    }

    private void sendMessageList(int chatId, int toUserId, List<Message> messageList) {
        WebSocketEntity webSocketEntity = new WebSocketEntity();
        webSocketEntity.setTransportMessage(new TransportMessage());
        webSocketEntity.getTransportMessage().setMessageAnswer(new ArrayList<>(messageList.size()));

        messageList.forEach(m -> webSocketEntity.getTransportMessage().getMessageAnswer().add(m));
        webSocketEntity.getTransportMessage().setChatId(chatId);
        send(toUserId, webSocketEntity);
    }

    private void send(int toUserId, WebSocketEntity msgObj) {
        for (ChatUser user : usersList) {
            if (user.getUserId() == toUserId)
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