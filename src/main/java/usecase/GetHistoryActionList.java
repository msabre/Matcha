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
        return formHistory(from, actions, false);
    }

    public List<ActionHistory> getNActionsAfterId(Action action, int from, int lastMatchId, int size) {
        List<LikeAction> actions = likesActionRepository.getNFromAfterId(action, from, lastMatchId, size);
        return formHistory(from, actions, false);
    }


    // К пользователю
    public List<ActionHistory> getNtoUser(Action action, int from, int size) {
        List<LikeAction> actions = likesActionRepository.getNTo(action, from, size);
        return formHistory(from, actions, true);
    }

    public List<ActionHistory> getNtoUserAfterId(Action action, int from, int lastMatchId, int size) {
        List<LikeAction> actions = likesActionRepository.getNToAfterId(action, from, lastMatchId, size);
        return formHistory(from, actions, true);
    }


    private List<ActionHistory> formHistory(int id, List<LikeAction> actionIds, boolean toMe) {
        List<Integer> ids = actionIds.stream().map(act -> toMe ? act.getFromUsr() : act.getToUsr()).collect(Collectors.toList());
        List<Photo> photoList = userCardRepository.getIconsByIds(ids);
        List<ChatAffiliation> chatAffiliation = affiliationRepository.getByIdsWithToUsr(ids, id);
        Map<Integer, String> userNames = userRepository.getUserNamesByIds(ids);

        List<ActionHistory> actionHistories = new LinkedList<>();
        for (LikeAction action : actionIds) {
            int userId = toMe ? action.getFromUsr() : action.getToUsr();

            ActionHistory history = new ActionHistory();
            history.setUserId(userId);
            history.setFirstName(userNames.get(userId));
            history.setAction(action.getAction());

            Photo icon = photoList.stream().filter(p -> p.getUserId() == userId).findFirst().orElse(null);
            if (Action.MATCH.equals(action.getAction())) {
                Integer chat = chatAffiliation.stream().filter(c -> c.getFromUsr() == userId).findFirst().map(ChatAffiliation::getChatId).orElse(null);
                history.setChatId(chat);
            }

            history.setIcon(icon);
            actionHistories.add(history);
        }
        return actionHistories;
    }

}
