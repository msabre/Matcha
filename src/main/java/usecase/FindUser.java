package usecase;

import domain.entity.User;
import usecase.port.PasswordEncoder;
import usecase.port.UserRepository;

public class FindUser {

    private UserRepository repository;

    public FindUser(UserRepository repository,PasswordEncoder passwordEncoder) {
        this.repository = repository;
    }

    public User findUserById(int id) {
        return repository.findById(id);
    }

    public User findUserByEmail(String email) {
        return repository.findByEmail(email);
    }
}
