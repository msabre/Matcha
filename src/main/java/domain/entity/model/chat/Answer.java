package domain.entity.model.chat;

public class Answer {
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
