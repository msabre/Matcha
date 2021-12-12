package domain.entity.model.chat;

import domain.entity.LikeAction;

public class WebSocketEntity {
    private WebSocketType webSocketType;
    private TransportMessage transportMessage;
    private LikeAction likeAction;
    private Answer answer;

    public WebSocketType getWebSocketType() {
        return webSocketType;
    }

    public void setWebSocketType(WebSocketType webSocketType) {
        this.webSocketType = webSocketType;
    }

    public TransportMessage getTransportMessage() {
        return transportMessage;
    }

    public void setTransportMessage(TransportMessage transportMessage) {
        this.transportMessage = transportMessage;
    }

    public LikeAction getLikeAction() {
        return likeAction;
    }

    public void setLikeAction(LikeAction likeAction) {
        this.likeAction = likeAction;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public static class Answer {
        private String text;

        public Answer(String errorText) {
            this.text = errorText;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
    
    public enum WebSocketType {
        CHAT,
        LIKE_NOTIFICATION
    }
}
