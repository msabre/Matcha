package usecase;

import domain.entity.ChatAffiliation;
import domain.entity.LikeAction;
import domain.entity.Photo;
import domain.entity.model.ActionHistory;
import domain.entity.model.types.Action;
import usecase.port.ChatAffiliationRepository;
import usecase.port.LikesActionRepository;
import usecase.port.UserCardRepository;
import usecase.port.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class GetHistoryActionList {
    private final LikesActionRepository likesActionRepository;
    private final UserCardRepository userCardRepository;
    private final ChatAffiliationRepository affiliationRepository;
    private final UserRepository userRepository;

    public GetHistoryActionList(LikesActionRepository likesActionRepository, UserCardRepository userCardRepository, ChatAffiliationRepository affiliationRepository, UserRepository userRepository) {
        this.likesActionRepository = likesActionRepository;
        this.userCardRepository = userCardRepository;
        this.affiliationRepository = affiliationRepository;
        this.userRepository = userRepository;
    }

    // От пользователя
    public List<ActionHistory> getNActions(Action action, int from, int size) {
        List<LikeAction> actions = likesActionRepository.getNFrom(action, from, size);
        return formUserMatches(from, actions);
    }

    public List<ActionHistory> getNActionsAfterId(Action action, int from, int lastMatchId, int size) {
        List<LikeAction> actions = likesActionRepository.getNFromAfterId(action, from, lastMatchId, size);
        return formUserMatches(from, actions);
    }


    // К пользователю
    public List<ActionHistory> getNtoUser(Action action, int to, int size) {
        List<LikeAction> actions = likesActionRepository.getNTo(action, to, size);
        return formUserMatches(to, actions);
    }

    public List<ActionHistory> getNtoUserAfterId(Action action, int to, int lastMatchId, int size) {
        List<LikeAction> actions = likesActionRepository.getNToAfterId(action, to, lastMatchId, size);
        return formUserMatches(to, actions);
    }


    private List<ActionHistory> formUserMatches(int id, List<LikeAction> actionIds) {
        List<Integer> ids = actionIds.stream().map(LikeAction::getToUsr).collect(Collectors.toList());
        List<Photo> photoList = userCardRepository.getIconsByIds(ids);
        List<ChatAffiliation> chatAffiliation = affiliationRepository.getByIdsWithToUsr(ids, id);
        Map<Integer, String> userNames = userRepository.getUserNamesByIds(ids);

        List<ActionHistory> actionHistories = new LinkedList<>();
        for (LikeAction action : actionIds) {
            int toUsr = action.getToUsr();

            ActionHistory match = new ActionHistory();
            match.setUserId(toUsr);
            match.setFirstName(userNames.get(toUsr));

            Photo icon = photoList.stream().filter(p -> p.getUserId() == toUsr).findFirst().orElse(null);
            if (Action.MATCH.equals(action.getAction())) {
                Integer chat = chatAffiliation.stream().filter(c -> c.getFromUsr() == toUsr).findFirst().map(ChatAffiliation::getChatId).orElse(null);
                match.setChatId(chat);
            }

            match.setIcon(icon);
            actionHistories.add(match);
        }
        return actionHistories;
    }
}
