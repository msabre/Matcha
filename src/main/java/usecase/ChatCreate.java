package usecase;

import usecase.port.ChatAffiliationRepository;

public class ChatCreate {
    private final ChatAffiliationRepository chatAffiliationRepository;

    public ChatCreate(ChatAffiliationRepository chatAffiliationRepository) {
        this.chatAffiliationRepository = chatAffiliationRepository;
    }

    public int create(int usrFrom, int usrTo) {
        int chatId = chatAffiliationRepository.getChatMaxId() + 1;
        if (chatId < 0)
            return -1;

        if (!chatAffiliationRepository.create(usrFrom, usrTo, chatId) || !chatAffiliationRepository.create(usrTo, usrFrom, chatId))
            return -1;
        return chatId;
    }
}
