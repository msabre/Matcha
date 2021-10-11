package usecase;

import usecase.port.ChatAffiliationRepository;
import usecase.port.MessageRepository;

public class ChatDelete {
    private final MessageRepository messageRepository;
    private final ChatAffiliationRepository chatAffiliationRepository;

    public ChatDelete(MessageRepository messageRepository, ChatAffiliationRepository chatAffiliationRepository) {
        this.messageRepository = messageRepository;
        this.chatAffiliationRepository = chatAffiliationRepository;
    }

    public boolean delete(int chatId, int userId) {
        return messageRepository.deleteAllByUserId(chatId, userId);
//        return chatAffiliationRepository.delete(chatId);
    }
}
