package usecase;

import usecase.port.UrlRepository;

public class CreateLink {
    private final UrlRepository repository;

    public CreateLink(UrlRepository repository) {
        this.repository = repository;
    }

    public Integer addLink(String link) {
        return repository.addLink(link);
    }
}
