package usecase;

import domain.entity.ChatAffiliation;
import domain.entity.LikeAction;
import domain.entity.Photo;
import domain.entity.model.UserInteraction;
import domain.entity.model.types.Action;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class GetInteractionList {
    private final LikesActionRepository likesActionRepository;
    private final UserCardRepository userCardRepository;
    private final ChatAffiliationRepository affiliationRepository;
    private final UserRepository userRepository;

    public GetInteractionList(LikesActionRepository likesActionRepository, UserCardRepository userCardRepository, ChatAffiliationRepository affiliationRepository, UserRepository userRepository) {
        this.likesActionRepository = likesActionRepository;
        this.userCardRepository = userCardRepository;
        this.affiliationRepository = affiliationRepository;
        this.userRepository = userRepository;
    }

    public List<UserInteraction> getN(Action action, int id, int size) {
        List<LikeAction> matchIds = likesActionRepository.getNActionForUserId(action, id, size);
        return formUserMatches(id, matchIds);
    }

    public List<UserInteraction> getNAfterSpecificId(Action action, int id, int lastMatchId, int size) {
        List<LikeAction> matchIds = likesActionRepository.getNActionsUserIdsAfterSpecificId(action, id, lastMatchId, size);
        return formUserMatches(id, matchIds);
    }

    private List<UserInteraction> formUserMatches(int id, List<LikeAction> actionIds) {
        List<Integer> ids = actionIds.stream().map(LikeAction::getToUsr).collect(Collectors.toList());
        List<Photo> photoList = userCardRepository.getIconsByIds(ids);
        List<ChatAffiliation> chatAffiliation = affiliationRepository.getByIdsWithToUsr(ids, id);
        Map<Integer, String> userNames = userRepository.getUserNamesByIds(ids);

        List<UserInteraction> userInteractions = new LinkedList<>();
        for (LikeAction action : actionIds) {
            int toUsr = action.getToUsr();

            UserInteraction match = new UserInteraction();
            match.setUserId(toUsr);
//            match.setMatchId(action.getId());
            match.setFirstName(userNames.get(toUsr));

            Photo icon = photoList.stream().filter(p -> p.getUserId() == toUsr).findFirst().orElse(null);
            if (Action.MATCH.equals(action.getAction())) {
                Integer chat = chatAffiliation.stream().filter(c -> c.getFromUsr() == toUsr).findFirst().map(ChatAffiliation::getChatId).orElse(null);
                match.setChatId(chat);
            }

            match.setIcon(icon);
            userInteractions.add(match);
        }
        return userInteractions;
    }
}
