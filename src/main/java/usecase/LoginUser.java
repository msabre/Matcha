package usecase;

import config.MyProperties;
import domain.entity.User;
import usecase.port.PasswordEncoder;
import usecase.port.UserRepository;

import static java.util.Objects.isNull;

public class LoginUser {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public LoginUser(UserRepository repository,PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String login, String password) {
        User user = repository.findByEmail(login);
        if (isNull(user))
            user = repository.findByUsername(login);
        if (isNull(user))
            return null;

        String userPass = user.getPassword();
        String salt = userPass.substring(userPass.length() - (MyProperties.SALT_BYTES_COUNT + 8), userPass.length());
        String hashPass = passwordEncoder.encrypt(password, salt);

        if (userPass.equals(hashPass))
            return user;

        return null;
    }
}
