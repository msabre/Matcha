package usecase;

import domain.entity.Link;
import usecase.port.UrlRepository;

import static java.util.Objects.isNull;

public class CheckLink {
    private final UrlRepository repository;

    public CheckLink(UrlRepository repository) {
        this.repository = repository;
    }

    public boolean isRelevantLink(Integer id, String token) {
        if (id == null)
            return false;

        Link link = repository.getLink(id);

        if (isNull(link) || link.isOpen() || !token.equals(link.getToken()))
            return false;

        return true;
    }
}
