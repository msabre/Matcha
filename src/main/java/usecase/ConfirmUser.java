package usecase;

import usecase.port.UserRepository;

public class ConfirmUser {
    private UserRepository repository;

    public ConfirmUser(UserRepository repository) {
        this.repository = repository;
    }

    public void confirm(Integer id) {
        repository.confirmById(id);
    }
}
