package usecase;

import domain.entity.Link;
import usecase.port.UrlRepository;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CheckLink {
    private UrlRepository repository;

    public CheckLink(UrlRepository repository) {
        this.repository = repository;
    }

    public boolean isRevelantLink(Integer id, String url) {
        if (id == null)
            return false;

        Link link = repository.getLink(id);

        if (isNull(link) || link.isOpen() || (nonNull(url) && !(link.getUrl() + id).equals(url)))
            return false;

        return true;
    }
}
