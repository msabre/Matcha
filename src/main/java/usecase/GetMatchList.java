package usecase;

import domain.entity.ChatAffiliation;
import domain.entity.LikeAction;
import domain.entity.Photo;
import domain.entity.model.UserMatch;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class GetMatchList {
    private final LikesActionRepository likesActionRepository;
    private final UserCardRepository userCardRepository;
    private final ChatAffiliationRepository affiliationRepository;
    private final UserRepository userRepository;

    public GetMatchList(LikesActionRepository likesActionRepository, UserCardRepository userCardRepository, ChatAffiliationRepository affiliationRepository, UserRepository userRepository) {
        this.likesActionRepository = likesActionRepository;
        this.userCardRepository = userCardRepository;
        this.affiliationRepository = affiliationRepository;
        this.userRepository = userRepository;
    }

    public List<UserMatch> getN(int id, int size) {
        List<LikeAction> matchIds = likesActionRepository.getNMatchForUserId(id, size);
        return formUserMatches(id, matchIds);
    }

    public List<UserMatch> getNAfterSpecificId(int id, int lastMatchId, int size) {
        List<LikeAction> matchIds = likesActionRepository.getNMatchUserIdsAfterSpecificId(id, lastMatchId, size);
        return formUserMatches(id, matchIds);
    }

    private List<UserMatch> formUserMatches(int id, List<LikeAction> matchIds) {
        List<Integer> ids = matchIds.stream().map(LikeAction::getToUsr).collect(Collectors.toList());
        List<Photo> photoList = userCardRepository.getIconsByIds(ids);
        List<ChatAffiliation> chatAffiliation = affiliationRepository.getByIdsWithToUsr(ids, id);
        Map<Integer, String> userNames = userRepository.getUserNamesByIds(ids);

        List<UserMatch> userMatches = new LinkedList<>();
        for (LikeAction action : matchIds) {
            int toUsr = action.getToUsr();

            UserMatch match = new UserMatch();
            match.setUserId(toUsr);
            match.setMatchId(action.getId());
            match.setFirstName(userNames.get(toUsr));

            Photo icon = photoList.stream().filter(p -> p.getUserId() == toUsr).findFirst().orElse(null);
            Integer chat = chatAffiliation.stream().filter(c -> c.getFromUsr() == toUsr).findFirst().map(ChatAffiliation::getChatId).orElse(null);

            match.setIcon(icon);
            match.setChatId(chat);
            userMatches.add(match);
        }
        return userMatches;
    }
}
