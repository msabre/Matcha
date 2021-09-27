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

    public List<UserMatch> get(int id) {
        List<Integer> matchIds = likesActionRepository.getMatchUserIds(id);
        List<Photo> photoList = userCardRepository.getIconsByIds(matchIds);
        List<ChatAffiliation> chatAffiliation = affiliationRepository.getByUserId(id);

        List<UserMatch> userMatches = new LinkedList<>();
        for (int userId : matchIds) {
            UserMatch match = new UserMatch();
            match.setUserId(userId);

            Photo icon = photoList.stream().filter(p -> p.getUserId() == userId).findFirst().orElse(null);
            Integer chat = chatAffiliation.stream().filter(c -> c.getUserId() == userId).findFirst().map(ChatAffiliation::getChatId).orElse(null);

            match.setIcon(icon);
            match.setChatId(chat);
        }
        return userMatches;
    }
}
