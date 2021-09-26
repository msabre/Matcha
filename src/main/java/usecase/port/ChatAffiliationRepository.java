package usecase.port;

import domain.entity.ChatAffiliation;
import java.util.List;

public interface ChatAffiliationRepository {
    List<ChatAffiliation> getByUserId(int id);
}
