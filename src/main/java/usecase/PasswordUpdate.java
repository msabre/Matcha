package usecase;

import usecase.port.UserRepository;

public class PasswordUpdate {
    private final UserRepository repository;

    public PasswordUpdate(UserRepository repository) {
        this.repository = repository;
    }

    public boolean updatePassword(Integer id, String password){
        return repository.passwordUpdate(id, password);
    }
}
