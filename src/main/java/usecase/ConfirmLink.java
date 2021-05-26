package usecase;

import usecase.port.UrlRepository;

public class ConfirmLink {
    private final UrlRepository repository;

    public ConfirmLink(UrlRepository repository) {
        this.repository = repository;
    }

    public void markLink(Integer id) {
        repository.markLink(id);
    }
}
