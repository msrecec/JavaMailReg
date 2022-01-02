package com.example.mail.service;


import com.example.mail.dto.UserDto;
import com.example.mail.model.User;
import com.example.mail.model.VerificationToken;
import com.example.mail.repository.UsersRepositoryJpa;
import com.example.mail.repository.VerificationTokenRepositoryJpa;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UsersService {

    private UsersRepositoryJpa usersRepositoryJpa;

    private VerificationTokenRepositoryJpa verificationTokenRepositoryJpa;

    public UserServiceImpl(UsersRepositoryJpa usersRepositoryJpa, VerificationTokenRepositoryJpa verificationTokenRepositoryJpa) {
        this.usersRepositoryJpa = usersRepositoryJpa;
        this.verificationTokenRepositoryJpa = verificationTokenRepositoryJpa;
    }

    @Override
    public User register(UserDto user) {

        User newUser = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(false)
                .build();

        return usersRepositoryJpa.save(newUser);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .expiryDate(VerificationToken.calculateExpiryDate(VerificationToken.EXPIRATION))
                .user(user)
                .build();

        verificationTokenRepositoryJpa.save(verificationToken);

    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepositoryJpa.findByToken(token);
    }

    @Override
    public User saveRegisteredUser(User user) {

        user.setEnabled(true);

        return usersRepositoryJpa.save(user);
    }
}
