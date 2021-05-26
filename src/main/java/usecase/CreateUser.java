package usecase;

import domain.entity.User;
import usecase.port.PasswordEncoder;
import usecase.port.UserRepository;

public class CreateUser {
    private UserRepository repository;


    public CreateUser(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
    }

    public int create(User user) {
        return repository.save(user);
    }
}
