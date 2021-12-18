package domain.entity.model.chat;

import domain.entity.LikeAction;

public class WebSocketEntity {
    private WebSocketType type;
    private TransportMessage transportMessage;
    private LikeAction likeAction;
    private Answer answer;

    public WebSocketType getType() {
        return type;
    }

    public void setType(WebSocketType type) {
        this.type = type;
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
        ACTION_NOTIFICATION
    }
}
