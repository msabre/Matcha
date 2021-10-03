package usecase;

import domain.entity.ChatAffiliation;
import domain.entity.Photo;
import domain.entity.model.UserMatch;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;
import usecase.port.UserCardRepository;

import java.util.*;

public class GetMatchList {
    private final LikesActionRepository likesActionRepository;
    private final UserCardRepository userCardRepository;
    private final ChatAffiliationRepository affiliationRepository;

    public GetMatchList(LikesActionRepository likesActionRepository, UserCardRepository userCardRepository, ChatAffiliationRepository affiliationRepository) {
        this.likesActionRepository = likesActionRepository;
        this.userCardRepository = userCardRepository;
        this.affiliationRepository = affiliationRepository;
    }

    public List<UserMatch> getN(int id, int size) {
        List<Integer> matchIds = likesActionRepository.getNMatchUserIds(id, size);
        return formUserMatches(id, matchIds);
    }

    public List<UserMatch> getNAfterSpecificId(int id, int lastId, int size) {
        List<Integer> matchIds = likesActionRepository.getNMatchUserIdsAfterSpecificId(id, lastId, size);
        return formUserMatches(id, matchIds);
    }

    // TODO int size
    // Тяжелый запрос нужно ограничить, лайков может быть очень много
    // Как вариант сделать получение по примеру чата
    private List<UserMatch> formUserMatches(int id, List<Integer> matchIds) {
        List<Photo> photoList = userCardRepository.getIconsByIds(matchIds);
        List<ChatAffiliation> chatAffiliation = affiliationRepository.getByUserId(id); // TODO получать только тех пользователей котрые есть в matchIds

        List<UserMatch> userMatches = new LinkedList<>();
        for (int userId : matchIds) {
            UserMatch match = new UserMatch();
            match.setUserId(userId);

            Photo icon = photoList.stream().filter(p -> p.getUserId() == userId).findFirst().orElse(null);
            Integer chat = chatAffiliation.stream().filter(c -> c.getToUsr() == userId).findFirst().map(ChatAffiliation::getChatId).orElse(null);

            match.setIcon(icon);
            match.setChatId(chat);
            userMatches.add(match);
        }
        return userMatches;
    }
}
