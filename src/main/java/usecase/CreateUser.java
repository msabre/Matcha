package usecase;

import domain.entity.User;
import usecase.exception.EmailBusyException;
import usecase.exception.UserNameBusyException;
import usecase.port.PasswordEncoder;
import usecase.port.UserRepository;

public class CreateUser {
    private final UserRepository repository;


    public CreateUser(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
    }

    public int create(User user) throws UserNameBusyException, EmailBusyException {
        return repository.save(user);
    }
}
