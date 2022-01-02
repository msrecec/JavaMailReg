package com.example.mail.service;

import com.example.mail.dto.UserDto;
import com.example.mail.model.User;
import com.example.mail.model.VerificationToken;

public interface UsersService {

    User register(UserDto user);

    User saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String token);

}
