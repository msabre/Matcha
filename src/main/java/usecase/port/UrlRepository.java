package usecase.port;

import domain.entity.Link;

public interface UrlRepository {
    Integer addLink(String link);

    Link getLink(Integer id);

    void markLink(Integer id);
}
