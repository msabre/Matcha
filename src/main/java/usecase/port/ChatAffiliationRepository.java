package usecase.port;

import domain.entity.ChatAffiliation;
import java.util.List;

public interface ChatAffiliationRepository {
    int getChatMaxId();
    boolean create(int fromUsr, int toUsr, int chatId);
    List<ChatAffiliation> getByIdsWithToUsr(List<Integer> ids, int toUsr);
}
